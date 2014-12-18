package com.auth0.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.LockActivity;

import static com.auth0.app.R.id;
import static com.auth0.app.R.layout;

public class MyActivity extends Activity {

    private static final int AUTHENTICATION_REQUEST = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_my);
        final Button loginButton = (Button) findViewById(id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(MyActivity.this, LockActivity.class);
                startActivityForResult(loginIntent, AUTHENTICATION_REQUEST);
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTHENTICATION_REQUEST) {
            if (resultCode == RESULT_OK) {
                UserProfile profile = data.getParcelableExtra("profile");
                Token token = data.getParcelableExtra("token");
                Log.d(MyActivity.class.getName(), "User " + profile.getName() + " with token " + token.getIdToken());
                TextView welcomeLabel = (TextView) findViewById(id.welcome_label);
                welcomeLabel.setText("Herzlich Willkommen " + profile.getName());
            }
        }
    }
}
