package org.keycloak.cli.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.test.junit.main.LaunchResult;
import org.junit.jupiter.api.AssertionFailureBuilder;
import org.junit.jupiter.api.Assertions;
import org.keycloak.cli.ConfigTestProfile;

public class LauncherAssertions {

    public static void assertSuccess(LaunchResult result) {
        if (result.exitCode() != 0) {
            AssertionFailureBuilder.assertionFailure().message("Exit code " + result.exitCode()).reason(result.getErrorOutput()).buildAndThrow();
        }
    }

    public static void assertSuccess(LaunchResult result, String expectedOutput) {
        assertSuccess(result);
        Assertions.assertEquals(expectedOutput, result.getOutput());
    }

    public static void assertYamlOutput(LaunchResult result, String header, Object value) {
        String expectedOutput;
        try {
            expectedOutput = ConfigTestProfile.OBJECT_MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (header != null) {
            expectedOutput = header + "\n" + expectedOutput;
        }
        assertSuccess(result);
        Assertions.assertEquals(expectedOutput, result.getOutput() + "\n");
    }

}
