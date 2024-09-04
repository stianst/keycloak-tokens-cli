package org.keycloak.cli.assertion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.util.JSONObjectUtils;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import org.junit.jupiter.api.Assertions;

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

    public static UserInfo assertUserInfoResponse(String jsonString) {
        UserInfo userInfo = null;
        try {
            userInfo = new UserInfo(JSONObjectUtils.parse(jsonString));
            Assertions.assertNotNull(userInfo.getSubject());
            Assertions.assertNotNull(userInfo.getPreferredUsername());
        } catch (ParseException e) {
            Assertions.fail("Failed to parse userinfo", e);
        }
        return userInfo;
    }


    public static UserInfo assertTokenIntrospectionResponse(String jsonString) {
        UserInfo userInfo = null;
        try {
            userInfo = new UserInfo(JSONObjectUtils.parse(jsonString));
            Assertions.assertTrue(userInfo.getClaim("active", Boolean.class));
            Assertions.assertNotNull(userInfo.getSubject());
            Assertions.assertNotNull(userInfo.getPreferredUsername());
        } catch (ParseException e) {
            Assertions.fail("Failed to parse userinfo", e);
        }
        return userInfo;
    }

}
