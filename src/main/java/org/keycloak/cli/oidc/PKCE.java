package org.keycloak.cli.oidc;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class PKCE {

    public static final String S256 = "S256";

    private static final char[] ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    private String codeChallenge;
    private String codeVerifier;

    public static PKCE create() {
        PKCE pkce = new PKCE();
        pkce.codeVerifier = pkce.randomString(128);
        pkce.codeChallenge = pkce.generateCodeChallenge(pkce.codeVerifier);
        return pkce;
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    private String randomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        char[] chars = new char[length];
        for (int i = 0; i < chars.length; i++) {
            chars[i] = ALPHANUM[secureRandom.nextInt(ALPHANUM.length)];
        }
        return new String(chars);
    }

    private String generateCodeChallenge(String codeChallenge) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(codeChallenge.getBytes(StandardCharsets.ISO_8859_1));
            String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(md.digest());
            return encoded;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
