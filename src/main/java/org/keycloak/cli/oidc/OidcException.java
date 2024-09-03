package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.ErrorResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import org.keycloak.cli.config.Messages;

public class OidcException extends RuntimeException {

    private HTTPRequest request;
    private ErrorResponse errorResponse;
    private int statusCode;
    private String errorMessage;

    public OidcException(HTTPRequest request, ErrorResponse errorResponse) {
        super();
        this.request = request;
        this.errorResponse = errorResponse;
    }

    public OidcException(HTTPRequest request, int statusCode) {
        super();
        this.request = request;
        this.statusCode = statusCode;
    }

    public OidcException(HTTPRequest request, String errorMessage) {
        super();
        this.request = request;
        this.errorMessage = errorMessage;
    }

    public String getCode() {
        return errorResponse != null ? errorResponse.getErrorObject().getCode() : null;
    }

    @Override
    public String getMessage() {
        if (errorResponse != null) {
            return Messages.format("Received error ''{0}'' from ''{1}''", errorResponse.getErrorObject().getCode(), request.getURI());
        } else if (errorMessage != null) {
            return Messages.format("Error sending request to ''{0}'': ''{1}''", request.getURI(), errorMessage);
        } else {
            return Messages.format("Received error ''{0}'' from ''{1}''", statusCode, request.getURI());
        }
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
