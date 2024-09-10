package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.GrantType;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import org.keycloak.cli.enums.Flow;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Set;

public class OIDCClientMetadataGenerator {

    public static OIDCClientMetadata generate(Flow flow, Set<String> scope) {
        OIDCClientMetadata clientMetadata = new OIDCClientMetadata();
        clientMetadata.setScope(OidcService.toScope(scope));
        clientMetadata.setGrantTypes(Collections.singleton(getGrantType(flow)));

        if (flow.equals(Flow.BROWSER)) {
            clientMetadata.setRedirectionURI(getLocalhostRedirect());
        }

        return clientMetadata;
    }

    private static GrantType getGrantType(Flow flow) {
        return switch (flow) {
            case BROWSER -> GrantType.AUTHORIZATION_CODE;
            case DEVICE -> GrantType.DEVICE_CODE;
            case CLIENT -> GrantType.CLIENT_CREDENTIALS;
            case PASSWORD -> GrantType.PASSWORD;
        };
    }

    private static URI getLocalhostRedirect() {
        try {
            return new URI("http://127.0.0.1");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
