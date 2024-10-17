package org.keycloak.cli.commands.config;

import jakarta.inject.Inject;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.interact.InteractService;
import picocli.CommandLine;

@CommandLine.Command(name = "update", description = "Update config", mixinStandardHelpOptions = true)
public class ConfigUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"--default-context"}, description = "Set default context")
    String defaultContext;

    @CommandLine.Option(names = {"--store-tokens"}, description = "Store tokens")
    Boolean storeTokens;

    @CommandLine.Option(names = {"--truststore-path"}, description = "Path to truststore (supports PKCS12 and Java KeyStore formats", arity = "0..1")
    String truststorePath;

    @CommandLine.Option(names = {"--truststore-password"}, description = "Path to truststore password", arity = "0..1")
    String truststorePassword;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config old = configService.loadConfig();
        if (storeTokens == null) {
            storeTokens = old.getStoreTokens();
        }
        if (defaultContext == null) {
            defaultContext = old.getDefaultContext();
        }
        if (truststorePath == null) {
            truststorePath = old.getTruststore() != null ? old.getTruststore().getPath() : null;
        }
        if (truststorePassword == null) {
            truststorePassword = old.getTruststore() != null ? old.getTruststore().getPassword() : null;
        }

        Config config = new Config(defaultContext, storeTokens, old.getIssuers(), new Config.Truststore(truststorePath, truststorePassword));
        configService.saveConfig(config);
        interact.println("Config updated");
    }

}