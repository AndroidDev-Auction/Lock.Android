package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import com.auth0.android.lock.R;

/**
 * A custom AppCompatTextView implementation that changes the background drawable when focused.
 */
public class LinkTextView extends AppCompatTextView {
    private Drawable focusedBackground;

    public LinkTextView(Context context) {
        super(context);
        init();
    }

    public LinkTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LinkTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        focusedBackground = ContextCompat.getDrawable(getContext(), R.drawable.com_auth0_lock_link_background);
        setFocusableInTouchMode(false);
        setFocusable(true);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ViewUtils.setBackground(this, gainFocus ? focusedBackground : null);
    }
}
