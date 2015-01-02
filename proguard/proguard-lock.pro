-keep class com.auth0.core.** { *; }
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn com.actionbarsherlock.**
-dontwarn com.google.android.maps.MapActivity
-dontwarn roboguice.fragment.RoboSherlock*
-dontwarn roboguice.activity.RoboSherlock*
-dontwarn roboguice.activity.RoboMapActivity
-dontwarn roboguice.activity.Sherlock*

-keepclassmembers class com.auth0.lock.provider.BusProvider {
    public <init> (...);
}

-keepclassmembers class com.auth0.lock.LockFragmentBuilder {
    public <init> (...);
}

-keepclassmembers class com.auth0.lock.error.* {
    public <init> (...);
}

-keepclassmembers class com.auth0.lock.validation.* {
    public <init> (...);
}

