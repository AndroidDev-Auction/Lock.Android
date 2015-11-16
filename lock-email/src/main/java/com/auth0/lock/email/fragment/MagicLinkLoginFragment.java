/*
 * MagicLinkLoginFragment.java
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

package com.auth0.lock.email.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.lock.email.R;
import com.auth0.lock.email.event.CodeManualEntryRequestedEvent;
import com.auth0.lock.email.event.EmailVerificationCodeRequestedEvent;
import com.auth0.lock.fragment.BaseTitledFragment;

public class MagicLinkLoginFragment extends BaseTitledFragment {

    public static final String EMAIL_ARGUMENT = "EMAIL_ARGUMENT";

    private String email;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            email = arguments.getString(EMAIL_ARGUMENT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_magic_link_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button enterCodeButton = (Button) view.findViewById(R.id.com_auth0_email_enter_code_button);
        enterCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new CodeManualEntryRequestedEvent());
            }
        });

        final Button resendCodeButton = (Button) view.findViewById(R.id.com_auth0_email_resend_code_button);
        resendCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendCodeButton.setVisibility(View.INVISIBLE);
                bus.post(new EmailVerificationCodeRequestedEvent(email));
                new ShowLaterTask().execute(resendCodeButton);
            }
        });

        TextView messageTextView = (TextView) view.findViewById(R.id.com_auth0_email_magic_link_message);
        String messageFormat = getString(R.string.com_auth0_email_login_message_magic_link);
        messageTextView.setText(Html.fromHtml(String.format(messageFormat, email)));

        new ShowLaterTask().execute(resendCodeButton);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_email_title_magic_link;
    }

    private class ShowLaterTask extends AsyncTask<View, Void, Boolean> {

        View view;

        @Override
        protected Boolean doInBackground(View... params) {
            try {
                view = params[0];
                Thread.sleep(10000);
                return true;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                view.setVisibility(View.VISIBLE);
            }
        }
    }
}
