package com.auth0.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.api.callback.RefreshIdTokenCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockActivity;
import com.auth0.lock.LockProvider;
import com.auth0.lock.sms.LockSMSActivity;

import static com.auth0.app.R.id;
import static com.auth0.app.R.layout;

public class MyActivity extends ActionBarActivity {

    public static final String TAG = MyActivity.class.getName();

    LocalBroadcastManager broadcastManager;
    Token token;
    UserProfile profile;
    Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(id.lock_toolbar);
        setSupportActionBar(toolbar);
        final Button loginButton = (Button) findViewById(id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MyActivity.this, LockActivity.class);
                startActivity(loginIntent);
            }
        });

        final Button smsButton = (Button) findViewById(id.sms_button);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(MyActivity.this, LockSMSActivity.class);
                smsIntent.putExtra(LockSMSActivity.REQUEST_SMS_CODE_JWT, getString(R.string.request_sms_code_jwt));
                startActivity(smsIntent);
            }
        });

        refreshButton = (Button) findViewById(id.refresh_jwt_button);
        refreshButton.setEnabled(false);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockProvider lockProvider = (LockProvider) getApplication();
                Lock lock = lockProvider.getLock();
                lock.getAPIClient().fetchIdTokenWithRefreshToken(token.getRefreshToken(), null, new RefreshIdTokenCallback() {
                    @Override
                    public void onSuccess(String idToken, String tokenType, int expiresIn) {
                        Log.d(TAG, "User " + profile.getName() + " with new token " + idToken);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        Log.e(TAG, "Failed to refresh JWT", error);
                    }
                });
            }
        });
        broadcastManager = LocalBroadcastManager.getInstance(this);
        broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(Lock.AUTHENTICATION_ACTION));
        broadcastManager.registerReceiver(cancelReceiver, new IntentFilter(Lock.CANCEL_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        broadcastManager.unregisterReceiver(authenticationReceiver);
        broadcastManager.unregisterReceiver(cancelReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        profile = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
        token = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER);
        if (token != null) {
            Log.d(TAG, "User " + profile.getName() + " with token " + token.getIdToken());
            TextView welcomeLabel = (TextView) findViewById(id.welcome_label);
            welcomeLabel.setText("Herzlich Willkommen " + profile.getName());
        } else {
            Log.i(TAG, "Signed Up user");
        }
        refreshButton.setEnabled(token != null && token.getRefreshToken() != null);
        }
    };

    private BroadcastReceiver cancelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "User Cancelled");
        }
    };

}
