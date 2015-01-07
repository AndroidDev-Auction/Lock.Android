/*
 * LockFragmentBuilderTest.java
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

import android.os.Parcelable;
import android.support.v4.app.Fragment;

import com.auth0.core.Application;
import com.auth0.core.Connection;
import com.auth0.core.Strategy;
import com.auth0.lock.Lock;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.DatabaseResetPasswordFragment;
import com.auth0.lock.fragment.DatabaseSignUpFragment;
import com.auth0.lock.fragment.SocialDBFragment;
import com.auth0.lock.fragment.SocialFragment;
import com.auth0.lock.util.LockFragmentBuilder;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by hernan on 12/16/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LockFragmentBuilderTest {

    private LockFragmentBuilder builder;

    @Mock
    private Application application;
    @Mock
    private Strategy socialStrategy;
    @Mock
    private Strategy enterpriseStrategy;
    @Mock
    private Strategy adStrategy;
    @Mock
    private Strategy databaseStrategy;
    @Mock
    private Connection adConnection;
    @Mock
    private Lock lock;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new LockFragmentBuilder(lock);
        builder.setApplication(application);
        when(adStrategy.getConnections()).thenReturn(Arrays.asList(adConnection));
    }

    @Test
    public void shouldReturnDefaultRootFragment() throws Exception {
        assertThat(builder.root(), is(DatabaseLoginFragment.class));
    }

    @Test
    public void shouldReturnDBLogin() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(databaseStrategy);
        when(application.getEnterpriseStrategies()).thenReturn(new ArrayList<Strategy>());
        when(application.getSocialStrategies()).thenReturn(new ArrayList<Strategy>());
        assertThat(builder.root(), is(DatabaseLoginFragment.class));
    }


    @Test
    public void shouldReturnDBLoginPlusEnterprise() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(databaseStrategy);
        when(application.getEnterpriseStrategies()).thenReturn(Arrays.asList(enterpriseStrategy));
        when(application.getSocialStrategies()).thenReturn(new ArrayList<Strategy>());
        assertThat(builder.root(), is(DatabaseLoginFragment.class));
    }

    @Test
    public void shouldReturnEnterprise() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(null);
        when(application.getEnterpriseStrategies()).thenReturn(Arrays.asList(enterpriseStrategy));
        when(application.getSocialStrategies()).thenReturn(new ArrayList<Strategy>());
        assertThat(builder.root(), is(DatabaseLoginFragment.class));
    }

    @Test
    public void shouldReturnSocial() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(null);
        when(application.getEnterpriseStrategies()).thenReturn(new ArrayList<Strategy>());
        when(application.getSocialStrategies()).thenReturn(Arrays.asList(socialStrategy));
        assertThat(builder.root(), is(SocialFragment.class));
    }

    @Test
    public void shouldReturnDBPlusSocial() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(databaseStrategy);
        when(application.getEnterpriseStrategies()).thenReturn(new ArrayList<Strategy>());
        when(application.getSocialStrategies()).thenReturn(Arrays.asList(socialStrategy));
        assertThat(builder.root(), is(SocialDBFragment.class));
    }

    @Test
    public void shouldReturnEnterprisePlusSocial() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(null);
        when(application.getEnterpriseStrategies()).thenReturn(Arrays.asList(enterpriseStrategy));
        when(application.getSocialStrategies()).thenReturn(Arrays.asList(socialStrategy));
        assertThat(builder.root(), is(SocialDBFragment.class));
    }

    public void shouldSetDefaultConnectionWithSocial() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(null);
        when(application.getEnterpriseStrategies()).thenReturn(Arrays.asList(enterpriseStrategy, adStrategy));
        when(application.getSocialStrategies()).thenReturn(Arrays.asList(socialStrategy));
        final Fragment root = builder.root();
        assertThat(root.getArguments(), is(notNullValue()));
        assertThat(root.getArguments().getParcelable(DatabaseLoginFragment.DEFAULT_CONNECTION_ARGUMENT), Matchers.<Parcelable>equalTo(adConnection));
    }

    public void shouldSetDefaultConnectionWithoutSocial() throws Exception {
        when(application.getDatabaseStrategy()).thenReturn(null);
        when(application.getEnterpriseStrategies()).thenReturn(Arrays.asList(enterpriseStrategy, adStrategy));
        when(application.getSocialStrategies()).thenReturn(new ArrayList<Strategy>());
        final Fragment root = builder.root();
        assertThat(root.getArguments(), is(notNullValue()));
        assertThat(root.getArguments().getParcelable(DatabaseLoginFragment.DEFAULT_CONNECTION_ARGUMENT), Matchers.<Parcelable>equalTo(adConnection));
    }

    @Test
    public void shouldReturnSignUp() throws Exception {
        assertThat(builder.signUp(), is(DatabaseSignUpFragment.class));
    }

    @Test
    public void shouldReturnResetPassword() throws Exception {
        assertThat(builder.resetPassword(), is(DatabaseResetPasswordFragment.class));
    }
}
