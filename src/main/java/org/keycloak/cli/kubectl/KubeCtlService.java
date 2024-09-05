package org.keycloak.cli.kubectl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.utils.JsonFormatter;

@ApplicationScoped
public class KubeCtlService {

    @Inject
    JsonFormatter jsonFormatter;

    public boolean isKubeExecContext() {
        return System.getenv().containsKey("KUBERNETES_EXEC_INFO");
    }

    public String wrapToken(String token) {
        ExecCredentialRepresentation execCredential = new ExecCredentialRepresentation();
        execCredential.getStatus().setToken(token);
        return jsonFormatter.toPrettyJson(execCredential);
    }

}
