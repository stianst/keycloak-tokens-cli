package org.keycloak.cli.commands.config;

import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientMetadata;
import jakarta.inject.Inject;
import org.keycloak.cli.commands.converter.CommaSeparatedListConverter;
import org.keycloak.cli.commands.converter.FlowConverter;
import org.keycloak.cli.config.Config;
import org.keycloak.cli.config.ConfigException;
import org.keycloak.cli.config.ConfigService;
import org.keycloak.cli.config.Messages;
import org.keycloak.cli.enums.Flow;
import org.keycloak.cli.enums.TokenType;
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OIDCClientMetadataGenerator;
import org.keycloak.cli.oidc.OidcService;
import org.keycloak.cli.tokens.TokenManagerService;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Set;

@CommandLine.Command(name = "create", description = "Create config context", mixinStandardHelpOptions = true)
public class ContextCreateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to create", required = true)
    String contextId;

    @CommandLine.Option(names = {"--iss"}, required = true)
    String iss;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class, required = true)
    Flow flow;

    @CommandLine.Option(names = {"--scope"}, converter = CommaSeparatedListConverter.class)
    Set<String> scope;

    @CommandLine.Option(names = {"--client"})
    String client;

    @CommandLine.Option(names = {"--client-secret"})
    String clientSecret;

    @CommandLine.Option(names = {"--user"})
    String user;

    @CommandLine.Option(names = {"--user-password"})
    String userPassword;

    @CommandLine.Option(names = {"--create-client"})
    boolean createClient;

    @Inject
    ConfigService configService;

    @Inject
    OidcService oidcService;

    @Inject
    TokenManagerService tokens;

    @Inject
    InteractService interact;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        Config.Issuer issuer;

        if (config.findContext(contextId) != null) {
            throw ConfigException.exists(Messages.Type.CONTEXT, contextId);
        }

        if (iss.startsWith("http://") || iss.startsWith("https://")) {
            issuer = config.findIssuerByUrl(iss);
            if (issuer == null) {
                issuer = new Config.Issuer(iss, new HashMap<>(), null);
                iss = iss.substring(iss.indexOf('/') + 1).replace(":", "-").replace('/', '-');
                config.getIssuers().put(iss, issuer);
            }
        } else {
            issuer = config.getIssuers().get(iss);
            if (issuer == null) {
                throw ConfigException.notFound(Messages.Type.ISSUER, iss);
            }
        }

        String registrationToken = null;
        String registrationUrl = null;
        if (createClient) {
            configService.setCurrentContext(issuer.getClientRegistrationContext());
            String token = tokens.getToken(TokenType.ACCESS, null, false);

            OIDCClientMetadata clientMetadata = OIDCClientMetadataGenerator.generate(flow, scope);
            OIDCClientInformation oidcClientInformation = oidcService.registerClient(token, clientMetadata);

            client = oidcClientInformation.getID().getValue();
            clientSecret = oidcClientInformation.getSecret().getValue();
            registrationToken = oidcClientInformation.getRegistrationAccessToken().getValue();
            registrationUrl = oidcClientInformation.getRegistrationURI().toString();
        }

        issuer.getContexts().put(contextId, new Config.Context(
                flow,
                client != null ? new Config.Client(client, clientSecret, registrationToken, registrationUrl) : null,
                user != null ? new Config.User(user, userPassword) : null,
                scope
        ));

        configService.saveConfig(config);
        interact.printCreated(Messages.Type.CONTEXT, contextId);
    }

}
