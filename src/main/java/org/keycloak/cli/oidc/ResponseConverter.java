package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.ErrorObject;
import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPRequest;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.oauth2.sdk.token.AccessTokenType;
import com.nimbusds.oauth2.sdk.token.BearerTokenError;
import com.nimbusds.oauth2.sdk.token.TokenSchemeError;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformationResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationResponseParser;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

public class ResponseConverter<T> {

    private final HTTPRequest request;
    private final HTTPResponse response;
    private final Class<T> clazz;

    public ResponseConverter(HTTPRequest request, HTTPResponse response, Class<T> clazz) {
        this.request = request;
        this.response = response;
        this.clazz = clazz;
    }

    @SuppressWarnings("unchecked")
    public T convert() {
        try {
            if (response.indicatesSuccess()) {
                return (T) toResponse();
            } else {
                throw toException();
            }
        } catch (ParseException e) {
            throw new OidcException(request, e);
        }
    }

    private Object toResponse() throws ParseException {
        if (String.class.equals(clazz)) {
            return response.getBody();
        } else if (Boolean.class.equals(clazz)) {
            return Boolean.TRUE;
        } else if (OIDCProviderMetadata.class.equals(clazz)) {
            return OIDCProviderMetadata.parse(response.getBodyAsJSONObject());
        } else if (UserInfo.class.equals(clazz)) {
            return UserInfoResponse.parse(response).toSuccessResponse().getUserInfo();
        } else if (OIDCTokens.class.equals(clazz)) {
            return OIDCTokenResponse.parse(response).getOIDCTokens();
        } else if (DeviceAuthorizationSuccessResponse.class.equals(clazz)) {
            return DeviceAuthorizationResponse.parse(response).toSuccessResponse();
        } else if (OIDCClientInformation.class.equals(clazz)) {
            return ((OIDCClientInformationResponse) OIDCClientRegistrationResponseParser.parse(response).toSuccessResponse()).getOIDCClientInformation();
        } else {
            throw new RuntimeException("Unknown response type '" + clazz.getSimpleName() + "'");
        }
    }

    private OidcException toException() {
        String wwwAuth = response.getWWWAuthenticate();
        if (StringUtils.isNotBlank(wwwAuth)) {
            try {
                BearerTokenError tokenError = BearerTokenError.parse(response.getWWWAuthenticate());
                return new OidcException(request, response, tokenError.getCode(), tokenError.getDescription());
            } catch (ParseException e) {
            }
        }

        ErrorObject errorObject = ErrorObject.parse(response);
        return new OidcException(request, response, errorObject.getCode(), errorObject.getDescription());
    }

}
