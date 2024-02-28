package org.keycloak.cli.oidc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderMetadata {

    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;
    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
    @JsonProperty("userinfo_endpoint")
    private String userinfoEndpoint;

    @JsonProperty("device_authorization_endpoint")
    private String deviceAuthorizationEndpoint;

    public String getAuthorizationEndpoint() {
        return authorizationEndpoint;
    }

    public void setAuthorizationEndpoint(String authorizationEndpoint) {
        this.authorizationEndpoint = authorizationEndpoint;
    }

    public String getTokenEndpoint() {
        return tokenEndpoint;
    }

    public void setTokenEndpoint(String tokenEndpoint) {
        this.tokenEndpoint = tokenEndpoint;
    }

    public String getUserinfoEndpoint() {
        return userinfoEndpoint;
    }

    public void setUserinfoEndpoint(String userinfoEndpoint) {
        this.userinfoEndpoint = userinfoEndpoint;
    }

    public String getDeviceAuthorizationEndpoint() {
        return deviceAuthorizationEndpoint;
    }

    public void setDeviceAuthorizationEndpoint(String deviceAuthorizationEndpoint) {
        this.deviceAuthorizationEndpoint = deviceAuthorizationEndpoint;
    }
}
