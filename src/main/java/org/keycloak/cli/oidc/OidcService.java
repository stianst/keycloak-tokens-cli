package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenRevocationRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;

import java.io.IOException;
import java.util.Set;

@ApplicationScoped
public class OidcService {

    @Inject
    ConfigService config;

    @Inject
    NimbusApacheHttpClient httpClient;

    private Context context;
    private OIDCProviderMetadata providerMetadata;

    @PostConstruct
    public void init() {
        context = config.getContext();
    }

    public OIDCProviderMetadata providerMetadata() {
        try {
            if (providerMetadata == null) {
                OIDCProviderConfigurationRequest oidcProviderConfigurationRequest = new OIDCProviderConfigurationRequest(context.getIssuer());
                HTTPResponse httpResponse = oidcProviderConfigurationRequest.toHTTPRequest().send(httpClient);
                if (!httpResponse.indicatesSuccess()) {
                    throw new RuntimeException("Failed to retrieve provider metadata " + httpResponse.getStatusCode());
                }
                providerMetadata = OIDCProviderMetadata.parse(httpResponse.getBodyAsJSONObject());
            }
            return providerMetadata;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UserInfo userInfo(String accessToken) {
        try {
            HTTPResponse httpResponse = new UserInfoRequest(providerMetadata().getUserInfoEndpointURI(), new BearerAccessToken(accessToken))
                    .toHTTPRequest()
                    .send(httpClient);
            UserInfoResponse userInfoResponse = UserInfoResponse.parse(httpResponse);
            if (userInfoResponse.indicatesSuccess()) {
                return userInfoResponse.toSuccessResponse().getUserInfo();
            } else {
                throw new RuntimeException(userInfoResponse.toErrorResponse().getErrorObject().toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Tokens token(Set<String> scope) {
        try {
            return switch (context.getFlow()) {
                case CLIENT -> clientGrant(scope);
                case PASSWORD -> passwordGrant(scope);
                default -> null;
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean revoke(String token) {
        try {
            ClientAuthentication clientAuthentication = context.getClientAuthentication();
            TokenRevocationRequest tokenRevocationRequest;
            if (clientAuthentication != null) {
                tokenRevocationRequest = new TokenRevocationRequest(providerMetadata().getRevocationEndpointURI(), context.getClientAuthentication(), new BearerAccessToken(token));
            } else {
                tokenRevocationRequest = new TokenRevocationRequest(providerMetadata().getRevocationEndpointURI(), context.getClientId(), new BearerAccessToken(token));
            }
            HTTPResponse httpResponse = tokenRevocationRequest.toHTTPRequest().send(httpClient);
            return httpResponse.indicatesSuccess();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Tokens passwordGrant(Set<String> scope) throws IOException, ParseException {
        return tokenRequest(new ResourceOwnerPasswordCredentialsGrant(context.getUsername(), new Secret(context.getUserPassword())), scope, scope);
    }

    private Tokens clientGrant(Set<String> scope) throws IOException, ParseException {
        return tokenRequest(new ClientCredentialsGrant(), scope, scope);
    }

    public Tokens refresh(String refreshToken, Set<String> refreshScope, Set<String> requestScope) throws IOException, ParseException {
        return tokenRequest(new RefreshTokenGrant(new RefreshToken(refreshToken)), refreshScope, requestScope);
    }

    public Tokens tokenRequest(AuthorizationGrant grant, Set<String> refreshScope, Set<String> requestScope) throws IOException, ParseException {
        ClientAuthentication clientAuthentication = context.getClientAuthentication();
        Scope s = new Scope(requestScope.toArray(new String[0]));
        TokenRequest tokenRequest;
        if (clientAuthentication != null) {
            tokenRequest = new TokenRequest(providerMetadata().getTokenEndpointURI(), context.getClientAuthentication(), grant, s);
        } else {
            tokenRequest = new TokenRequest(providerMetadata().getTokenEndpointURI(), context.getClientId(), grant, s);
        }
        OIDCTokenResponse tokenResponse = OIDCTokenResponse.parse(tokenRequest.toHTTPRequest().send(httpClient));
        if (tokenResponse.indicatesSuccess()) {
            return new Tokens(tokenResponse.toSuccessResponse(), refreshScope, requestScope);
        } else {
            throw new RuntimeException("Failed to send token request: " + tokenResponse.toErrorResponse().getErrorObject().toString());
        }
    }

}
