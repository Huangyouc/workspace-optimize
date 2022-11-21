# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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
#############DeviceIdentifier########################
-keep class repeackage.com.uodis.opendevice.aidl.** { *; }
-keep interface repeackage.com.uodis.opendevice.aidl.** { *; }
-keep class repeackage.com.asus.msa.SupplementaryDID.** { *; }
-keep interface repeackage.com.asus.msa.SupplementaryDID.** { *; }
-keep class repeackage.com.bun.lib.** { *; }
-keep interface repeackage.com.bun.lib.** { *; }
-keep class repeackage.com.heytap.openid.** { *; }
-keep interface repeackage.com.heytap.openid.** { *; }
-keep class repeackage.com.samsung.android.deviceidservice.** { *; }
-keep interface repeackage.com.samsung.android.deviceidservice.** { *; }
-keep class repeackage.com.zui.deviceidservice.** { *; }
-keep interface repeackage.com.zui.deviceidservice.** { *; }
-keep class repeackage.com.coolpad.deviceidsupport.** { *; }
-keep interface repeackage.com.coolpad.deviceidsupport.** { *; }
-keep class repeackage.com.android.creator.** { *; }
-keep interface repeackage.com.android.creator.** { *; }
-keep class repeackage.com.google.android.gms.ads.identifier.internal.** { *; }
-keep interface repeackage.com.google.android.gms.ads.identifier.internal.* { *; }