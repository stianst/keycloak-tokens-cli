package org.keycloak.cli.config;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.keycloak.cli.enums.Flow;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@ApplicationScoped
public class ConfigService {

    @Inject
    ConfigFileService configFileService;

    boolean fromProperties;

    String context;

    Supplier<String> issuer;

    Supplier<String> client;

    Supplier<String> clientSecret;

    Supplier<String> user;

    Supplier<String> userPassword;

    Supplier<Flow> flow;

    Supplier<String> scope;

    Config config;

    @PostConstruct
    void init() throws IOException {
        config = configFileService.loadConfigFromFile();

        fromProperties = ConfigProvider.getConfig().getOptionalValue("kct.issuer", String.class).isPresent();

        if (!fromProperties && config == null) {
            throw new RuntimeException("Config file " + configFileService.configFile + " not found");
        }

        if (!fromProperties) {
            ConfigVerifier.verify(config);
            ConfigRefResolver.resolve(config);
        }

        context = ConfigProvider.getConfig()
                .getOptionalValue("kct.context", String.class)
                .orElse(!fromProperties ? config.getDefaultContext() : null);

        issuer = new PropSupplier<>("issuer", () -> getCurrent().getIssuer(), String.class);
        client = new PropSupplier<>("client", () -> getCurrent().getClient(), String.class);
        clientSecret = new PropSupplier<>("client-secret", () -> getCurrent().getClientSecret(), String.class, true);
        user = new PropSupplier<>("user", () -> getCurrent().getUser(), String.class, true);
        userPassword = new PropSupplier<>("user-password", () -> getCurrent().getUserPassword(), String.class, true);
        flow = new PropSupplier<>("flow", () -> getCurrent().getFlow(), Flow.class);
        scope = new PropSupplier<>("scope", () -> getCurrent().getScope(), String.class, true);
    }

    public Config getConfig() {
        return config;
    }

    public boolean isConfiguredFromProperties() {
        return fromProperties;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public boolean isStoreTokens() {
        if (context == null) {
            return false;
        } else {
            Boolean storeTokens = config.getStoreTokens();
            return storeTokens != null ? storeTokens : false;
        }
    }

    public String getIssuer() {
        return issuer.get();
    }

    public String getClient() {
        return client.get();
    }

    public String getClientSecret() {
        return clientSecret.get();
    }

    public String getUser() {
        return user.get();
    }

    public String getUserPassword() {
        return userPassword.get();
    }

    public Flow getFlow() {
        return flow.get();
    }

    public Set<String> getScope() {
        String s = scope.get();
        return s != null ? Arrays.stream(s.split(",")).map(String::trim).collect(Collectors.toCollection(LinkedHashSet::new)) : null;
    }

    private Config.Context getCurrent() {
        return config.getContexts().get(getContext());
    }

    class PropSupplier<T> implements Supplier<T> {
        private final String key;
        private final Supplier<T> configSupplier;
        private final Class<T> clazz;
        private final boolean optional;

        public PropSupplier(String key, Supplier<T> configSupplier, Class<T> clazz) {
            this.key = key;
            this.configSupplier = configSupplier;
            this.clazz = clazz;
            this.optional = false;
        }

        public PropSupplier(String key, Supplier<T> configSupplier, Class<T> clazz, boolean optional) {
            this.key = key;
            this.configSupplier = configSupplier;
            this.clazz = clazz;
            this.optional = optional;
        }

        @Override
        public T get() {
            T value = fromProperties ? ConfigProvider.getConfig().getOptionalValue("kct." + key, clazz).orElse(null) : configSupplier.get();
            if (value == null && !optional) {
                throw new RuntimeException(key + " is not configured");
            }
            return value;
        }
    }

}
