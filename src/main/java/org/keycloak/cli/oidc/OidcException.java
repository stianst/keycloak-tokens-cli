package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import org.keycloak.cli.config.Messages;

public class OidcException extends RuntimeException {

    private HTTPRequest request;
    private HTTPResponse response;
    private String errorCode;
    private String errorDescription;

    public OidcException(HTTPRequest request, HTTPResponse response, String errorCode, String errorDescription) {
        super();
        this.request = request;
        this.response = response;
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
    }

    public OidcException(HTTPRequest request, Exception exception) {
        super();
        this.request = request;
        this.errorDescription = exception.getMessage();
    }

    public String getCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        if (response != null) {
            return Messages.format("Request failed: status={0}, error={1}, description=''{2}'', url={3}", response.getStatusCode(), errorCode, errorDescription, request.getURI());
        } else {
            return Messages.format("Request failed: error=''{0}'', url={1}", errorDescription, request.getURI());
        }
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
