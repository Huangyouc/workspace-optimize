package com.example.module_floatwindow;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

public class TestUtils {


    public static void envChange(Context context) {
        if (context == null) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(context)) {// 6.0以上系统默认是关闭的
                    //去申请悬浮框权限
                    Intent serviceIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                    context.startActivity(serviceIntent);
                } else {
                    // 绘ui代码, 这里说明6.0系统已经有权限了
                    MyWindowManager.startThread(context);
                }
            } else {
                MyWindowManager.startThread(context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
