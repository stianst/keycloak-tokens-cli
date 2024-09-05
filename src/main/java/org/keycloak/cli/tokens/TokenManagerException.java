package org.keycloak.cli.tokens;

import org.keycloak.cli.config.Messages;

public class TokenManagerException extends RuntimeException {

    public TokenManagerException(String message) {
        super(message);
    }

    public TokenManagerException(String message, Object... args) {
        super(Messages.format(message, args));
    }
}
