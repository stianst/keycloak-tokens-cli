package org.keycloak.cli.oidc;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public interface TokenResource {

    @POST
    TokenResponse device(
            @FormParam("grant_type") String grant_type,
            @FormParam("device_code") String device_code,
            @FormParam("client_id") String client_id);

}
