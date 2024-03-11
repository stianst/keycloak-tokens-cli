package org.keycloak.cli.assertion;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.kubectl.ExecCredentialRepresentation;

public class KubeCtlAssertions {

    public static ExecCredentialRepresentation assertExecCredential(String output) {
        ExecCredentialRepresentation execCredential = null;
        try {
            execCredential = new ObjectMapper().readValue(output, ExecCredentialRepresentation.class);
        } catch (Throwable t) {
            Assertions.fail("Failed to parse token", t);
        }
        return execCredential;
    }

}
