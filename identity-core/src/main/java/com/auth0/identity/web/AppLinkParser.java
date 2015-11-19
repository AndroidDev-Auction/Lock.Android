/*
 * LinkParser.java
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

package com.auth0.identity.web;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class AppLinkParser {
    private static final String TAG = AppLinkParser.class.getName();

    public static final int TYPE_INVALID = 0;
    public static final int TYPE_EMAIL   = 1;
    public static final int TYPE_SMS     = 2;

    public boolean isAppLinkIntent(Intent intent) {
        return Intent.ACTION_VIEW.equals(intent.getAction());
    }

    public boolean isValidAppLinkIntent(Intent intent) {
        return TYPE_INVALID != getAppLinkTypeFromIntent(intent);
    }

    public int getAppLinkTypeFromIntent(Intent intent) {
        if (isAppLinkIntent(intent)) {
            Uri uri = intent.getData();
            if (null != uri && null != getCodeFromAppLinkUri(uri)) {
                String pathString = uri.getPath();
                if (null != pathString) {
                    if (pathString.matches("/android/.*/sms")) {
                        return TYPE_SMS;
                    } else if (pathString.matches("/android/.*/email")) {
                        return TYPE_EMAIL;
                    }
                }
            }
        }

        Log.e(TAG, "The app link doesn't match any of the supported types: "+intent);
        return TYPE_INVALID;
    }

    public String getCodeFromAppLinkIntent(Intent intent) {
        if (isAppLinkIntent(intent)) {
            Uri dataUri = intent.getData();
            String code = getCodeFromAppLinkUri(dataUri);
            if (code != null) {
                return code;
            }
        }

        Log.d(TAG, "Invalid app link intent: " + intent);
        return null;
    }

    protected String getCodeFromAppLinkUri(Uri uri) {
        if (uri == null) {
            return null;
        }

        return uri.getQueryParameter("code");
    }
}
