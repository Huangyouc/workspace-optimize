package com.example.module_base.ui;

import android.os.Bundle;
import android.view.View;

import com.example.module_base.R;

public class DemoActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        showLoadingDialog("正在加载");
        showLoadingDialog(null);

        //模拟网络请求
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
                showEmptyView();
            }
        },2000);

        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmptyView();
            }
        });
    }

    @Override
    public int getLayoutId() {
        return R.layout.common_activity_demo;
    }
}