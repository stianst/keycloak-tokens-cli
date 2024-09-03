package org.keycloak.cli.config;

import java.text.MessageFormat;

public class Messages {

    public static String CREATED = "{0} ''{1}'' created";

    public static String NOT_FOUND = "{0} ''{1}'' not found";

    public static String EXISTS = "{0} ''{1}'' already exists";

    public static String DELETED = "{0} ''{1}'' deleted";

    public static String UPDATED = "{0} ''{1}'' updated";

    public enum Type {
        CONTEXT,
        DEFAULT_CONTEXT,
        ISSUER;

        @Override
        public String toString() {
            return super.toString().toLowerCase().replace('_', ' ');
        }
    }

    public static String format(String message, Object... arguments) {
        return capitalize(MessageFormat.format(message, arguments));
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }

}
