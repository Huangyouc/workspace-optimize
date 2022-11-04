package com.example.module_base;

import android.content.Context;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

public class EmulatorDetector {
  private static int a = -1;
  private static StringBuilder sb = new StringBuilder();

  public static boolean a(Context context) {
    String c = c(context);
    if (!TextUtils.isEmpty(c)) {
      sb.append(c);
      sb.append("|");
      return true;
    }
    if (Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("sdk_x86") || Build.PRODUCT.contains("sdk_google") || Build.PRODUCT.contains("Andy") || Build.PRODUCT.contains("Droid4X") || Build.PRODUCT.contains("nox") || Build.PRODUCT.contains("vbox86p") || Build.PRODUCT.contains("aries")
      || Build.MANUFACTURER.equals("Genymotion") || Build.MANUFACTURER.contains("Andy") || Build.MANUFACTURER.contains("nox") || Build.MANUFACTURER.contains("TiantianVM")
      || Build.BRAND.contains("Andy")
      || Build.DEVICE.contains("Andy") || Build.DEVICE.contains("Droid4X") || Build.DEVICE.contains("nox") || Build.DEVICE.contains("vbox86p") || Build.DEVICE.contains("aries")
      || Build.MODEL.contains("Emulator") || Build.MODEL.equals("google_sdk") || Build.MODEL.contains("Droid4X") || Build.MODEL.contains("TiantianVM") || Build.MODEL.contains("Andy") || Build.MODEL.equals("Android SDK built for x86_64") || Build.MODEL.equals("Android SDK built for x86")
      || Build.HARDWARE.equals("vbox86") || Build.HARDWARE.contains("nox") || Build.HARDWARE.contains("ttVM_x86")
      || Build.FINGERPRINT.contains("generic/sdk/generic") || Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") || Build.FINGERPRINT.contains("Andy") || Build.FINGERPRINT.contains("ttVM_Hdragon") || Build.FINGERPRINT.contains("generic/google_sdk/generic") || Build.FINGERPRINT.contains("vbox86p") || Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
      sb.append("PRODUCT:");
      sb.append(Build.PRODUCT);
      sb.append("|");
      sb.append("MANUFACTURER:");
      sb.append(Build.MANUFACTURER);
      sb.append("|");
      sb.append("BRAND:");
      sb.append(Build.BRAND);
      sb.append("|");
      sb.append("DEVICE:");
      sb.append(Build.DEVICE);
      sb.append("|");
      sb.append("MODEL:");
      sb.append(Build.MODEL);
      sb.append("|");
      sb.append("HARDWARE:");
      sb.append(Build.HARDWARE);
      sb.append("|");
      sb.append("FINGERPRINT:");
      sb.append(Build.FINGERPRINT);
      sb.append("|");
      return true;
    }
    return false;
  }

  public static String b(Context context) {
    if (a(context)) {
      return sb.toString();
    }
    if (a < 0) {
      int i;
      if (Build.PRODUCT.contains("sdk") || Build.PRODUCT.contains("Andy") || Build.PRODUCT.contains("ttVM_Hdragon") || Build.PRODUCT.contains("google_sdk") || Build.PRODUCT.contains("Droid4X") || Build.PRODUCT.contains("nox") || Build.PRODUCT.contains("sdk_x86") || Build.PRODUCT.contains("sdk_google") || Build.PRODUCT.contains("vbox86p") || Build.PRODUCT.contains("aries")) {
        sb.append("PRODUCT:");
        sb.append(Build.PRODUCT);
        sb.append("|");
        i = 1;
      } else {
        i = 0;
      }
      if (Build.MANUFACTURER.equals("unknown") || Build.MANUFACTURER.equals("Genymotion") || Build.MANUFACTURER.contains("Andy") || Build.MANUFACTURER.contains("MIT") || Build.MANUFACTURER.contains("nox") || Build.MANUFACTURER.contains("TiantianVM")) {
        sb.append("MANUFACTURER:");
        sb.append(Build.MANUFACTURER);
        sb.append("|");
        i++;
      }
      if (Build.BRAND.equals("generic") || Build.BRAND.equals("generic_x86") || Build.BRAND.equals("TTVM") || Build.BRAND.contains("Andy")) {
        sb.append("BRAND:");
        sb.append(Build.BRAND);
        sb.append("|");
        i++;
      }
      if (Build.DEVICE.contains("generic") || Build.DEVICE.contains("generic_x86") || Build.DEVICE.contains("Andy") || Build.DEVICE.contains("ttVM_Hdragon") || Build.DEVICE.contains("Droid4X") || Build.DEVICE.contains("nox") || Build.DEVICE.contains("generic_x86_64") || Build.DEVICE.contains("vbox86p") || Build.DEVICE.contains("aries")) {
        sb.append("generic:");
        sb.append(Build.DEVICE);
        sb.append("|");
        i++;
      }
      if (Build.MODEL.equals("sdk") || Build.MODEL.contains("Emulator") || Build.MODEL.equals("google_sdk") || Build.MODEL.contains("Droid4X") || Build.MODEL.contains("TiantianVM") || Build.MODEL.contains("Andy") || Build.MODEL.equals("Android SDK built for x86_64") || Build.MODEL.equals("Android SDK built for x86")) {
        sb.append("MODEL:");
        sb.append(Build.MODEL);
        sb.append("|");
        i++;
      }
      if (Build.HARDWARE.equals("goldfish") || Build.HARDWARE.equals("vbox86") || Build.HARDWARE.contains("nox") || Build.HARDWARE.contains("ttVM_x86")) {
        sb.append("HARDWARE:");
        sb.append(Build.HARDWARE);
        sb.append("|");
        i++;
      }
      if (Build.FINGERPRINT.contains("generic/sdk/generic") || Build.FINGERPRINT.contains("generic_x86/sdk_x86/generic_x86") || Build.FINGERPRINT.contains("Andy") || Build.FINGERPRINT.contains("ttVM_Hdragon") || Build.FINGERPRINT.contains("generic_x86_64") || Build.FINGERPRINT.contains("generic/google_sdk/generic") || Build.FINGERPRINT.contains("vbox86p") || Build.FINGERPRINT.contains("generic/vbox86p/vbox86p")) {
        sb.append("FINGERPRINT:");
        sb.append(Build.FINGERPRINT);
        sb.append("|");
        i++;
      }

      try {
        String glGetString = GLES20.glGetString(7937);
        if (glGetString != null && (glGetString.contains("Bluestacks") || glGetString.contains("Translator"))) {
          sb.append("glGetString:");
          sb.append(glGetString);
          sb.append("|");
          i += 10;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      try {
        String s = Environment.getExternalStorageDirectory().toString() + File.separatorChar + "windows" + File.separatorChar + "BstSharedFolder";
        if (new File(s).exists()) {
          sb.append("BstSharedFolder:");
          sb.append(s);
          sb.append("|");
          i += 10;
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
      a = i;
    }
    if (a <= 3) {
      return null;
    }
    return sb.toString();
  }

  private static final String c(Context context) {
    String a = a(context, "ro.kernel.qemu");
    if ("1".equals(a)) {
      return a;
    }
    return "";
  }

  private static final String a(Context context, String str) {
    try {
      Class loadClass = context.getClassLoader().loadClass("android.os.SystemProperties");
      return (String) loadClass.getMethod("get", new Class[]{String.class}).invoke(loadClass, new Object[]{str});
    } catch (Exception e) {
      return "";
    }
  }
}
