package org.keycloak.cli.oidc;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("")
public interface ExchangeResource {

    @POST
    TokenResponse exchange(
            @FormParam("client_id") String client_id,
            @FormParam("client_secret") String client_secret,
            @FormParam("grant_type") String grant_type,
            @FormParam("subject_token") String subject_token,
            @FormParam("audience") String audience);

}
