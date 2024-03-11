package org.keycloak.cli.kubectl;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.quarkus.runtime.annotations.RegisterForReflection;

/**
 * See https://kubernetes.io/docs/reference/config-api/client-authentication.v1/
 */
@RegisterForReflection
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExecCredentialRepresentation {

    private String kind = "ExecCredential";
    private String apiVersion = "client.authentication.k8s.io/v1";
    private Spec spec = new Spec();
    private Status status = new Status();

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getApiVersion() {
        return apiVersion;
    }

    public void setApiVersion(String apiVersion) {
        this.apiVersion = apiVersion;
    }

    public Spec getSpec() {
        return spec;
    }

    public void setSpec(Spec spec) {
        this.spec = spec;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @RegisterForReflection
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Spec {
        private boolean interactive = true;

        public boolean isInteractive() {
            return interactive;
        }

        public void setInteractive(boolean interactive) {
            this.interactive = interactive;
        }
    }

    @RegisterForReflection
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public static class Status {

        private String expirationTimestamp;
        private String token;

        public String getExpirationTimestamp() {
            return expirationTimestamp;
        }

        public void setExpirationTimestamp(String expirationTimestamp) {
            this.expirationTimestamp = expirationTimestamp;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

}
