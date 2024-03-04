package org.keycloak.cli.config;

import org.keycloak.cli.enums.Flow;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

public class ConfigVerifier {

    private final Config config;

    private ConfigVerifier(Config config) {
        this.config = config;
    }

    public static void verify(Config config) {
        new ConfigVerifier(config).verify();
    }

    public void verify() {
        String defaultContext = config.getDefaultContext();
        checkNotEmpty(defaultContext, "default context not set");
        if (config.getContexts().get(defaultContext) == null) {
            fail("default context={0} not found", defaultContext);
        }

        if (config.getIssuers() != null) {
            for (Map.Entry<String, Config.Issuer> e : config.getIssuers().entrySet()) {
                if (e.getValue() == null) {
                    fail("issuer={0} invalid: not configured");
                }
                verify(e.getKey(), e.getValue());
            }
        }

        for (Map.Entry<String, Config.Context> e : config.getContexts().entrySet()) {
            verify(e.getKey(), e.getValue());
        }
    }

    private void verify(String issuerId, Config.Issuer issuer) {
        checkNotEmpty(issuer.getUrl(), "issuer={0} invalid: missing url", issuerId);
        checkUrl(issuer.getUrl(), "issuer={0} invalid: invalid url", issuerId);

        for (Map.Entry<String, Config.Client> e : issuer.getClients().entrySet()) {
            verify(issuerId, e.getKey(), e.getValue());
        }
    }

    private void verify(String issuerId, String clientId, Config.Client client) {
        checkNotEmpty(client.getId(), "issuer={0} invalid: client={1} missing id", issuerId, clientId);
        checkNotNull(client.getFlow(), "issuer={0} invalid: client={1} missing flow", issuerId, clientId);
    }

    private void verify(String contextId, Config.Context context) {
        if (empty(context.getIssuerRef())) {
            checkNotEmpty(context.getIssuer(), "context={0} invalid: missing issuer", contextId);
            checkUrl(context.getIssuer(), "context={0} invalid: invalid issuer", contextId);
        } else {
            checkEmpty(context.getIssuer(), "context={0} invalid: both issuer and issuer-ref set", contextId);
            checkNotNull(config.getIssuers().get(context.getIssuerRef()), "context={0} invalid: issuer-ref={1} not found", contextId, context.getIssuerRef());
        }

        Flow flow;
        if (empty(context.getClientRef())) {
            checkNotEmpty(context.getClient(), "context={0} invalid: missing client", contextId);
            checkNotNull(context.getFlow(), "context={0} invalid: missing flow", contextId);

            flow = context.getFlow();
        } else {
            checkEmpty(context.getClient(), "context={0} invalid: both client and client-ref set", contextId);
            checkEmpty(context.getClientSecret(), "context={0} invalid: both client-secret and client-ref set", contextId);
            checkNull(context.getFlow(), "context={0} invalid: both flow and client-ref set", contextId);

            Config.Client clientRef = config.getIssuers().get(context.getIssuerRef()).getClients().get(context.getClientRef());
            checkNotNull(clientRef, "context={0} invalid: client-ref={1} not found in issuer-ref={2}", contextId, context.getClientRef(), context.getIssuerRef());

            flow = clientRef.getFlow();
        }

        if (flow.equals(Flow.PASSWORD)) {
            checkNotEmpty(context.getUser(), "context={0} invalid: user required for flow={1}", contextId, flow.jsonName());
            checkNotEmpty(context.getUserPassword(), "context={0} invalid: user-password required for flow={1}", contextId, flow.jsonName());
        } else if (flow.equals(Flow.DEVICE)) {
            checkEmpty(context.getUser(), "context={0} invalid: user set for flow={1}", contextId, flow.jsonName());
            checkEmpty(context.getUserPassword(), "context={0} invalid: user-password set for flow={1}", contextId, flow.jsonName());
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
        throw new ConfigException(MessageFormat.format(message, params));
    }

}
