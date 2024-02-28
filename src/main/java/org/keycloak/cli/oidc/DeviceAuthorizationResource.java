package org.keycloak.cli.oidc;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public interface DeviceAuthorizationResource {

    @POST
    DeviceAuthorizationResponse request(@FormParam("client_id") String clientId, @FormParam("scope") String scope);

}
