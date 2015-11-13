package com.auth0.identity.web;

import android.content.Intent;
import android.net.Uri;

import com.auth0.android.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class LinkParserTest {

    LinkParser linkParser;

    @Before
    public void setUp() throws Exception {
        linkParser = new LinkParser();
    }

    @Test
    public void testCodeFromAppLinkUri() throws Exception {
        assertNull(linkParser.getCodeFromAppLinkUri(null));
        assertNull(linkParser.getCodeFromAppLinkUri(""));
        assertNull(linkParser.getCodeFromAppLinkUri("http://example.com/"));
        assertNull(linkParser.getCodeFromAppLinkUri("thisshouldreturnnull"));
        assertEquals("567234", linkParser.getCodeFromAppLinkUri("https://tenant.auth0.com/android/com.example.app/email?code=567234"));
        assertNotEquals("234567", linkParser.getCodeFromAppLinkUri("https://tenant.auth0.com/android/com.example.app/email?code=567234"));
    }

    @Test
    public void testCodeFromAppLinkIntent() throws Exception {
        Intent validIntent = new Intent(Intent.ACTION_VIEW);
        validIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals("234567", linkParser.getCodeFromAppLinkIntent(validIntent));

        Intent invalidIntent = new Intent();
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setAction(Intent.ACTION_VIEW);
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setData(Uri.parse("https://example.com/"));
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));
    }
}