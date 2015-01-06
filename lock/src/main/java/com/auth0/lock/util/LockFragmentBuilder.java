/*
 * LockFragmentBuilder.java
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

package com.auth0.lock.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.auth0.core.Application;
import com.auth0.core.Strategy;
import com.auth0.lock.Lock;
import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.DatabaseResetPasswordFragment;
import com.auth0.lock.fragment.DatabaseSignUpFragment;
import com.auth0.lock.fragment.SocialDBFragment;
import com.auth0.lock.fragment.SocialFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hernan on 12/16/14.
 */
public class LockFragmentBuilder {

    private final Lock lock;
    private Application application;

    public LockFragmentBuilder(Lock lock) {
        this.lock = lock;
    }

    public Fragment signUp() {
        final DatabaseSignUpFragment fragment = new DatabaseSignUpFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(DatabaseSignUpFragment.LOGIN_AFTER_SIGNUP_ARGUMENT, lock.isLoginAfterSignUp());
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.isUseEmail());
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        fragment.setArguments(arguments);
        return fragment;
    }

    public Fragment resetPassword() {
        final DatabaseResetPasswordFragment fragment = new DatabaseResetPasswordFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.isUseEmail());
        fragment.setArguments(arguments);
        return fragment;
    }

    public Fragment login() {
        final DatabaseLoginFragment fragment = new DatabaseLoginFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.isUseEmail());
        fragment.setArguments(arguments);
        return fragment;
    }


    public Fragment socialLogin() {
        final SocialDBFragment fragment = new SocialDBFragment();
        if (application != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(SocialDBFragment.SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, activeSocialStrategies());
            bundle.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.isUseEmail());
            bundle.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public Fragment social() {
        final SocialFragment fragment = new SocialFragment();
        if (application != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(SocialFragment.SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, activeSocialStrategies());
            bundle.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public Fragment root() {
        if (application == null) {
            return login();
        }

        if (application.getDatabaseStrategy() == null && application.getSocialStrategies().size() > 0) {
            return social();
        }

        if (application.getDatabaseStrategy() != null && application.getSocialStrategies().size() > 0) {
            return socialLogin();
        }

        return login();
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    private ArrayList<String> activeSocialStrategies() {
        ArrayList<String> strategies = new ArrayList<>(application.getSocialStrategies().size());
        for (Strategy strategy : application.getSocialStrategies()) {
            strategies.add(strategy.getName());
        }
        return strategies;
    }
}
