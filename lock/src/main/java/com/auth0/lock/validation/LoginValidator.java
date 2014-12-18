/*
 * LoginValidator.java
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

package com.auth0.lock.validation;

import android.app.Fragment;

import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

/**
 * Created by hernan on 12/16/14.
 */
public class LoginValidator implements Validator {

    private final Validator usernameValidator;
    private final Validator passwordValidator;

    public LoginValidator(Validator usernameValidator, Validator passwordValidator) {
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
    }

    public LoginValidator() {
        this(new EmailValidator(R.id.db_login_username_field, R.string.invalid_credentials_title, R.string.invalid_email_message),
                new PasswordValidator(R.id.db_login_password_field, R.string.invalid_credentials_title, R.string.invalid_password_message));
    }

    @Override
    public AuthenticationError validateFrom(Fragment fragment) {
        final AuthenticationError usernameError = usernameValidator.validateFrom(fragment);
        final AuthenticationError passwordError = passwordValidator.validateFrom(fragment);
        if (usernameError != null && passwordError != null) {
            return new AuthenticationError(R.string.invalid_credentials_title, R.string.invalid_credentials_message);
        }
        return usernameError != null ? usernameError : passwordError;
    }

}
