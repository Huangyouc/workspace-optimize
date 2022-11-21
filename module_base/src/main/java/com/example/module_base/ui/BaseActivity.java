package com.example.module_base.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewStub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.module_base.AppManager;
import com.example.module_base.R;
import com.example.module_base.StringUtils;
import com.example.module_base.dialog.CommonLoadingDialog;

public abstract class BaseActivity extends AppCompatActivity {


    private CommonLoadingDialog mLoadingDialog;
    private ViewStub mEmptyView;
    public  Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        // 添加到栈中
        AppManager.getInstance().addActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 从栈中移除
        AppManager.getInstance().finishActivity(this);
    }

    public abstract int getLayoutId();

    public void showLoadingDialog(String loadingString){
        mLoadingDialog = new CommonLoadingDialog(this);
        if(StringUtils.isNotEmpty(loadingString)){
            mLoadingDialog.setText(loadingString);
        }
        mLoadingDialog.show();
    }

    public void dismissLoadingDialog(){
        if(mHandler!=null){
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mLoadingDialog!=null&&mLoadingDialog.isShowing()){
                        mLoadingDialog.dismiss();
                    }
                }
            });
        }
    }

    public void showEmptyView(){
        if(mEmptyView==null){
            mEmptyView = findViewById(R.id.view_stub);
            mEmptyView.inflate();
        }else {
            mEmptyView.setVisibility(ViewStub.VISIBLE);
        }
    }
    public void hideEmptyView(){
        if(mEmptyView!=null){
            mEmptyView.setVisibility(View.GONE);
        }
    }
}
