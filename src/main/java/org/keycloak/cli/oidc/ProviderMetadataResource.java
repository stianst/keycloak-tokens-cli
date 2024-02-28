package org.keycloak.cli.oidc;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/.well-known/openid-configuration")
public interface ProviderMetadataResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    ProviderMetadata getProviderMetadata();

}
