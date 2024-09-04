package org.keycloak.cli.config;

import org.keycloak.cli.enums.Flow;

import java.net.URI;

public class ConfigVerifier {

    private final Config config;
    private final VariableResolver variableResolver;

    private ConfigVerifier(Config config, VariableResolver variableResolver) {
        this.config = config;
        this.variableResolver = variableResolver;
    }

    public static void verify(Config config, VariableResolver variableResolver) {
        new ConfigVerifier(config, variableResolver).verify();
    }

    public void verify() {
        String defaultContext = config.getDefaultContext();
        if (defaultContext != null) {
            checkNotNull(config.getContexts().get(defaultContext), Messages.NOT_FOUND, Messages.Type.DEFAULT_CONTEXT, defaultContext);
        }

        if (config.getIssuers() != null) {
            config.getIssuers().forEach((issuerId, issuer) -> verifyIssuer(Messages.Type.ISSUER, issuerId, issuer));
        }

        config.getContexts().forEach(this::verifyContext);
    }

    private void verifyIssuer(Messages.Type type, String id, Config.Issuer issuer) {
        if (type.equals(Messages.Type.ISSUER)) {
            checkNotNull(issuer, "{0} ''{1}'' invalid: not configured", type, id);
        } else {
            checkNotNull(issuer, "{0} ''{1}'' invalid: issuer not configured", type, id);
        }

        if (empty(issuer.getRef())) {
            checkNotEmpty(issuer.getUrl(), "{0} ''{1}'' invalid: missing issuer url", type, id);
            checkUrl(variableResolver.resolve(issuer.getUrl()), "{0} ''{1}'' invalid: invalid issuer url", type, id);
        } else {
            checkEmpty(issuer.getUrl(), "{0} ''{1}'' invalid: both issuer url and issuer ref set", type, id);
            checkNotNull(config.getIssuers().get(issuer.getRef()), "{0} ''{1}'' invalid: issuer ref ''{2}'' not found", type, id, issuer.getRef());
        }
    }

    private void verifyContext(String contextId, Config.Context context) {
        Config.Issuer issuer = context.getIssuer();
        verifyIssuer(Messages.Type.CONTEXT, contextId, issuer);

        Config.Client client = context.getClient();
        Flow flow = context.getFlow();

        checkNotNull(client, "Context ''{0}'' invalid: missing client", contextId);
        checkNotEmpty(client.getClientId(), "Context ''{0}'' invalid: missing client-id", contextId);
        checkNotNull(flow, "Context ''{0}'' invalid: missing flow", contextId);

        if (flow.equals(Flow.CLIENT)) {
            checkNotEmpty(client.getSecret(), "Context ''{0}'' invalid: client secret required for flow ''{1}''", contextId, flow.jsonName());
        }

        Config.User user = context.getUser();
        if (flow.equals(Flow.PASSWORD)) {
            checkNotNull(user, "Context ''{0}'' invalid: user required for flow ''{1}''", contextId, flow.jsonName());
            checkNotEmpty(user.getUsername(), "Context ''{0}'' invalid: user username required for flow ''{1}''", contextId, flow.jsonName());
            checkNotEmpty(user.getPassword(), "Context ''{0}'' invalid: user password required for flow ''{1}''", contextId, flow.jsonName());
        } else {
            checkNull(user, "Context ''{0}'' invalid: user set for flow ''{1}''", contextId, flow.jsonName());
        }
    }

    private void checkUrl(String value, String failMessage, Object... failParams) {
        try {
            new URI(value).toURL();
        } catch (Exception e) {
            fail(failMessage, failParams);
        }
    }

    private void checkNotNull(Object value, String failMessage, Object... failParams) {
        if (value == null) {
            fail(failMessage, failParams);
        }
    }

    private void checkNotEmpty(String value, String failMessage, Object... failParams) {
        if (empty(value)) {
            fail(failMessage, failParams);
        }
    }

    private boolean empty(String value) {
        return value == null || value.isBlank();
    }

    private void checkEmpty(String value, String failMessage, Object... failParams) {
        if (!empty(value)) {
            fail(failMessage, failParams);
        }
    }

    private void checkNull(Object value, String failMessage, Object... failParams) {
        if (value != null) {
            fail(failMessage, failParams);
        }
    }

    private void fail(String message, Object... params) {
        throw new ConfigException(Messages.format(message, params));
    }

}
