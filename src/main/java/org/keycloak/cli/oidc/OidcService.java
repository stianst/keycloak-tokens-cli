package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.AuthorizationGrant;
import com.nimbusds.oauth2.sdk.ClientCredentialsGrant;
import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.RefreshTokenGrant;
import com.nimbusds.oauth2.sdk.ResourceOwnerPasswordCredentialsGrant;
import com.nimbusds.oauth2.sdk.Scope;
import com.nimbusds.oauth2.sdk.TokenIntrospectionRequest;
import com.nimbusds.oauth2.sdk.TokenRequest;
import com.nimbusds.oauth2.sdk.TokenRevocationRequest;
import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.tokenexchange.TokenExchangeGrant;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

@ApplicationScoped
public class OidcService {

    @Inject
    ConfigService config;

    @Inject
    NimbusApacheHttpClient httpClient;

    @Inject
    AuthorizationCodeService authorizationCodeService;

    @Inject
    DeviceAuthorizationService deviceAuthorizationService;

    private Context context;
    private OIDCProviderMetadata providerMetadata;

    @PostConstruct
    public void init() {
        context = config.getContext();
    }

    public OIDCProviderMetadata providerMetadata() {
        if (providerMetadata == null) {
            OIDCProviderConfigurationRequest oidcProviderConfigurationRequest = new OIDCProviderConfigurationRequest(context.getIssuer());
            providerMetadata = send(oidcProviderConfigurationRequest.toHTTPRequest(), OIDCProviderMetadata.class);
        }
        return providerMetadata;
    }

    public String keys() {
        return keys(providerMetadata().getJWKSetURI());
    }

    public String keys(URI uri) {
        HTTPRequest httpRequest = new HTTPRequest(HTTPRequest.Method.GET, uri);
        return send(httpRequest, String.class);
    }

    public String userInfo(String accessToken) {
        UserInfoRequest request = new UserInfoRequest(providerMetadata().getUserInfoEndpointURI(), new BearerAccessToken(accessToken));
        return send(request.toHTTPRequest(), String.class);
    }

    public Tokens token(Set<String> scope) {
        try {
            return switch (context.getFlow()) {
                case CLIENT -> clientGrant(scope);
                case PASSWORD -> passwordGrant(scope);
                case DEVICE -> deviceAuthorizationService.getToken(scope);
                case BROWSER -> authorizationCodeService.getToken(scope);
            };
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String introspect(String token) {
        ClientAuthentication clientAuthentication = context.getClientAuthentication();
        TokenIntrospectionRequest request = new TokenIntrospectionRequest(providerMetadata().getIntrospectionEndpointURI(), clientAuthentication, new BearerAccessToken(token));
        return send(request.toHTTPRequest(), String.class);
    }

    public boolean revoke(String token) {
        ClientAuthentication clientAuthentication = context.getClientAuthentication();
        TokenRevocationRequest tokenRevocationRequest;
        if (clientAuthentication != null) {
            tokenRevocationRequest = new TokenRevocationRequest(providerMetadata().getRevocationEndpointURI(), context.getClientAuthentication(), new BearerAccessToken(token));
        } else {
            tokenRevocationRequest = new TokenRevocationRequest(providerMetadata().getRevocationEndpointURI(), context.getClientId(), new BearerAccessToken(token));
        }
        return send(tokenRevocationRequest.toHTTPRequest(), Boolean.class);
    }

    public String exchange(String subjectToken, Set<String> audience, Set<String> scope) {
        List<Audience> audiences = audience != null ? audience.stream().map(Audience::new).toList() : null;
        TokenExchangeGrant tokenExchangeGrant = new TokenExchangeGrant(new BearerAccessToken(subjectToken), TokenTypeURI.ACCESS_TOKEN, null, null, null, audiences);
        try {
            return tokenRequest(tokenExchangeGrant, scope).getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Tokens passwordGrant(Set<String> scope) {
        return tokenRequest(new ResourceOwnerPasswordCredentialsGrant(context.getUsername(), new Secret(context.getUserPassword())), scope);
    }

    private Tokens clientGrant(Set<String> scope) {
        return tokenRequest(new ClientCredentialsGrant(), scope);
    }

    public Tokens refresh(String refreshToken, Set<String> requestScope) {
        return tokenRequest(new RefreshTokenGrant(new RefreshToken(refreshToken)), requestScope);
    }

    public Tokens tokenRequest(AuthorizationGrant grant, Set<String> requestScope) {
        ClientAuthentication clientAuthentication = context.getClientAuthentication();
        TokenRequest tokenRequest;
        if (clientAuthentication != null) {
            tokenRequest = new TokenRequest(providerMetadata().getTokenEndpointURI(), context.getClientAuthentication(), grant, toScope(requestScope));
        } else {
            tokenRequest = new TokenRequest(providerMetadata().getTokenEndpointURI(), context.getClientId(), grant, toScope(requestScope));
        }
        OIDCTokens tokens = send(tokenRequest.toHTTPRequest(), OIDCTokens.class);
        return new Tokens(tokens);
    }

    @SuppressWarnings("unchecked")
    protected <T> T send(HTTPRequest httpRequest, Class<T> responseClazz) {
        HTTPResponse httpResponse;
        try {
            httpResponse = httpRequest.send(httpClient);
        } catch (IOException e) {
            throw new OidcException(httpRequest, e.getMessage());
        }

        boolean success = httpResponse.indicatesSuccess();

        try {
            Object response = ResponseConverter.convert(httpResponse, responseClazz);

            if (success) {
                return (T) response;
            } else if (response != null) {
                ErrorResponse errorResponse = (ErrorResponse) response;
                throw new OidcException(httpRequest, errorResponse);
            } else {
                throw new OidcException(httpRequest, httpResponse.getStatusCode());
            }
        } catch (ParseException e) {
            throw new OidcException(httpRequest, e.getMessage());
        }
    }

    protected Scope toScope(Set<String> scope) {
        return scope != null ? new Scope(scope.toArray(new String[0])) : null;
    }

}
