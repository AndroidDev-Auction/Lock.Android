/*
 * LockOptions.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import com.auth0.Auth0;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class LockOptions implements Parcelable {
    private static final int WITHOUT_DATA = 0x00;
    private static final int HAS_DATA = 0x01;
    private static final String KEY_AUTHENTICATION_PARAMETERS = "authenticationParameters";

    public Auth0 account;
    public boolean useBrowser;
    public boolean closable;
    public boolean fullscreen;
    public boolean sendSDKInfo = true;
    public boolean useEmail = false;
    public boolean signUpEnabled = true;
    public boolean changePasswordEnabled = true;
    public String defaultDatabaseConnection;
    public List<String> connections;
    public List<String> enterpriseConnectionsUsingWebForm;
    public HashMap<String, Object> authenticationParameters;

    public LockOptions() {
    }

    protected LockOptions(Parcel in) {
        Auth0Parcelable auth0Parcelable = (Auth0Parcelable) in.readValue(Auth0Parcelable.class.getClassLoader());
        account = auth0Parcelable.getAuth0();
        useBrowser = in.readByte() != HAS_DATA;
        closable = in.readByte() != WITHOUT_DATA;
        fullscreen = in.readByte() != WITHOUT_DATA;
        sendSDKInfo = in.readByte() != WITHOUT_DATA;
        useEmail = in.readByte() != WITHOUT_DATA;
        signUpEnabled = in.readByte() != WITHOUT_DATA;
        changePasswordEnabled = in.readByte() != WITHOUT_DATA;
        defaultDatabaseConnection = in.readString();
        if (in.readByte() == HAS_DATA) {
            connections = new ArrayList<String>();
            in.readList(connections, String.class.getClassLoader());
        } else {
            connections = null;
        }
        if (in.readByte() == HAS_DATA) {
            enterpriseConnectionsUsingWebForm = new ArrayList<String>();
            in.readList(enterpriseConnectionsUsingWebForm, String.class.getClassLoader());
        } else {
            enterpriseConnectionsUsingWebForm = null;
        }
        if (in.readByte() == HAS_DATA) {
            // FIXME this is something to improve
            Bundle mapBundle = in.readBundle();
            authenticationParameters = (HashMap<String, Object>) mapBundle.getSerializable(KEY_AUTHENTICATION_PARAMETERS);
        } else {
            authenticationParameters = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(new Auth0Parcelable(account));
        dest.writeByte((byte) (useBrowser ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (closable ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (fullscreen ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (sendSDKInfo ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (useEmail ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (signUpEnabled ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (changePasswordEnabled ? HAS_DATA : WITHOUT_DATA));
        dest.writeString(defaultDatabaseConnection);
        if (connections == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(connections);
        }
        if (enterpriseConnectionsUsingWebForm == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(enterpriseConnectionsUsingWebForm);
        }
        if (authenticationParameters == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            // FIXME this is something to improve
            Bundle mapBundle = new Bundle();
            mapBundle.putSerializable(KEY_AUTHENTICATION_PARAMETERS, authenticationParameters);
            dest.writeBundle(mapBundle);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LockOptions> CREATOR = new Parcelable.Creator<LockOptions>() {
        @Override
        public LockOptions createFromParcel(Parcel in) {
            return new LockOptions(in);
        }

        @Override
        public LockOptions[] newArray(int size) {
            return new LockOptions[size];
        }
    };
}