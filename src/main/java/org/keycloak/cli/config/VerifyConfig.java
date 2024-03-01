package org.keycloak.cli.config;

import org.keycloak.cli.enums.Flow;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Map;

public class VerifyConfig {

    private final Config config;

    public VerifyConfig(Config config) {
        this.config = config;
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

    private void verify(String contextId, Config.Issuer issuer) {
        checkNotEmpty(issuer.getUrl(),"context={0} invalid: missing issuer", contextId);
        checkUrl(issuer.getUrl(),"context={0} invalid: issuer is not valid", contextId);
    }

    private void verify(String contextId, Config.Context context) {
        if (!empty(context.getIssuer()) && !empty(context.getIssuerRef())) {
            fail("context={0} invalid: both issuer and issuer-ref set", contextId);
        }

        if (empty(context.getIssuerRef())) {
            checkNotEmpty(context.getIssuer(), "context={0} invalid: missing issuer", contextId);
            checkUrl(context.getIssuer(), "context={0} invalid: issuer is not valid", contextId);
            checkNotEmpty(context.getClient(), "context={0} invalid: missing client", contextId);
        } else {
            checkNotNull(config.getIssuers().get(context.getIssuerRef()), "context={0} invalid: issuer-ref {1} not found", contextId, context.getIssuerRef());
        }

        if (context.getFlow().equals(Flow.PASSWORD)) {
            checkNotEmpty(context.getUser(), "context={0} invalid: user required for flow={1}", contextId, Flow.PASSWORD.jsonName());
            checkNotEmpty(context.getUser(), "context={0} invalid: user-password required for flow={1}", contextId, Flow.PASSWORD.jsonName());
        } else if (context.getFlow().equals(Flow.DEVICE)) {
            checkEmpty(context.getUser(), "context={0} invalid: user specified for flow={1}", contextId, Flow.PASSWORD.jsonName());
            checkEmpty(context.getUser(), "context={0} invalid: user-password specified for flow={1}", contextId, Flow.PASSWORD.jsonName());
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

    private void fail(String message, Object... params) {
        throw new ConfigException(MessageFormat.format(message, params));
    }

}
