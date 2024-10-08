package org.keycloak.cli.oidc;

import com.nimbusds.oauth2.sdk.ParseException;
import com.nimbusds.oauth2.sdk.TokenResponse;
import com.nimbusds.oauth2.sdk.client.ClientRegistrationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationResponse;
import com.nimbusds.oauth2.sdk.device.DeviceAuthorizationSuccessResponse;
import com.nimbusds.oauth2.sdk.http.HTTPResponse;
import com.nimbusds.openid.connect.sdk.OIDCTokenResponse;
import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import com.nimbusds.openid.connect.sdk.claims.UserInfo;
import com.nimbusds.openid.connect.sdk.op.OIDCProviderMetadata;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformation;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientInformationResponse;
import com.nimbusds.openid.connect.sdk.rp.OIDCClientRegistrationResponseParser;
import com.nimbusds.openid.connect.sdk.token.OIDCTokens;

public class ResponseConverter {

    public static <T> Object convert(HTTPResponse response, Class<T> clazz) throws ParseException {
        boolean success = response.indicatesSuccess();

        if (String.class.equals(clazz)) {
            return success ? response.getBody() : null;
        } else if (Boolean.class.equals(clazz)) {
            return success ? Boolean.TRUE : Boolean.FALSE;
        } else if (OIDCProviderMetadata.class.equals(clazz)) {
            return success ? OIDCProviderMetadata.parse(response.getBodyAsJSONObject()) : null;
        } else if (UserInfo.class.equals(clazz)) {
            UserInfoResponse userInfoResponse = UserInfoResponse.parse(response);
            return success ? userInfoResponse.toSuccessResponse().getUserInfo() : userInfoResponse.toErrorResponse();
        } else if (OIDCTokens.class.equals(clazz)) {
            return success ? OIDCTokenResponse.parse(response).getOIDCTokens() : TokenResponse.parse(response).toErrorResponse();
        } else if (DeviceAuthorizationSuccessResponse.class.equals(clazz)) {
            DeviceAuthorizationResponse deviceAuthorizationResponse = DeviceAuthorizationResponse.parse(response);
            return success ? deviceAuthorizationResponse.toSuccessResponse() : deviceAuthorizationResponse.toErrorResponse();
        } else if (OIDCClientInformation.class.equals(clazz)) {
            ClientRegistrationResponse clientRegistrationResponse = OIDCClientRegistrationResponseParser.parse(response);
            return success ? ((OIDCClientInformationResponse) clientRegistrationResponse.toSuccessResponse()).getOIDCClientInformation() : clientRegistrationResponse.toErrorResponse();
        } else {
            throw new RuntimeException("Unknown response type '" + clazz.getSimpleName() + "'");
        }
    }

}
