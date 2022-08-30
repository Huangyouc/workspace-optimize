package com.example.inject;

import android.view.View;

import androidx.appcompat.app.AppCompatActivity;


public interface ViewUtils {
    public static <T extends View> T findViewById(AppCompatActivity activity, int resourceId) {
        return (T) activity.findViewById(resourceId);
    }


    public static <T extends View> T findViewById(View view, int resourceId) {
        return (T) view.findViewById(resourceId);
    }
}
