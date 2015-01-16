Lock for Android
============
[![CI Status](http://img.shields.io/travis/auth0/Lock.Android.svg?style=flat)](https://travis-ci.org/auth0/Lock.Android)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

## Key features

* **Integrates** your Android app with **Auth0**.
* Provides a **beautiful native UI** to log your users in.
* Provides support for **Social Providers** (Facebook, Twitter, etc.), **Enterprise Providers** (AD, LDAP, etc.) and **Username & Password**.
* Passwordless authentication using **SMS**.

## Requierements

Android API level 14+ is required in order to use Lock's UI. If you'll create your own API and just call Auth0 API via the `android-core.aar`, the minimum required API level is 9.

##Install

Lock is available both in [Maven Central](http://search.maven.org) and [JCenter](https://bintray.com/bintray/jcenter). To start using *Lock* add these lines to your `build.gradle` dependencies file:

```gradle
compile 'com.auth0:lock:0.1.0'
```

Once it's installed, you'll need to configure LockActivity in your`AndroidManifest.xml`, inside the `application` tag:

```xml
<!--Auth0 Lock-->
<activity
  android:name="com.auth0.lock.LockActivity"
  android:theme="@style/Lock.Theme"
  android:screenOrientation="portrait"
  android:launchMode="singleTask">
  <intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <data android:scheme="a0INSERT_YOUR_APP_CLIENT_ID" android:host="YOUR_ACCOUNT_NAME.auth0.com"/>
  </intent-filter>
</activity>
<meta-data android:name="com.auth0.lock.client-id" android:value="@string/auth0_client_id"/>
<meta-data android:name="com.auth0.lock.tenant" android:value="@string/auth0_tenant_name"/>
<!--Auth0 Lock End-->
```

> The value `@string/auth0_client_id` is your application's clientID and `@string/auth0_tenant_name` is the name of the account that owns the application.

Finally, Make your Application class (The one that extends from `android.app.Application`) implement the interface `com.auth0.lock.LockProvider` and add the following code:

```java
public class MyApplication extends Application implements LockProvider {

  @Override
  private Lock lock;

  public void onCreate() {
    super.onCreate();
    lock = new LockBuilder()
      .loadFromApplication(this)
      <!-- Other configuration goes here -->
      .closable(true)
      .build();
  }

  @Override
  public Lock getLock() {
    return lock;
  }
}
```

## Usage

### Email/Password, Enterprise & Social authentication

`LockActivity` will handle Email/Password, Enterprise & Social authentication based on your Application's connections enabled in your Auth0's Dashboard.

When a user authenticates successfully, LockActivity will send an Action using LocalBroadcaster manager and then finish itself (by calling finish()). The activity that is interested in receiving this Action (In this case the one that will show Lock) needs to register a listener in the LocalBroadcastManager:

```java
// This activity will show Lock
public class HomeActivity extends Activity {

  private LocalBroadcastManager broadcastManager;

  private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      UserProfile profile = intent.getParcelableExtra("profile");
      Token token = intent.getParcelableExtra("token");
      Log.i(TAG, "User " + profile.getName() + " logged in");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Customize your activity

    broadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(Lock.AUTHENTICATION_ACTION));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    broadcastManager.unregisterReceiver(authenticationReceiver);
  }
}
```

Then just start `LockActivity`

```java
Intent lockIntent = new Intent(this, LockActivity.class);
startActivity(lockIntent);
```
And you'll see our native login screen

[![Lock.png](http://blog.auth0.com.s3.amazonaws.com/Lock-Widget-Android-Screenshot.png)](https://auth0.com)

> By default all social authentication will be done using an external browser, if you want native integration please check this [wiki page](https://github.com/auth0/Lock.Android/wiki/Native-Social-Authentication).

### SMS

`LockSMSActivity` authenticates users by sending them an SMS (Similar to how WhatsApp authenticates you). In order to be able to authenticate the user, your application must have the SMS connection enabled and configured in your [dashboard](https://manage.auth0.com/#/connections/passwordless).

`LockSMSActivity` is not included in `com.auth0:lock:aar` but you can add it with this line in your `build.gradle`:
```gradle
compile 'com.auth0.lock-sms:0.1.0'
```

When a user authenticates successfully, LockActivity will send an Action using LocalBroadcaster manager and then finish itself (by calling finish()). The activity that is interested in receiving this Action (In this case the one that will show Lock) needs to register a listener in the LocalBroadcastManager:

```java
// This activity will show Lock
public class HomeActivity extends Activity {
  private LocalBroadcastManager broadcastManager;

  private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      UserProfile profile = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
      Token token = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER);
      Log.i(TAG, "User " + profile.getName() + " logged in");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Customize your activity

    broadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(Lock.AUTHENTICATION_ACTION));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    broadcastManager.unregisterReceiver(authenticationReceiver);
  }
}
```

Then just start `LockSMSActivity`

```java
Intent smsIntent = new Intent(this, LockSMSActivity.class);
smsIntent.putExtra(LockSMSActivity.REQUEST_SMS_CODE_JWT, "API v2 JWT");
startActivity(smsIntent);
```

> The value for `LockSMSActivity.REQUEST_SMS_CODE_JWT` is an API Token used to register the  phone number and send the login code with SMS. This token can be generated in  [Auth0 API v2 page](https://docs.auth0.com/apiv2), just select the scope `create:users` and copy the generated API Token.

And you'll see SMS login screen

[![Lock.png](http://blog.auth0.com.s3.amazonaws.com/Lock-SMS-Android-Screenshot.png)](https://auth0.com)

##API

###Lock

####Constants

```java
public static final String AUTHENTICATION_ACTION;
```
Action sent in `LocalBroadcastManager` when a user authenticates. It will include an instance of `UserProfile` and `Token`.

```java
public static final String AUTHENTICATION_ACTION_PROFILE_PARAMETER;
```
Name of the parameter that will include user's profile

```java
public static final String AUTHENTICATION_ACTION_TOKEN_PARAMETER;
```
Name of the parameter that will include user's token information

```java
public static final String CANCEL_ACTION;
```
Action sent when the user navigates back closing `LockActivity` or `LockSMSActivity`

```java
public static final String CANCEL_ACTION;
```
Action sent when the user change its password

####Properties
```java
public boolean shouldUseWebView();
public void setUseWebView(boolean useWebView);
```
Forces Lock to use an embedded `android.webkit.WebView` and by  default is `false`.

```java
public boolean shouldLoginAfterSignUp();
public boolean setLoginAfterSignUp(boolean loginAfterSignUp);
```
Lock will login the user after a successful sign up. By default is `true`

```java
public boolean isClosable();
public boolean setClosable(boolean closable);
```
Allows Lock activities to be closed by pressing back button. Default is `false`

```java
public boolean shouldUseEmail();
public void setUseEmail(boolean useEmail);
```
Lock will ask for the user's email instead of a username. By default is `true`.

```java
public Map<String, Object> getAuthenticationParameters();
public void setAuthenticationParameters(Map<String, Object> authenticationParameters);
```
Map with parameters that will be sent on every authentication request with Auth0 API.

####Methods

```java
public void setProvider(String serviceName, IdentityProvider provider);
```
Change the default identity provider handler for Social and Enterprise connections. By default all social/enterprise authentication are done using Web flow with a Browser.

```java
public void resetAllProviders();
```
Removes all session information the Identity Provider handlers might have.

###LockBuilder
A simple builder to help you create and configure Lock in your  application.

####Constants

```java
public static final String CLIENT_ID_KEY = "com.auth0.lock.client-id";
```
Key value used by Lock to search in your application's meta-data for the ClientID.
```java
public static final String TENANT_KEY = "com.auth0.lock.tenant";
```
Key value used by Lock to search in your application's meta-data for tenant name.
```java
public static final String DOMAIN_URL_KEY = "com.auth0.lock.domain-url";
```
Key value used by Lock to search in your application's meta-data for domain Url.
```java
public static final String CONFIGURATION_URL_KEY = "com.auth0.lock.configuration-url";
```
Key value used by Lock to search in your application's meta-data for configuration Url.

####Methods

```java
public LockBuilder clientId(String clientId);
```
Set the clientId of your application in Auth0. This value is mandatory.

```java
public LockBuilder tenant(String tenant);
```
Set the tenant name of your application. This value is optional if you supply a domain url.

```java
public LockBuilder domainUrl(String domain);
```
Set the domain Url for Auth0's API. This value is optional if you provide a tenant name, it will default to Auth0 cloud API `https://tenant_name.auth0.com`.

```java
public LockBuilder configurationUrl(String configuration);
```
Set the Url where Lock fetches the App configuration. By default it asks Auth0 for this info.

```java
public LockBuilder useWebView(boolean useWebView);
```
Make Lock use an embedded WebView for Social+Enterprise authentications.

```java
public LockBuilder closable(boolean closable);
```
Allow the user to close Lock's activity by pressing back button.

```java
public LockBuilder loginAfterSignUp(boolean loginAfterSignUp);
```
After a successful sign up of a user, sign him/her in too.

```java
public LockBuilder authenticationParameters(Map<String, Object> parameters);
```
Extra parameters sent to Auth0 Auth API during authentication. By default it has `scope` defined as `openid offline_access` and a device name stored in `device` parameter key.

```java
public LockBuilder useEmail(boolean useEmail);
```
Lock will ask for an email for authentication, otherwise it will ask for a username. By default is `true`.

```java
public LockBuilder loadFromApplication(Application application);
```
Load ClientID, Tenant name, Domain and configuration URLs from the Android app's metadata (if available).
These are the values that can be defined and it's keys:
* __com.auth0.lock.client-id__: Application's clientId in Auth0.
* __com.auth0.lock.tenant__: Application's owner tenant name. (Optional if you supply Domain and Configuration URLs)
* __com.auth0.lock.domain-url__: URL where the Auth0 API is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)
* __com.auth0.lock.configuration-url__: URL where Auth0 apps information is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)

```java
public Lock build();
```
Creates a new instance of `Lock` and configure it with the values passed to the builder.

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple authentication sources](https://docs.auth0.com/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, amont others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.auth0.com/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.auth0.com/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.auth0.com/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.auth0.com/rules).

## Create a free account in Auth0

1. Go to [Auth0](https://auth0.com) and click Sign Up.
2. Use Google, GitHub or Microsoft Account to login.

## Author

Auth0

## License

Lock is available under the MIT license. See the [LICENSE file](LICENSE) for more info.
