package com.auth0.identity.util;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.identity.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Performs code exchange according to Proof Key for Code Exchange (PKCE) spec.
 */
public class PKCE {
    private static final String TAG = PKCE.class.getSimpleName();
    private static final String US_ASCII = "US-ASCII";
    private static final String SHA_256 = "SHA-256";

    private final AuthenticationAPIClient apiClient;
    private final String codeVerifier;
    private final String redirectUri;
    private final String codeChallenge;

    /**
     * Creates a new instance of this class with the given AuthenticationAPIClient.
     * The instance should be disposed after a call to getToken().
     *
     * @param apiClient   to get the OAuth Token.
     * @param redirectUri going to be used in the OAuth code request.
     * @throws IllegalStateException when either 'US-ASCII` encoding or 'SHA-256' algorithm is not available.
     * @see #isAvailable()
     */
    public PKCE(@NonNull AuthenticationAPIClient apiClient, String redirectUri) {
        this(apiClient, redirectUri, generateCodeVerifier());
    }

    PKCE(@NonNull AuthenticationAPIClient apiClient, @NonNull String redirectUri, @NonNull String codeVerifier) {
        this.apiClient = apiClient;
        this.redirectUri = redirectUri;
        this.codeVerifier = codeVerifier;
        this.codeChallenge = generateCodeChallenge();
    }

    public String getCodeChallenge() {
        return codeChallenge;
    }

    private String generateCodeChallenge() {
        byte[] input = asASCIIBytes(codeVerifier);
        byte[] signature = SHA256(input);
        String challenge = Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "Generated code challenge: " + challenge);
        return challenge;
    }

    /**
     * Performs a request to the Auth0 API to get the OAuth Token and end the PKCE flow.
     * The instance of this class must be disposed after this method is called.
     *
     * @param authorizationCode received in the call to /authorize with a "grant_type=code"
     * @param callback          to notify the result of this call to.
     */
    public void getToken(String authorizationCode, @NonNull final IdentityProviderCallback callback) {
        apiClient.tokenRequest(authorizationCode, codeVerifier, redirectUri).start(new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile profile, Token token) {
                Log.i(TAG, "OnSuccess called after PKCE");
                callback.onSuccess(token);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "OnFailure called with error " + error.getMessage());
                callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_access_denied_message, error);
            }
        });
    }

    /**
     * Checks if this device is capable of using the PKCE flow when performing calls to the
     * /authorize endpoint.
     *
     * @return if this device can use PKCE flow or not.
     */
    public static boolean isAvailable() {
        try {
            byte[] input = asASCIIBytes("test");
            SHA256(input);
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private static byte[] asASCIIBytes(String value) {
        byte[] input;
        try {
            input = value.getBytes(US_ASCII);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, "Could not convert string to an ASCII byte array", e);
            throw new IllegalStateException("Could not convert string to an ASCII byte array", e);
        }
        return input;
    }

    private static byte[] SHA256(byte[] input) {
        byte[] signature;
        try {
            MessageDigest md = MessageDigest.getInstance(SHA_256);
            md.update(input, 0, input.length);
            signature = md.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Failed to get SHA-256 signature", e);
            throw new IllegalStateException("Failed to get SHA-256 signature", e);
        }
        return signature;
    }

    private static String generateCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }
}
