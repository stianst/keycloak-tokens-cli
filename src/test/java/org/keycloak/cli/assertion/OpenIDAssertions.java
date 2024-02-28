package org.keycloak.cli.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.oidc.UserInfo;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class OpenIDAssertions {

    public static JsonNode assertEncodedToken(String token) {
        JsonNode jsonNode = null;
        try {
            String[] output = token.split("\\.");
            Assertions.assertEquals(3, output.length, "invalid number of parts");
            String decoded = new String(Base64.getUrlDecoder().decode(output[1]), StandardCharsets.UTF_8);
            jsonNode = assertDecodedToken(decoded);
        } catch (Throwable t) {
            Assertions.fail("Failed to parse token", t);
        }
        return jsonNode;
    }

    public static JsonNode assertDecodedToken(String jsonString) {
        JsonNode jsonNode = null;
        try {
            jsonNode = new ObjectMapper().readValue(jsonString, JsonNode.class);
            Assertions.assertNotNull(jsonNode.get("exp"), "exp claim missing");
        } catch (Throwable t) {
            Assertions.fail("Failed to parse token", t);
        }
        return jsonNode;
    }

    public static UserInfo assertUserInfoResponse(String jsonString) throws JsonProcessingException {
        UserInfo userInfo = new ObjectMapper().readValue(jsonString, UserInfo.class);
        Assertions.assertNotNull(userInfo.getSub());
        Assertions.assertNotNull(userInfo.getSub());
        Assertions.assertNotNull(userInfo.getPreferredUsername());
        return userInfo;
    }

}
