package org.keycloak.cli.config;

import org.keycloak.cli.enums.Flow;

import java.net.URI;
import java.util.Map;

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
        String defaultContext = config.defaultContext();
        if (defaultContext != null) {
            checkNotNull(config.findContext(defaultContext), Messages.NOT_FOUND, Messages.Type.DEFAULT_CONTEXT, defaultContext);
        }

        if (config.issuers() != null) {
            config.issuers().forEach((issuerId, issuer) -> verifyIssuer(Messages.Type.ISSUER, issuerId, issuer));
        }
    }

    private void verifyIssuer(Messages.Type type, String id, Config.Issuer issuer) {
        if (type.equals(Messages.Type.ISSUER)) {
            checkNotNull(issuer, "{0} ''{1}'' invalid: not configured", type, id);
        } else {
            checkNotNull(issuer, "{0} ''{1}'' invalid: issuer not configured", type, id);
        }

        checkNotEmpty(issuer.url(), "{0} ''{1}'' invalid: missing issuer url", type, id);
        checkUrl(variableResolver.resolve(issuer.url()), "{0} ''{1}'' invalid: invalid issuer url", type, id);

        for (Map.Entry<String, Config.Context> e : issuer.contexts().entrySet()) {
            verifyContext(e.getKey(), e.getValue());
        }
    }

    private void verifyContext(String contextId, Config.Context context) {
        Config.Client client = context.client();
        Flow flow = context.flow();

        checkNotNull(client, "Context ''{0}'' invalid: missing client", contextId);
        checkNotEmpty(client.clientId(), "Context ''{0}'' invalid: missing client-id", contextId);
        checkNotNull(flow, "Context ''{0}'' invalid: missing flow", contextId);

        if (flow.equals(Flow.CLIENT)) {
            checkNotEmpty(client.secret(), "Context ''{0}'' invalid: client secret required for flow ''{1}''", contextId, flow.jsonName());
        }

        Config.User user = context.user();
        if (flow.equals(Flow.PASSWORD)) {
            checkNotNull(user, "Context ''{0}'' invalid: user required for flow ''{1}''", contextId, flow.jsonName());
            checkNotEmpty(user.username(), "Context ''{0}'' invalid: user username required for flow ''{1}''", contextId, flow.jsonName());
            checkNotEmpty(user.password(), "Context ''{0}'' invalid: user password required for flow ''{1}''", contextId, flow.jsonName());
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
