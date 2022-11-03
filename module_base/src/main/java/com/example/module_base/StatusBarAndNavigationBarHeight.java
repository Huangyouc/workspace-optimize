package com.example.module_base;

import android.content.Context;
import android.content.res.Resources;
import android.view.DisplayCutout;

import com.example.module_base.resutil.ResUtil;

import java.lang.reflect.Method;


public class StatusBarAndNavigationBarHeight {
    public static DisplayCutout cutoutDisp = null;
    public static int CutOutSafeHeight = 0;
    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度 px
     */
    public static int getStatusBarHeightPX(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }
    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度 dp
     */
    public static float getStatusNewBarHeightDP(Context context){
        float result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        result = context.getResources().getDimensionPixelSize(resourceId);
        if(cutoutDisp != null && CutOutSafeHeight > 0 && result > 0 && CutOutSafeHeight > result){
            // 挖孔屏的高度采用摄像头的高度，摄像头比状态栏要大
            result = CutOutSafeHeight;
        }
        if(result > 0) {
            result = ResUtil.px2dp((int) result);
        }
        return result;
    }

    /**
     * 获取底部虚拟按键高度
     *
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeight = 0;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
            navigationBarHeight = rs.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }
    /**
     * 判断是否有虚拟底部按钮
     *
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }
}
