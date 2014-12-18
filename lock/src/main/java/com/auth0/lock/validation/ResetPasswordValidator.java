/*
 * ResetPasswordValidator.java
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
 * Created by hernan on 12/15/14.
 */
public class ResetPasswordValidator implements Validator {

    private Validator emailValidator;
    private Validator passwordValidator;
    private Validator repeatPasswordValidator;

    public ResetPasswordValidator(Validator emailValidator, Validator passwordValidator, Validator repeatPasswordValidator) {
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.repeatPasswordValidator = repeatPasswordValidator;
    }

    public ResetPasswordValidator() {
        this(
            new EmailValidator(R.id.db_reset_password_username_field, R.string.invalid_credentials_title, R.string.invalid_email_message),
            new PasswordValidator(R.id.db_reset_password_password_field, R.string.invalid_credentials_title, R.string.invalid_password_message),
            new RepeatPasswordValidator(R.id.db_reset_password_repeat_password_field, R.id.db_reset_password_password_field, R.string.invalid_credentials_title, R.string.db_reset_password_invalid_repeat_password_message)
        );
    }

    @Override
    public AuthenticationError validateFrom(Fragment fragment) {
        AuthenticationError emailError = emailValidator.validateFrom(fragment);
        AuthenticationError passwordError = passwordValidator.validateFrom(fragment);
        AuthenticationError repeatError = repeatPasswordValidator.validateFrom(fragment);
        if (emailError != null && (passwordError != null || repeatError != null)) {
            return new AuthenticationError(R.string.invalid_credentials_title, R.string.invalid_credentials_message);
        }
        if (repeatError != null) {
            return repeatError;
        }
        return passwordError != null ? passwordError : emailError;
    }
}
