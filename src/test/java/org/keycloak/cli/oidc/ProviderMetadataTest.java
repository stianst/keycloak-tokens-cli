package org.keycloak.cli.oidc;

import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.cli.ConfigTestProfile;
import org.keycloak.cli.tokens.TokenStoreService;

@QuarkusTest
@ExtendWith({ConfigTestProfile.class})
public class ProviderMetadataTest {

    @Inject
    OidcService oidcService;

    @Inject
    TokenStoreService tokenStoreService;

    @Test
    public void testProviderMetadataCache() {
        OIDCProviderMetadata oidcProviderMetadata = oidcService.providerMetadata();
        Assertions.assertNotNull(oidcProviderMetadata);

        OIDCProviderMetadata oidcProviderMetadata1 = tokenStoreService.getProviderMetadata();
        Assertions.assertNotNull(oidcProviderMetadata1);
    }

}
