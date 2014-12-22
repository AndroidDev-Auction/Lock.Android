/*
 * WebIdentityProvider.java
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

package com.auth0.lock.identity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.lock.Lock;
import com.auth0.lock.LockActivity;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.SocialAuthenticationEvent;
import com.auth0.lock.event.SocialAuthenticationRequestEvent;
import com.auth0.lock.provider.BusProvider;
import com.auth0.lock.web.CallbackParser;
import com.auth0.lock.web.WebViewActivity;

import java.util.Map;

/**
 * Created by hernan on 12/22/14.
 */
public class WebIdentityProvider implements IdentityProvider {

    private Lock lock;
    private BusProvider provider;
    private CallbackParser parser;

    public WebIdentityProvider(CallbackParser parser) {
        this.parser = parser;
    }

    public void initialize(Lock lock, BusProvider provider) {
        this.lock = lock;
        this.provider = provider;
    }

    @Override
    public void authorize(int requestCode, int resultCode, Intent data) {
        Uri uri = data.getData();
        if (uri != null) {
            final Map<String, String> values = parser.getValuesFromUri(uri);
            if (values.containsKey("error")) {
                final int message = "access_denied".equalsIgnoreCase(values.get("error")) ? R.string.social_access_denied_message : R.string.social_error_message;
                final AuthenticationError error = new AuthenticationError(R.string.social_error_title, message);
                provider.getBus().post(error);
            } else if(values.size() > 0) {
                Log.d(LockActivity.class.getName(), "Authenticated using web flow");
                provider.getBus().post(new SocialAuthenticationEvent(values));
            }
        }
    }

    @Override
    public void authenticate(Activity activity, SocialAuthenticationRequestEvent event, Application application) {
        final Uri url = event.getAuthenticationUri(application);
        final Intent intent;
        if (lock.isUseWebView()) {
            intent = new Intent(activity, WebViewActivity.class);
            intent.setData(url);
            intent.putExtra(WebViewActivity.SERVICE_NAME, event.getServiceName());
            activity.startActivityForResult(intent, WEBVIEW_AUTH_REQUEST_CODE);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, url);
            activity.startActivity(intent);
        }
    }

}
