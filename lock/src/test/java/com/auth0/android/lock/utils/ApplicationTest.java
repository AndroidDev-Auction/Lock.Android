package com.auth0.android.lock.utils;

import com.auth0.android.lock.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.auth0.android.lock.utils.Strategies.ADFS;
import static com.auth0.android.lock.utils.Strategies.Auth0;
import static com.auth0.android.lock.utils.Strategies.Facebook;
import static com.auth0.android.lock.utils.Strategies.Office365;
import static com.auth0.android.lock.utils.Strategies.SAMLP;
import static com.auth0.android.lock.utils.Strategies.Twitter;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ApplicationTest {

    public static final String ID = "ID";
    public static final String TENANT = "TENANT";
    public static final String AUTHORIZE_URL = "AUTHORIZE";
    public static final String CALLBACK_URL = "CALLBACK";
    public static final String SUBSCRIPTION = "SUBSCRIPTION";
    public static final boolean HAS_ALLOWED_ORIGINS = true;

    @Test
    public void shouldInstantiateApplication() throws Exception {
        Application application = newApplicationWithStrategies(Auth0);
        assertThat(application, is(notNullValue()));
    }

    @Test
    public void shouldHaveApplicationInfo() throws Exception {
        Application application = newApplicationWithStrategies(Auth0);
        assertThat(application.getId(), equalTo(ID));
        assertThat(application.getTenant(), equalTo(TENANT));
        assertThat(application.getAuthorizeURL(), equalTo(AUTHORIZE_URL));
        assertThat(application.getCallbackURL(), equalTo(CALLBACK_URL));
        assertThat(application.getSubscription(), equalTo(SUBSCRIPTION));
        assertThat(application.isHasAllowedOrigins(), equalTo(HAS_ALLOWED_ORIGINS));
    }

    @Test
    public void shouldReturnDatabaseStrategy() throws Exception {
        Application application = newApplicationWithStrategies(Auth0);
        assertThat(application.getDatabaseStrategy(), is(notNullValue()));
        assertThat(application.getDatabaseStrategy().getName(), equalTo(Auth0.getName()));
    }

    @Test
    public void shouldReturnNoDatabaseStrategy() throws Exception {
        Application application = newApplicationWithStrategies(Facebook);
        assertThat(application.getDatabaseStrategy(), is(nullValue()));
    }

    @Test
    public void shouldReturnSocialStrategies() throws Exception {
        Application application = newApplicationWithStrategies(Facebook, Twitter, Auth0);
        assertThat(application.getSocialStrategies().size(), equalTo(2));
    }

    @Test
    public void shouldReturnEnterpriseStrategies() throws Exception {
        Application application = newApplicationWithStrategies(Auth0, ADFS, SAMLP, Office365);
        assertThat(application.getEnterpriseStrategies().size(), equalTo(3));
    }

    private static Strategy newStrategyFor(Strategies strategyMetadata) {
        return new Strategy(strategyMetadata.getName(), Arrays.asList(mock(Connection.class)));
    }

    private static Application newApplicationWithStrategies(Strategies... list) {
        List<Strategy> strategies = new ArrayList<>();
        for (Strategies str : list) {
            strategies.add(newStrategyFor(str));
        }
        Application application = new Application(ID, TENANT, AUTHORIZE_URL, CALLBACK_URL, SUBSCRIPTION, HAS_ALLOWED_ORIGINS, strategies);
        return application;
    }
}