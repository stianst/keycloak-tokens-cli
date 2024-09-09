package org.keycloak.cli.config;

import com.nimbusds.oauth2.sdk.auth.ClientAuthentication;
import com.nimbusds.oauth2.sdk.auth.ClientSecretBasic;
import com.nimbusds.oauth2.sdk.auth.Secret;
import com.nimbusds.oauth2.sdk.id.ClientID;
import com.nimbusds.oauth2.sdk.id.Issuer;
import org.keycloak.cli.enums.Flow;

import java.util.Set;

public class Context {

    private boolean storeTokens;
    private final Config.Context config;
    private final String issuerUrl;

    public Context(Boolean storeTokens, Config.Context config, String issuerUrl) {
        this.storeTokens = storeTokens != null ? storeTokens : false;
        this.config = config;
        this.issuerUrl = issuerUrl;
    }

    public boolean storeTokens() {
        return storeTokens;
    }

    public Issuer getIssuer() {
        return new Issuer(issuerUrl);
    }

    public Flow getFlow() {
        return config.flow();
    }

    public ClientAuthentication getClientAuthentication() {
        Secret clientSecret = getClientSecret();
        return clientSecret != null ? new ClientSecretBasic(getClientId(), clientSecret) : null;
    }

    public ClientID getClientId() {
        return new ClientID(config.client().clientId());
    }

    public Secret getClientSecret() {
        return config.client().secret() != null ? new Secret(config.client().secret()) : null;
    }

    public String getUsername() {
        return config.user().username();
    }

    public String getUserPassword() {
        return config.user().password();
    }

    public Set<String> getScope() {
        return config.scope();
    }

}
