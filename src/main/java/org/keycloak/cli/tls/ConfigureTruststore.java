package org.keycloak.cli.tls;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.util.Optional;

@ApplicationScoped
public class ConfigureTruststore {

    static final Logger logger = Logger.getLogger(ConfigureTruststore.class);

    @ConfigProperty(name = "kct.truststore")
    Optional<File> truststore;

    @ConfigProperty(name = "kct.truststore.password")
    Optional<String> truststorePassword;

    void onStart(@Observes StartupEvent event) {
        if (truststore.isPresent()) {
            System.setProperty("javax.net.ssl.trustStore", truststore.get().getAbsolutePath());
            if (truststorePassword.isPresent()) {
                System.setProperty("javax.net.ssl.trustStorePassword", truststorePassword.get());
                logger.debug("Configured truststore with password");
            } else {
                logger.debug("Configured truststore without password");
            }
        } else {
            logger.debug("Truststore not configured");
        }
    }

}
