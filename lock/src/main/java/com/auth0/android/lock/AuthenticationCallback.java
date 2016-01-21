package com.auth0.android.lock;

import com.auth0.authentication.Authentication;

/**
 * Created by nikolaseu on 1/21/16.
 */
public interface AuthenticationCallback {
    void onAuthentication(Authentication authentication);
    void onCancelled();
    void onError(Auth0Exception error);
}
