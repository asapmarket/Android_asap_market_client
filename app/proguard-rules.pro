# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xusheng/xusheng/software_install/android-sdk-mac/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keepattributes *Annotation*

-keepattributes Signature

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-ignorewarnings

-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

-keep public class de.greenrobot.** {*;}
-dontwarn de.greenrobot.**

-keepclassmembers public class com.mhysa.waimai.** {
    public void onEvent(***);
}

-keep public class com.mhysa.waimai.model.** {
  private <fields>;
  public <fields>;
  public void set*(***);
  public *** get*();
  public *** is*();
}

-keep public class com.mhysa.waimai.ui.customerviews.** {
  private <fields>;
  public <fields>;
  public void set*(***);
  public *** get*();
  public *** is*();
}

-keep class **.R$* {
 *;
}

-keep public class **.R$*{
   public static final int *;
}

#-libraryjars libs/android-support-v4.jar
-dontwarn android.support.v4.**
-keep class android.support.v4.**  { *; }
-keep interface android.support.v4.app.** { *; }
-keep public class * extends android.support.v4.**
-keep public class * extends android.app.Fragment


-dontwarn android.support.design.**
-keep class android.support.design.**  { *; }
-keep interface android.support.design.app.** { *; }
-keep public class * extends android.support.design.**

-dontwarn android.support.v7.**
-keep class android.support.v7.**  { *; }
-keep interface android.support.v7.app.** { *; }
-keep public class * extends android.support.v7.**

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keepclasseswithmembernames class * {
 native <methods>;
}
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}
-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}

# Keep gson.
-dontwarn com.google.gson.**
-keep class com.google.gson.** {*;}

-keep public class com.mhysa.waimai.event.WtApplicationEvent*{
public <fields>;
      public <methods>;
}

-keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip

# Do not strip any method/class that is annotated with @DoNotStrip
-keep @com.facebook.common.internal.DoNotStrip class *
-keepclassmembers class * {
    @com.facebook.common.internal.DoNotStrip *;
}

# Keep native methods
-keepclassmembers class * {
    native <methods>;
}

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn okhttp3.**
-keep class com.squareup.okhttp.** { *;}

-dontwarn com.zhy.http.okhttp.*
-keep class com.zhy.http.okhttp.** { *;}
-keep class okhttp3.** { *;}
-keep class okio.** { *;}

-keep class com.mhysa.waimai.http.** { *; }

-dontwarn javax.annotation.**
-dontwarn com.android.volley.toolbox.**
-dontwarn com.facebook.infer.**

-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
        @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
        @butterknife.* <methods>;
}

-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {

    private static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepattributes *Annotation*
-keepclassmembers class ** {
        @org.greenrobot.eventbus.Subscribe <methods>;
}

-keep enum org.greenrobot.eventbus.ThreadMode { *; }

    # Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
        <init>(Java.lang.Throwable);
}
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

-keep class com.joey.devilfish.** { *;}

-keep public class com.mhysa.waimai.http.OkHttpClientManager*{
public <fields>;
      public <methods>;
}