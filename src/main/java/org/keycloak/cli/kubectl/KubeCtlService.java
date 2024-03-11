package org.keycloak.cli.kubectl;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.keycloak.cli.utils.PrettyPrinterService;

@ApplicationScoped
public class KubeCtlService {

    @Inject
    PrettyPrinterService prettyPrinter;

    public boolean isKubeExecContext() {
        return System.getenv().containsKey("KUBERNETES_EXEC_INFO");
    }

    public String wrapToken(String token) {
        ExecCredentialRepresentation execCredential = new ExecCredentialRepresentation();
        execCredential.getStatus().setToken(token);
        return prettyPrinter.prettyPrint(execCredential);
    }

}
