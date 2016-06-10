# Lock.Android v2

## Enums
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

## Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

-keepclassmembers class ** {
    com.auth0.android.lock.views.ViewUtils$Corners *;
}

## Data Models / POJOs
-keep class com.auth0.android.lock.utils.Strategies { *; }
-keep class com.auth0.android.lock.utils.json.Strategy { *; }
-keep class com.auth0.android.lock.utils.json.Connection { *; }
-keep class com.auth0.android.lock.utils.json.Application { *; }