/*
 * Lock.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock;

import com.auth0.api.APIClient;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.WebIdentityProvider;
import com.auth0.identity.web.CallbackParser;
import com.auth0.lock.credentials.CredentialStore;
import com.auth0.lock.credentials.NullCredentialStore;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Lock is the class that allows to change the behaviour of Lock using its options.
 */
public class Lock {

    /**
     * Action sent in {@link android.support.v4.content.LocalBroadcastManager} when a user authenticates.
     */
    public static final String AUTHENTICATION_ACTION = "Lock.Authentication";
    /**
     * Action sent when the user navigates back closing {@link com.auth0.lock.LockActivity}.
     */
    public static final String CANCEL_ACTION = "Lock.Cancel";
    /**
     * Action sent when the user change its password.
     */
    public static final String CHANGE_PASSWORD_ACTION = "Lock.ChangePassword";
    /**
     * Name of the parameter that will include user's profile.
     */
    public static final String AUTHENTICATION_ACTION_PROFILE_PARAMETER = "profile";
    /**
     * Name of the parameter that will include user's token information.
     */
    public static final String AUTHENTICATION_ACTION_TOKEN_PARAMETER = "token";

    private boolean useWebView;
    private boolean loginAfterSignUp;
    private boolean closable;
    private Map<String, Object> authenticationParameters;
    private boolean useEmail;
    private boolean fullScreen;

    private WebIdentityProvider defaultProvider;
    private Map<String, IdentityProvider> providers;

    private final Bus bus;
    private final APIClient apiClient;

    private Configuration configuration;
    private List<String> connections;
    private String defaultDatabaseConnection;
    private boolean signUpEnabled;
    private boolean changePasswordEnabled;
    private CredentialStore credentialStore;

    public Lock(APIClient apiClient) {
        this.useWebView = false;
        this.closable = false;
        this.loginAfterSignUp = true;
        this.useEmail = true;
        this.providers = new HashMap<>();
        this.bus = new Bus("Lock");
        this.defaultProvider = new WebIdentityProvider(new CallbackParser());
        this.apiClient = apiClient;
        this.fullScreen = false;
        this.signUpEnabled = true;
        this.changePasswordEnabled = true;
        this.credentialStore = new NullCredentialStore();
    }

    /**
     * A instance of {@link com.auth0.api.APIClient} used by Lock
     * @return a client
     */
    public APIClient getAPIClient() {
        return apiClient;
    }

    /**
     * A instance of {@link com.squareup.otto.Bus} where all internal events are sent.
     * @return a bus used internally
     */
    public Bus getBus() {
        return bus;
    }

    /**
     * Force Lock to use an embedded {@link android.webkit.WebView}. Default is {@code false}
     * You'll also need to declare the following activity in your {@code AndroidManifest.xml}
     * <pre>{@code
     * <activity android:name="com.auth0.identity.web.WebViewActivity" android:theme="@style/Lock.Theme"/>
     * }</pre>
     * @param useWebView if Lock should use an embedded {@link android.webkit.WebView} or not
     */
    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
        this.defaultProvider.setUseWebView(useWebView);
    }

    /**
     * Flag that tells Lock to use an embedded {@link android.webkit.WebView}. Default is {@code false}
     * @return if Lock uses a {@link android.webkit.WebView}
     */
    public boolean shouldUseWebView() {
        return useWebView;
    }

    /**
     * Make Lock login a newly created user. Default is {@code true}
     * @param loginAfterSignUp If Lock performs signup + login
     */
    public void setLoginAfterSignUp(boolean loginAfterSignUp) {
        this.loginAfterSignUp = loginAfterSignUp;
    }

    /**
     * Make Lock login a newly created user. Default is {@code true}
     * @return If Lock performs signup + login
     */
    public boolean shouldLoginAfterSignUp() {
        return loginAfterSignUp;
    }

    /**
     * Allows Lock activities to be closed by pressing back button. Default is {@code false}
     * @return if back button is enabled for Lock
     */
    public boolean isClosable() {
        return closable;
    }

    /**
     * Allows Lock activity to be closed by pressing back button. Default is {@code false}
     * @param closable if back button is enabled for Lock
     */
    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    /**
     * Use Email to authenticate, otherwise use username. Default is {@code true}
     * @param useEmail use email or username
     */
    public void setUseEmail(boolean useEmail) {
        this.useEmail = useEmail;
    }

    /**
     * Use Email to authenticate, otherwise use username. Default is {@code true}
     * @return use email or username
     */
    public boolean shouldUseEmail() {
        return useEmail;
    }

    /**
     * Extra parameters sent to Auth0 API during authentication
     * @return extra parameters for the API
     */
    public Map<String, Object> getAuthenticationParameters() {
        return authenticationParameters != null ? new HashMap<>(authenticationParameters) : new HashMap<String, Object>();
    }

    /**
     * Extra parameters sent to Auth0 API during authentication
     * @param authenticationParameters extra parameters for the API
     */
    public void setAuthenticationParameters(Map<String, Object> authenticationParameters) {
        this.authenticationParameters = authenticationParameters;
        this.defaultProvider.setParameters(authenticationParameters);
    }

    /**
     * Set a native handler for a specific Identity Provider (IdP), e.g.: Facebook
     * @param serviceName name of the Auth0 strategy to handle. (For all valid values check {@link com.auth0.core.Strategies}
     * @param provider IdP handler
     */
    public void setProvider(String serviceName, IdentityProvider provider) {
        providers.put(serviceName, provider);
    }

    /**
     * Finds a custom IdP handler by service name.
     * @param serviceName name of the service
     * @return a custom handler or null
     */
    public IdentityProvider providerForName(String serviceName) {
        IdentityProvider provider = providers.get(serviceName);
        return provider != null ? provider : defaultProvider;
    }

    /**
     * Default provider for every Auth0 Authentication strategy
     * @return a default provider
     */
    public IdentityProvider getDefaultProvider() {
        return defaultProvider;
    }

    /**
     * Clears all session information stored in custom IdP handlers.
     */
    public void resetAllProviders() {
        for (IdentityProvider provider: this.providers.values()) {
            provider.stop();
        }
        this.defaultProvider.stop();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setDefaultDatabaseConnection(String defaultDatabaseConnection) {
        this.defaultDatabaseConnection = defaultDatabaseConnection;
    }

    public String getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    /**
     * Shows Lock in Fullscreen mode.
     * @param fullScreen if lock is shown in fullscreen mode
     */
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    /**
     * If Lock is displayed in fullscreen mode. By default is false
     * @return
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setSignUpEnabled(boolean signUpEnabled) {
        this.signUpEnabled = signUpEnabled;
    }

    /**
     * If Lock has SignUp action enabled
     * @return
     */
    public boolean isSignUpEnabled() {
        return signUpEnabled;
    }

    public void setChangePasswordEnabled(boolean changePasswordEnabled) {
        this.changePasswordEnabled = changePasswordEnabled;
    }

    /**
     * If Lock has Change Password action enabled
     * @return
     */
    public boolean isChangePasswordEnabled() {
        return changePasswordEnabled;
    }

    public void setCredentialStore(CredentialStore credentialStore) {
        if (credentialStore != null) {
            this.credentialStore = credentialStore;
        } else {
            this.credentialStore = new NullCredentialStore();
        }
    }

    /**
     * Lock's credential store for user's credentials e.g. Google's Smart Lock
     * By default no credentials are stored.
     * @return an instance of CredentialStore
     */
    public CredentialStore getCredentialStore() {
        return credentialStore;
    }
}
