package org.keycloak.cli.web;

public enum MimeType {

    FORM("application/x-www-form-urlencoded"),
    HTML("text/html"),
    X_ICON("image/x-icon");

    private String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String toString() {
        return mimeType;
    }
}
