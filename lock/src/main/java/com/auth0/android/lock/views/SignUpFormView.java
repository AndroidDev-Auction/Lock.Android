/*
 * SignUpFormView.java
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

package com.auth0.android.lock.views;

import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.LockActivity;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DbSignUpEvent;
import com.squareup.otto.Bus;

public class SignUpFormView extends FormView {

    private static final String TAG = SignUpFormView.class.getSimpleName();
    private ValidatedInputView usernameInput;
    private ValidatedInputView emailInput;
    private ValidatedInputView passwordInput;

    public SignUpFormView(Context context) {
        super(context);
    }

    public SignUpFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus, configuration);
    }

    @Override
    protected void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_signup_form_view, this);
        usernameInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);
        usernameInput.setVisibility(configuration.isUsernameRequired() ? View.VISIBLE : View.GONE);
        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);
        Button actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_sign_up);
        actionButton.setOnClickListener(this);
    }

    @Override
    protected Object getActionEvent() {
        return new DbSignUpEvent(getEmail(), getUsername(), getPassword());
    }

    public String getUsername() {
        return usernameInput.getText();
    }

    public String getEmail() {
        return emailInput.getText();
    }

    public String getPassword() {
        return passwordInput.getText();
    }

    @Override
    protected boolean hasValidData() {
        boolean valid = emailInput.validate() && passwordInput.validate();
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = valid && usernameInput.validate();
        }
        return valid;
    }
}
