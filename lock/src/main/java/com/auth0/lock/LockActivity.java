package com.auth0.lock;

import android.os.Bundle;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.core.UserProfile;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.LoadingFragment;
import com.auth0.lock.provider.BusProvider;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboFragmentActivity;


public class LockActivity extends RoboFragmentActivity {

    @Inject private BusProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoadingFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.provider.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.provider.getBus().unregister(this);
    }

    @Subscribe public void onApplicationLoaded(Application application) {
        Log.d(LockActivity.class.getName(), "Application configuration loaded for id " + application.getId());
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new DatabaseLoginFragment())
                .commit();
    }

    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Log.i(LockActivity.class.getName(), "Authenticated user " + profile.getName());
        finish();
    }

    @Subscribe public void onThrowable(Throwable throwable) {
        Log.e(LockActivity.class.getName(), "Failed to authenticate user", throwable);
    }
}
