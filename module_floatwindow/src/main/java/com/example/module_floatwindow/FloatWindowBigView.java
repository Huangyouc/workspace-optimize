package com.example.module_floatwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class FloatWindowBigView extends LinearLayout implements OnClickListener {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;
    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;
    private Context context;

    public FloatWindowBigView(final Context context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        Button txt = (Button) findViewById(R.id.txt);
        txt.setText("当前环境:" + "todo hyc");
        Button btn_pre_pro = (Button) findViewById(R.id.btn_pre_pro);
        Button btn_uat_new = (Button) findViewById(R.id.btn_uat_new);
        Button btn_sit_new = (Button) findViewById(R.id.btn_sit_new);
        Button btn_uat_t1 = (Button) findViewById(R.id.btn_uat_t1);
        Button btn_uat_t2 = (Button) findViewById(R.id.btn_uat_t2);
        Button btn_uat_t3 = (Button) findViewById(R.id.btn_uat_t3);
        Button btn_uat_t4 = (Button) findViewById(R.id.btn_uat_t4);
        Button btn_uat_t5 = (Button) findViewById(R.id.btn_uat_t5);

        Button btn_skip_web = (Button) findViewById(R.id.btn_skip_web);
        Button btn_flutter_config = (Button) findViewById(R.id.btn_flutter_config);
        Button back = (Button) findViewById(R.id.back);
        Button user = (Button) findViewById(R.id.user);
        user.setText("用户faid:" + "todo hyc");
        btn_pre_pro.setOnClickListener(this);
        btn_uat_new.setOnClickListener(this);
        btn_sit_new.setOnClickListener(this);
        btn_uat_t1.setOnClickListener(this);
        btn_uat_t2.setOnClickListener(this);
        btn_uat_t3.setOnClickListener(this);
        btn_uat_t4.setOnClickListener(this);
        btn_uat_t5.setOnClickListener(this);
        btn_skip_web.setOnClickListener(this);
        btn_flutter_config.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_pre_pro) {
        } else if (id == R.id.btn_uat_new) {

        } else if (id == R.id.btn_sit_new) {

        } else if (id == R.id.btn_uat_t1) {

        } else if (id == R.id.btn_uat_t2) {

        } else if (id == R.id.btn_uat_t3) {

        } else if (id == R.id.btn_uat_t4) {

        } else if (id == R.id.btn_uat_t5) {

        } else if (id == R.id.btn_flutter_config) {

        } else if (id == R.id.btn_skip_web) {

        } else if (id == R.id.back) {

        }
        // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
        MyWindowManager.removeBigWindow(context);
        MyWindowManager.createSmallWindow(context);

    }
}
