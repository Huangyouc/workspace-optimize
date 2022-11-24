package com.example.module_base.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.module_base.DeviceUtil;
import com.example.module_base.R;

public class DemoActivity extends BaseActivity {

    TextView mTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTv = findViewById(R.id.tv);
//        showLoadingDialog("正在加载");
        showLoadingDialog(null);

        //模拟网络请求
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dismissLoadingDialog();
//                showEmptyView();
                mTv.setText(DeviceUtil.getDeviceID(DemoActivity.this));

            }
        },1000);

        mTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideEmptyView();
            }
        });

        findViewById(R.id.readfile_tv).setOnClickListener(v -> {
            startActivity(new Intent(DemoActivity.this,FileReadDemoActivity.class));
        });


    }

    @Override
    public int getLayoutId() {
        return R.layout.common_activity_demo;
    }
}