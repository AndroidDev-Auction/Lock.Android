/*
 * LockEmailActivity.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.lock.email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.web.AppLinkParser;
import com.auth0.lock.Lock;
import com.auth0.lock.email.event.CodeManualEntryRequestedEvent;
import com.auth0.lock.email.event.EmailVerificationCodeRequestedEvent;
import com.auth0.lock.email.event.EmailVerificationCodeSentEvent;
import com.auth0.lock.email.event.LoginRequestEvent;
import com.auth0.lock.email.fragment.EmailLoginFragment;
import com.auth0.lock.email.fragment.InProgressFragment;
import com.auth0.lock.email.fragment.InvalidLinkFragment;
import com.auth0.lock.email.fragment.MagicLinkLoginFragment;
import com.auth0.lock.email.fragment.RequestCodeFragment;
import com.auth0.lock.error.ErrorDialogBuilder;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.util.ActivityUIHelper;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;


/**
 * Activity that handles passwordless authentication using Email.
 * You'll need to declare it in your app's {@code AndroidManifest.xml}:
 * <pre>{@code
 * <activity
 *  android:name="com.auth0.lock.email.LockEmailActivity"
 *  android:theme="@style/Lock.Theme"
 *  android:label="@string/app_name"
 *  android:screenOrientation="portrait"
 *  android:launchMode="singleTask"/>
 * }</pre>
 *
 * Then just start it like any other Android activity:
 * <pre>{@code
 * Intent lockIntent = new Intent(this, LockEmailActivity.class);
 * startActivity(lockIntent);
 * }
 * </pre>
 *
 * And finally register listeners in {@link android.support.v4.content.LocalBroadcastManager} for these actions:
 * <ul>
 *     <li><b>Lock.Authentication</b>: Sent on a successful authentication with {@link com.auth0.core.UserProfile} and {@link com.auth0.core.Token}.
 *     Or both {@code null} when {@link Lock#loginAfterSignUp} is {@code false} </li>
 *     <li><b>Lock.Cancel</b>: Sent when the user's closes the activity by pressing the back button without authenticating. (Only if {@link Lock#closable} is {@code true}</li>
 * </ul>
 *
 * All these action names are defined in these constants: {@link Lock#AUTHENTICATION_ACTION} and {@link Lock#CANCEL_ACTION}.
 */
public class LockEmailActivity extends FragmentActivity {

    private static final String TAG = LockEmailActivity.class.getName();

    private static final String EMAIL_PARAMETER = "EMAIL_PARAMETER";
    private static final String USE_MAGIC_LINK_PARAMETER = "USE_MAGIC_LINK_PARAMETER";
    private static final String IS_IN_PROGRESS_PARAMETER = "IS_IN_PROGRESS_PARAMETER";

    Lock lock;

    private boolean isInProgress;
    private boolean useMagicLink;
    private String email;
    private LoginAuthenticationErrorBuilder errorBuilder;

