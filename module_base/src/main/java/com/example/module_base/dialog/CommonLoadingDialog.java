package com.example.module_base.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

import com.example.module_base.R;

/**
 * 页面通用加载dialog
 * @author hyc
 */
public class CommonLoadingDialog extends Dialog {

    private AppCompatTextView vTextViewContent;
    private CharSequence mContent;
    @ColorInt
    private int textColor;

    private Animation mAnimation;
    public CommonLoadingDialog(@NonNull Context context) {
        super(context, R.style.commonDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_loading_dialog);
        ImageView image = findViewById(R.id.image);
        vTextViewContent = findViewById(R.id.text);
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.common_loading_rotate);
        LinearInterpolator interpolator = new LinearInterpolator();
        mAnimation.setInterpolator(interpolator);
        image.startAnimation(mAnimation);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
    }

    public void setText(int textId) {
        mContent = getContext().getText(textId);

    }

    public void setText(String text){
        mContent = text;

    }

    public void setTextColor(int resId){
        textColor =resId;


    }

    @Override
    public void show() {
        super.show();
        if(!TextUtils.isEmpty(mContent)){
            vTextViewContent.setVisibility(View.VISIBLE);
            vTextViewContent.setText(mContent);

            if(textColor!=0){
                vTextViewContent.setTextColor(textColor);

            }
        }

    }

    @Override
    public void dismiss() {
        super.dismiss();
        if(mAnimation != null){
            mAnimation.cancel();
        }
    }
}