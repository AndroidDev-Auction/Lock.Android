/*
 * LockSMSActivity.java
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

package com.auth0.lock.sms;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;
import com.auth0.lock.sms.event.CountryCodeSelectedEvent;
import com.auth0.lock.sms.event.SelectCountryCodeEvent;
import com.auth0.lock.sms.event.SmsPasscodeSentEvent;
import com.auth0.lock.sms.fragment.RequestCodeFragment;
import com.auth0.lock.sms.fragment.SmsLoginFragment;
import com.squareup.otto.Subscribe;


public class LockSMSActivity extends FragmentActivity {

    public static final String REQUEST_SMS_CODE_JWT = "REQUEST_SMS_CODE_JWT";

    private static final String TAG = LockSMSActivity.class.getName();

    private static final int REQUEST_CODE = 0;

    Lock lock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_sms);
        lock = getLock();
        if (savedInstanceState == null) {
            final RequestCodeFragment fragment = new RequestCodeFragment();
            Bundle arguments = new Bundle();
            arguments.putString(RequestCodeFragment.REQUEST_CODE_JWT_ARGUMENT, getIntent().getStringExtra(REQUEST_SMS_CODE_JWT));
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        lock.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        lock.getBus().unregister(this);
    }

    @Subscribe
    public void onSelectCountryCodeEvent(SelectCountryCodeEvent event) {
        Intent intent = new Intent(this, CountryCodeActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Subscribe
    public void onPasscodeSentEvent(SmsPasscodeSentEvent event) {
        final SmsLoginFragment fragment = new SmsLoginFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SmsLoginFragment.PHONE_NUMBER_ARGUMENT, event.getPhoneNumber());
        fragment.setArguments(arguments);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(SmsLoginFragment.class.getName())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String country = data.getStringExtra(CountryCodeActivity.COUNTRY_CODE);
            String dialCode = data.getStringExtra(CountryCodeActivity.COUNTRY_DIAL_CODE);
            Log.d(TAG, "Picked country " + country);
            lock.getBus().post(new CountryCodeSelectedEvent(country, dialCode));
        }
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        LockProvider provider = (LockProvider) getApplication();
        return provider.getLock();
    }
}
