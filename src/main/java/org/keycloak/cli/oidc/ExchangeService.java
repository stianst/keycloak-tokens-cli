package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.id.Audience;
import com.nimbusds.oauth2.sdk.token.BearerAccessToken;
import com.nimbusds.oauth2.sdk.token.TokenTypeURI;
import com.nimbusds.oauth2.sdk.tokenexchange.TokenExchangeGrant;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.Set;

@ApplicationScoped
public class ExchangeService {

    @Inject
    OidcService oidcService;

    public String getExchange(String accessToken, List<String> audience, Set<String> scope) {
        TokenExchangeGrant tokenExchangeGrant = new TokenExchangeGrant(new BearerAccessToken(accessToken), TokenTypeURI.ACCESS_TOKEN, null, null, null, audience.stream().map(Audience::new).toList());
        try {
            return oidcService.tokenRequest(tokenExchangeGrant, scope, scope).getAccessToken();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
