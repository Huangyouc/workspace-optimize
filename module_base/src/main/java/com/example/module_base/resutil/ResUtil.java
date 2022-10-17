package com.example.module_base.resutil;

import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import com.example.module_base.ApplicationUtil;

public class ResUtil {

    private static Display d = ((WindowManager) ApplicationUtil.Companion.getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
    /**
     * 根据name获取String
     * @param name 资源名字
     * @return String的字符串
     */
    public static String getString(String name) {
        Context context = ApplicationUtil.Companion.getContext();
        if(TextUtils.isEmpty(name)){
            return null;
        }
        try {
            int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
            if(id > 0) {
                return context.getResources().getString(id);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据name获取图片资源
     * @param name 资源名字
     * @return
     */
    public static int getDrawableID(String name) {
        Context context = ApplicationUtil.Companion.getContext();
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }

    /**
     * 手机号4-7位用星号代替
     * @param phoneNumber 手机号
     * @return
     */
    public static String buildPhoneString(String phoneNumber){
        StringBuilder sb = new StringBuilder(phoneNumber);
        if(phoneNumber.length()>=11){ //手机号 4-7位数 用星号
            sb.replace(3,7,"****");
            phoneNumber = sb.toString();
        }

        return phoneNumber;
    }

    public static int sp2px(float spValue) {
        final float fontScale = ApplicationUtil.Companion.getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }
    public static int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal,
                ApplicationUtil.Companion.getContext().getResources().getDisplayMetrics());
    }

    public static int getWidth(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayWidth = displayMetrics.widthPixels;

        return displayWidth;
    }

    public static int getheight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;

        return displayHeight;
    }
}