package com.example.module_base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Proxy;
import android.os.Build;
import android.os.Debug;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 判断手机是否root了 或者使用了模拟器，或者使用了vpn代理等
 */
public class RootCheckUtil {
  private static final String LINE_SEP = System.getProperty("line.separator");

  // 是否使用了vpn 或者 代理
  private static boolean a(Context context) {
    String str;
    int i;
    if (Build.VERSION.SDK_INT >= 14) {
      String str1 = System.getProperty("http.proxyHost");
      str = System.getProperty("http.proxyPort");
      if (str == null)
        str = "-1";
      i = Integer.parseInt(str);
      str = str1;
    } else {
      str = Proxy.getHost(context);
      i = Proxy.getPort(context);
    }
    if (!TextUtils.isEmpty(str)) {
      if (i != -1)
        return true;
    }
    return false;
  }

  // 判断手机是否root了 或者使用了模拟器，或者使用了vpn代理等
  public static boolean c(Activity activity) {

    if (d() || EmulatorDetector.b(activity) != null) {
      showDialog(activity, "检测到您使用的是模拟器或者设备已经root,不允许继续使用!!!", true, EmulatorDetector.b(activity));
      return true;
    }
    if (a(activity)) {
      showDialog(activity, "检测到您使用了代理软件,不允许继续使用!!!", false, "");
      return true;
    }
    return false;
  }

  private static boolean d() {
    return (isAppRoot() || isDeviceRooted());
  }

  private static void showDialog(Activity context, String tip, boolean isrooted, String rootInfo) {
    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
    builder.setMessage(tip)
            .setTitle("温馨提示")
            .setCancelable(false)
            .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                System.exit(0);
              }
            }).create().show();
  }

  public static boolean isDeviceRooted() {
    String su = "su";
    String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
      "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
      "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
    for (String location : locations) {
      if (new File(location + su).exists()) {
        return true;
      }
    }
    return false;
  }

  // 判断手机是否root了
  public static boolean isAppRoot() {
    CommandResult result = execCmd(new String[]{"echo root"}, null, true, true);
    return result.result == 0;
  }

  public static CommandResult execCmd(final String[] commands,
                                      final String[] envp,
                                      final boolean isRooted,
                                      final boolean isNeedResultMsg) {
    int result = -1;
    if (commands == null || commands.length == 0) {
      return new CommandResult(result, "", "");
    }
    Process process = null;
    BufferedReader successResult = null;
    BufferedReader errorResult = null;
    StringBuilder successMsg = null;
    StringBuilder errorMsg = null;
    DataOutputStream os = null;
    try {
      process = Runtime.getRuntime().exec(isRooted ? "su" : "sh", envp, null);
      os = new DataOutputStream(process.getOutputStream());
      for (String command : commands) {
        if (command == null) continue;
        os.write(command.getBytes());
        os.writeBytes(LINE_SEP);
        os.flush();
      }
      os.writeBytes("exit" + LINE_SEP);
      os.flush();
      result = process.waitFor();
      if (isNeedResultMsg) {
        successMsg = new StringBuilder();
        errorMsg = new StringBuilder();
        successResult = new BufferedReader(
          new InputStreamReader(process.getInputStream(), "UTF-8")
        );
        errorResult = new BufferedReader(
          new InputStreamReader(process.getErrorStream(), "UTF-8")
        );
        String line;
        if ((line = successResult.readLine()) != null) {
          successMsg.append(line);
          while ((line = successResult.readLine()) != null) {
            successMsg.append(LINE_SEP).append(line);
          }
        }
        if ((line = errorResult.readLine()) != null) {
          errorMsg.append(line);
          while ((line = errorResult.readLine()) != null) {
            errorMsg.append(LINE_SEP).append(line);
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (os != null) {
          os.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (successResult != null) {
          successResult.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      try {
        if (errorResult != null) {
          errorResult.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (process != null) {
        process.destroy();
      }
    }
    return new CommandResult(
      result,
      successMsg == null ? "" : successMsg.toString(),
      errorMsg == null ? "" : errorMsg.toString()
    );
  }

  /**
   * The result of command.
   */
  public static class CommandResult {
    public int result;
    public String successMsg;
    public String errorMsg;

    public CommandResult(final int result, final String successMsg, final String errorMsg) {
      this.result = result;
      this.successMsg = successMsg;
      this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
      return "result: " + result + "\n" +
        "successMsg: " + successMsg + "\n" +
        "errorMsg: " + errorMsg;
    }
  }


  public static Debug.MemoryInfo as(Context context) {
    Debug.MemoryInfo memoryInfo = null;
    try {
      ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
      if (activityManager != null) {
        Debug.MemoryInfo[] arrayOfMemoryInfo = activityManager.getProcessMemoryInfo(new int[] { android.os.Process.myPid() });
        if (arrayOfMemoryInfo != null) {
          if (arrayOfMemoryInfo.length > 0)
             memoryInfo = arrayOfMemoryInfo[0];
        }
      }
    } catch (Exception exception) {
      exception.printStackTrace();
    }
    return memoryInfo;
  }
}
