package com.example.module_file;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class DownloadDialog extends AlertDialog {
    private Activity context;
    private View.OnClickListener mClickListener;
    public DownloadDialog(Activity context, int theme) {
        super(context, theme);
    }

    public DownloadDialog(Activity context, View.OnClickListener onClickListener) {
        super(context, R.style.ShareDialog);
        this.context = context;
        this.mClickListener = onClickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.download_file_dialog);
        //设置弹窗在底部
        Window window = this.getWindow();
        window.setGravity(Gravity.BOTTOM);
//        window.getDecorView().setPadding(0,0,0,0);
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        //activity屏幕变暗
        WindowManager.LayoutParams p = context.getWindow().getAttributes();
        p.alpha = 0.6f;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
//        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        context.getWindow().setAttributes(p);
        this.setOnDismissListener(new MyOnDismissListener());

        findViewById(R.id.ll_download).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mClickListener!=null){
                    mClickListener.onClick(view);
                }
                dismiss();
            }
        });

        findViewById(R.id.ll_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });
    }

    /**
     * 弹框消失后页面恢复背景
     */
    private class MyOnDismissListener implements OnDismissListener {

        @Override
        public void onDismiss(DialogInterface dialog) {

            WindowManager.LayoutParams lp = context.getWindow().getAttributes();
            lp.alpha = 1f;
            context.getWindow().setAttributes(lp);
        }
    }


}
