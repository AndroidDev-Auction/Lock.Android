# Change Log
All notable changes to this project will be documented in this file.

##master

###Addded
- Show rule error if the code is `unauthorized`. [#57](issues/57)

# 1.5.1 - 2015-04-28

###Changed
- Social GridView styles are extracted to `Lock.Theme.Social.GridView`. They can be modified using `Auth0.GridView.Small` attribute for Lock Theme.

# 1.5.0 - 2015-04-27

###Added
- DB connection's `requires_username` feature. Allows to signup with email, username and password, and login with either email or username.
- `waad` and `adfs` connections use `/ro` to authenticate.

# 1.4.0 - 2015-04-16

###Added
- Fullscreen mode for `LockActivity` & `LockSMSActivity`

# 1.3.3 - 2015-04-13

###Changed
- Fix NPE when loading Auth0 info from AndroidManifest.xml.

# 1.3.2 - 2015-04-12

###Changed
- Fixed NPE when using domain to configure Lock.
- Fixed NPE when no DB connection is enabled.
- Allow enterprise connections to use WebView instead of Browser app.

## 1.3.1 - 2015-03-27

###Changed
- Fix issue with scope when authenticating with web flow.
- Correctly set default scope to `openid offline_access`.

## 1.3.0 - 2015-03-12

###Added
- Filter Application connections when initialising Lock.
- SignUp actitivy with social buttons.
- Specify the default Database connection.

###Changed
- Fixed localization issues where strings were not properly localized.


## 1.2.0 - 2015-02-26
### Added
- Delegation API methods in `APIClient`

###Changed
- Fixed issue when small social buttons were clipped in some devices [#44](issues/44)
- Fixed UI issue in Lock when the application only has a single Enterprise Connection.

## 1.1.0 - 2015-01-27
### Changed
- Extracted Identity Provider (IdP) logic to a independent module (only requires core lib).
- Fixed issue when AD login fragment is the main screen.
- Fixed NPE when authentication using a `WebView` is cancelled by the user

## 1.0.1 - 2015-01-26
### Changed
- Fixed issue when application has only one connection and is AD. [#40](issues/40)

## 1.0.0 - 2015-01-19
### Added
- Initial release of Lock for Android
- Database + Social + Enterprise authentication
- Native integration with *Facebook* and *Google+*
- Lock UI implemented in `LockActivity`
- Authentication API Java client
- SMS Authentication (with `LockSMSActivity`)
