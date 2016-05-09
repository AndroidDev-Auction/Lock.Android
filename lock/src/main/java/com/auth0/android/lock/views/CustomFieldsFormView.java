/*
 * CustomFieldsFormView.java
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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.CustomField;
import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomFieldsFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = CustomFieldsFormView.class.getSimpleName();

    private LockWidgetForm lockWidget;
    private List<CustomField> fieldsData;
    private DatabaseSignUpEvent userData;
    private TextView title;
    private LinearLayout fieldContainer;
    private ValidatedInputView usernameField;
    private ValidatedInputView passwordField;

    public CustomFieldsFormView(Context context) {
        super(context);
    }

    public CustomFieldsFormView(LockWidgetForm lockWidget, DatabaseSignUpEvent userData) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.userData = userData;
        this.fieldsData = lockWidget.getConfiguration().getExtraSignUpFields();
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_custom_fields_form_view, this);
        title = (TextView) findViewById(R.id.com_auth0_lock_title);
        fieldContainer = (LinearLayout) findViewById(R.id.com_auth0_lock_container);

        if (lockWidget.getConfiguration().getExtraSignUpFields().size() == 1) {
            addDisabledFields();
        }
        addCustomFields();
    }

    private void addDisabledFields() {
        int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, verticalMargin / 2, 0, verticalMargin / 2);

        boolean showEmail = false;
        boolean showUsername = false;
        if (lockWidget.getConfiguration().isUsernameRequired()) {
            showEmail = true;
            showUsername = true;
        } else if (lockWidget.getConfiguration().getUsernameStyle() == UsernameStyle.USERNAME) {
            showUsername = true;
        } else if (lockWidget.getConfiguration().getUsernameStyle() == UsernameStyle.EMAIL || lockWidget.getConfiguration().getUsernameStyle() == UsernameStyle.DEFAULT) {
            showEmail = true;
        }

        if (showEmail && showUsername) {
            ValidatedInputView emailField = new ValidatedInputView(getContext());
            emailField.setDataType(ValidatedInputView.DataType.EMAIL);
            emailField.setLayoutParams(params);
            emailField.setText(userData.getEmail());
            emailField.setEnabled(false);
            fieldContainer.addView(emailField);

            usernameField = new ValidatedInputView(getContext());
            usernameField.setDataType(ValidatedInputView.DataType.USERNAME);
            usernameField.setLayoutParams(params);
            fieldContainer.addView(usernameField);
        } else {
            ValidatedInputView emailOrUsernameField = new ValidatedInputView(getContext());
            emailOrUsernameField.setDataType(ValidatedInputView.DataType.USERNAME_OR_EMAIL);
            emailOrUsernameField.setLayoutParams(params);
            emailOrUsernameField.setText(userData.getUsername() == null ? userData.getEmail() : userData.getUsername());
            emailOrUsernameField.setEnabled(false);
            fieldContainer.addView(emailOrUsernameField);
        }

        passwordField = new ValidatedInputView(getContext());
        passwordField.setDataType(ValidatedInputView.DataType.PASSWORD);
        passwordField.setLayoutParams(params);
        fieldContainer.addView(passwordField);
    }

    private void addCustomFields() {
        Log.d(TAG, String.format("Adding %d custom fields.", fieldsData.size()));
        int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, verticalMargin / 2, 0, verticalMargin / 2);

        for (CustomField data : fieldsData) {
            ValidatedInputView field = new ValidatedInputView(getContext());
            data.configureField(field);
            field.setLayoutParams(params);
            fieldContainer.addView(field);
        }
    }

    private Map<String, String> getCustomFieldValues() {
        Map<String, String> map = new HashMap<>();
        for (CustomField data : fieldsData) {
            map.put(data.getKey(), data.findValue(fieldContainer));
        }
        Log.d(TAG, "Custom field values are" + map.values().toString());

        return map;
    }

    @Override
    public Object getActionEvent() {
        if (lockWidget.getConfiguration().getExtraSignUpFields().size() == 1) {
            userData.setUsername(usernameField != null ? usernameField.getText() : null);
            userData.setPassword(passwordField.getText());
        }
        userData.setExtraFields(getCustomFieldValues());
        return userData;
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        for (int i = 0; i < fieldContainer.getChildCount(); i++) {
            ValidatedInputView input = (ValidatedInputView) fieldContainer.getChildAt(i);
            if (input.isEnabled()) {
                valid = input.validate(true) && valid;
            }
        }
        Log.d(TAG, "Is form data valid? " + valid);
        return valid;
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public void onKeyboardStateChanged(boolean isOpen) {
        title.setVisibility(isOpen ? GONE : VISIBLE);
    }
}
