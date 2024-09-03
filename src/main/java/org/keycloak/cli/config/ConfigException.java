package org.keycloak.cli.config;

public class ConfigException extends RuntimeException {

    public static ConfigException exists(Messages.Type type, String id) {
        return new ConfigException(Messages.format(Messages.EXISTS, type, id));
    }

    public static ConfigException notFound(Messages.Type type, String id) {
        return new ConfigException(Messages.format(Messages.NOT_FOUND, type, id));
    }

    public ConfigException(String message) {
        super(message);
    }

    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

}