    protected AuthenticationAPIClient client;
    protected Bus bus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_activity_lock_email);

        lock = getLock();
        client = lock.getAuthenticationAPIClient();
        bus = lock.getBus();
        errorBuilder = new LoginAuthenticationErrorBuilder(R.string.com_auth0_email_login_error_title, R.string.com_auth0_email_login_error_message, R.string.com_auth0_email_login_invalid_credentials_message);

        if (savedInstanceState == null) {
            isInProgress = false;
            boolean invalidMagicLink = Intent.ACTION_VIEW.equals(getIntent().getAction());
            useMagicLink = invalidMagicLink || getIntent().getBooleanExtra(USE_MAGIC_LINK_PARAMETER, false);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.com_auth0_container, RequestCodeFragment.newInstance(useMagicLink))
                    .commit();
            if (invalidMagicLink) {
                Fragment fragment = new InvalidLinkFragment();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.com_auth0_container, fragment)
                        .addToBackStack(fragment.getClass().getName())
                        .commit();
            }
        } else {
            isInProgress = savedInstanceState.getBoolean(IS_IN_PROGRESS_PARAMETER);
            useMagicLink = savedInstanceState.getBoolean(USE_MAGIC_LINK_PARAMETER);
            email = savedInstanceState.getString(EMAIL_PARAMETER);
        }

        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(IS_IN_PROGRESS_PARAMETER, isInProgress);
        savedInstanceState.putBoolean(USE_MAGIC_LINK_PARAMETER, useMagicLink);
        savedInstanceState.putString(EMAIL_PARAMETER, email);

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ActivityUIHelper.configureScreenModeForActivity(this, lock);
    }

    @Override
    protected void onStart() {
        super.onStart();
        lock.getBus().register(this);

        Log.d(TAG, "onStart email: " + email + " intent: " + getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.d(TAG, "onNewIntent email: " + email + " intent: " + intent);

        AppLinkParser linkParser = new AppLinkParser();
        String passcode = linkParser.getCodeFromAppLinkIntent(intent);
        performLogin(new LoginRequestEvent(email, passcode));
    }

    @Override
    protected void onStop() {
        super.onStop();
        lock.getBus().unregister(this);
    }

    @Override
    public void onBackPressed() {
        if (!isInProgress) {
            // the backstack is managed automatically
            super.onBackPressed();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeRequestedEvent(EmailVerificationCodeRequestedEvent event) {
        sendEmail(event);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(EmailVerificationCodeSentEvent event) {
        email = event.getEmail();
        Fragment fragment;
        if (!useMagicLink) {
            fragment = EmailLoginFragment.newInstance(email);
        } else {
            fragment = MagicLinkLoginFragment.newInstance(email);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onCodeManualEntryRequested(CodeManualEntryRequestedEvent event) {
        Fragment fragment = EmailLoginFragment.newInstance(email);
        getSupportFragmentManager().popBackStack();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        switch (event) {
            case BACK:
                onBackPressed();
                break;
            default:
                Log.v(TAG, "Invalid navigation event " + event);
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(TAG, "Failed to authenticate user", error.getThrowable());
        ErrorDialogBuilder.showAlertDialog(this, error);

        if (isInProgress) {
            isInProgress = false;
            getSupportFragmentManager().popBackStack();
        }
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Token token = event.getToken();
        Log.i(TAG, "Authenticated user " + profile.getName());
        Intent result = new Intent(Lock.AUTHENTICATION_ACTION)
                .putExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER, profile)
                .putExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);
        finish();
    }

    @SuppressWarnings("unused")
    @Subscribe public void onLoginRequest(LoginRequestEvent event) {
        performLogin(event);
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        return Lock.getLock(this);
    }

    private void performLogin(LoginRequestEvent event) {
        Fragment fragment = InProgressFragment.newInstance(
                useMagicLink ? R.string.com_auth0_email_title_in_progress_magic_link : R.string.com_auth0_email_title_in_progress_code, email);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
        isInProgress = true;

        client.loginWithEmail(event.getEmail(), event.getPasscode())
                .addParameters(lock.getAuthenticationParameters())
                .start(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(UserProfile userProfile, Token token) {
                        bus.post(new AuthenticationEvent(userProfile, token));
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        bus.post(errorBuilder.buildFrom(throwable));
                    }
                });
    }

    private void sendEmail(final EmailVerificationCodeRequestedEvent event) {
        email = event.getEmail();
        client.passwordlessWithEmail(email, useMagicLink).start(new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                Log.d(TAG, "Email code sent to " + email);
                if (!event.isRetry()) {
                    bus.post(new EmailVerificationCodeSentEvent(email));
                }
            }

            @Override
            public void onFailure(Throwable error) {
                bus.post(new AuthenticationError(R.string.com_auth0_email_send_code_error_tile, R.string.com_auth0_email_send_code_error_message, error));
            }
        });
    }

    public static void showFrom(Activity activity, boolean useMagicLink) {
        Intent intent = new Intent(activity, LockEmailActivity.class);
        intent.putExtra(LockEmailActivity.USE_MAGIC_LINK_PARAMETER, useMagicLink);
        activity.startActivity(intent);
    }
}
