# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontshrink

# Application
-keep @androidx.room.Dao class me.raatiniemi.worker.** { *; }
-keepclassmembers class me.raatiniemi.worker.domain.repository.** { *; }

# AndroidX
-keep class androidx.sqlite.db.SupportSQLiteOpenHelper$Factory { *; }
-keepclassmembers class androidx.sqlite.db.SupportSQLiteOpenHelper { *; }
-keep class androidx.sqlite.db.SupportSQLiteDatabase { *; }
-keepclassmembers class androidx.room.RoomOpenHelper$Delegate { *; }

# Kotlin
-keep class kotlin.jvm.functions.** { *; }

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}

# EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
