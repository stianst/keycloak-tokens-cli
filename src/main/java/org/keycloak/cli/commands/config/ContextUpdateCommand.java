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
import org.keycloak.cli.interact.InteractService;
import org.keycloak.cli.oidc.OidcService;
import picocli.CommandLine;

import java.util.Set;

@CommandLine.Command(name = "update", description = "Update config context", mixinStandardHelpOptions = true)
public class ContextUpdateCommand implements Runnable {

    @CommandLine.Option(names = {"-c", "--context"}, description = "Context to update")
    String contextId;

    @CommandLine.Option(names = {"--iss"}, arity = "0..1")
    String iss;

    @CommandLine.Option(names = {"--flow"}, converter = FlowConverter.class)
    Flow flow;

    @CommandLine.Option(names = {"--scope"}, converter = CommaSeparatedListConverter.class, arity = "0..1")
    Set<String> scope;

    @CommandLine.Option(names = {"--client"})
    String client;

    @CommandLine.Option(names = {"--client-secret"}, arity = "0..1")
    String clientSecret;

    @CommandLine.Option(names = {"--user"}, arity = "0..1")
    String user;

    @CommandLine.Option(names = {"--user-password"}, arity = "0..1")
    String userPassword;

    @Inject
    ConfigService configService;

    @Inject
    InteractService interact;

    @Inject
    OidcService oidcService;

    @Override
    public void run() {
        Config config = configService.loadConfig();
        if (contextId == null) {
            contextId = config.getDefaultContext();
        }

        Config.Issuer issuer = config.getIssuers().values().stream().filter(i -> i.getContexts().containsKey(contextId)).findFirst().orElse(null);
        if (issuer == null) {
            throw ConfigException.notFound(Messages.Type.CONTEXT, contextId);
        }
        Config.Context context = issuer.getContexts().get(contextId);

        if (flow == null) {
            flow = context.getFlow();
        }
        if (scope == null) {
            scope = context.getScope();
        }
        if (client == null) {
            client = context.getClient() != null ? context.getClient().getClientId() : null;
        }
        if (clientSecret == null) {
            clientSecret = context.getClient() != null ? context.getClient().getSecret() : null;
        }
        if (user == null) {
            user = context.getUser() != null ? context.getUser().getUsername() : null;
        }
        if (userPassword != null) {
            userPassword = context.getUser() != null ? context.getUser().getPassword() : null;
        }

        Config.Context removed = issuer.getContexts().remove(contextId);

        if (iss != null) {
            issuer = config.getIssuers().get(iss);
        }

        Config.Client client = removed.getClient();
        String registrationUrl = null;
        String registrationToken = null;
        if (client != null && client.getRegistrationUrl() != null && client.getRegistrationToken() != null) {
            configService.setCurrentContext(issuer.getClientRegistrationContext());
            OIDCClientInformation oidcClientInformation = oidcService.queryClient(client.getRegistrationToken(), client.getRegistrationUrl());

            OIDCClientMetadata oidcMetadata = oidcClientInformation.getOIDCMetadata();
            oidcMetadata.setScope(OidcService.toScope(scope));

            oidcClientInformation = oidcService.updateClient(oidcClientInformation, oidcClientInformation.getOIDCMetadata());

            registrationUrl = oidcClientInformation.getRegistrationURI().toString();
            registrationToken = oidcClientInformation.getRegistrationAccessToken().getValue();
        }

        issuer.getContexts().put(contextId, new Config.Context(
                flow,
                this.client != null ? new Config.Client(this.client, clientSecret, registrationToken, registrationUrl) : null,
                user != null ? new Config.User(user, userPassword) : null,
                scope
        ));

        configService.saveConfig(config);
        interact.printUpdated(Messages.Type.CONTEXT, contextId);
    }

}
