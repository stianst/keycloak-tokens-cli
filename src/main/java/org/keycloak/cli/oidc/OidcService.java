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
import com.nimbusds.oauth2.sdk.client.ClientDeleteRequest;
import com.nimbusds.oauth2.sdk.client.ClientReadRequest;
import com.nimbusds.oauth2.sdk.client.ClientUpdateRequest;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.RefreshToken;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.tokenexchange.TokenExchangeGrant;
import com.nimbusds.openid.connect.sdk.UserInfoRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderConfigurationRequest;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationRequest;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientUpdateRequest;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Context;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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

    public OIDCClientInformation registerClient(String token, OIDCClientMetadata clientMetadata) {
        BearerAccessToken accessToken = new BearerAccessToken(token);
        OIDCClientRegistrationRequest request = new OIDCClientRegistrationRequest(providerMetadata().getRegistrationEndpointURI(), clientMetadata, accessToken);
        return send(request.toHTTPRequest(), OIDCClientInformation.class);
    }

    public OIDCClientInformation queryClient(String registrationToken, String registrationUrl) {
        try {
            ClientReadRequest clientReadRequest = new ClientReadRequest(new URI(registrationUrl), new BearerAccessToken(registrationToken));
            return send(clientReadRequest.toHTTPRequest(), OIDCClientInformation.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public OIDCClientInformation updateClient(OIDCClientInformation clientInformation, OIDCClientMetadata clientMetadata) {
        ClientUpdateRequest clientUpdateRequest = new ClientUpdateRequest(
                clientInformation.getRegistrationURI(),
                clientInformation.getID(),
                clientInformation.getRegistrationAccessToken(),
                clientMetadata,
                clientInformation.getSecret());
        return send(clientUpdateRequest.toHTTPRequest(), OIDCClientInformation.class);
    }

    public boolean deleteClient(String registrationToken, String registrationUrl) {
        try {
            ClientDeleteRequest clientDeleteRequest = new ClientDeleteRequest(new URI(registrationUrl), new BearerAccessToken(registrationToken));
            return send(clientDeleteRequest.toHTTPRequest(), Boolean.class);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
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

    public String exchange(Set<String> audience, Set<String> scope, String requestedTokenType, String subjectToken, String subjectTokenType, String actorToken, String actorTokenType, Map<String, String> customParams) {
        try {
            TokenTypeURI requestedTokenTypeWrapper = requestedTokenType != null ? TokenTypeURI.parse(requestedTokenType) : null;
            List<Audience> audienceWrapper = audience != null ? audience.stream().map(Audience::new).toList() : null;
            BearerAccessToken subjectTokenWrapper = new BearerAccessToken(subjectToken);
            TokenTypeURI subjectTokenTypeWrapper = TokenTypeURI.parse(subjectTokenType);
            BearerAccessToken actorTokenWrapper = actorToken != null ? new BearerAccessToken(actorToken) : null;
            TokenTypeURI actorTokenTypeWrapper = actorTokenType != null ? TokenTypeURI.parse(actorTokenType) : null;

            TokenExchangeGrant tokenExchangeGrant = new TokenExchangeGrant(
                    subjectTokenWrapper,
                    subjectTokenTypeWrapper,
                    actorTokenWrapper,
                    actorTokenTypeWrapper,
                    requestedTokenTypeWrapper,
                    audienceWrapper);
            return tokenRequest(tokenExchangeGrant, scope, customParams).getAccessToken();
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
        return tokenRequest(grant, requestScope, null);
    }

    public Tokens tokenRequest(AuthorizationGrant grant, Set<String> requestScope, Map<String, String> customParams) {
        TokenRequest.Builder builder;
        ClientAuthentication clientAuthentication = context.getClientAuthentication();
        if (clientAuthentication != null) {
            builder = new TokenRequest.Builder(providerMetadata().getTokenEndpointURI(), context.getClientAuthentication(), grant);
        } else {
            builder = new TokenRequest.Builder(providerMetadata().getTokenEndpointURI(), context.getClientId(), grant);
        }
        builder.scope(toScope(requestScope));

        if (customParams != null && !customParams.isEmpty()) {
            for (Map.Entry<String, String> e : customParams.entrySet()) {
                builder.customParameter(e.getKey(), e.getValue());
            }
        }

        OIDCTokens tokens = send(builder.build().toHTTPRequest(), OIDCTokens.class);
        return new Tokens(tokens);
    }

    protected <T> T send(HTTPRequest httpRequest, Class<T> responseClazz) {
        HTTPResponse httpResponse;
        try {
            httpResponse = httpRequest.send(httpClient);
        } catch (IOException e) {
            throw new OidcException(httpRequest, e);
        }

        ResponseConverter<T> responseConverter = new ResponseConverter<>(httpRequest, httpResponse, responseClazz);
        return responseConverter.convert();
    }

    public static Scope toScope(Set<String> scope) {
        return scope != null ? new Scope(scope.toArray(new String[0])) : null;
    }

}
