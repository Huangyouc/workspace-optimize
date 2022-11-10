package com.example.module_file;

import android.util.Log;

import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;

public class X5Init {
    public static void init(){
        QbSdk.setDownloadWithoutWifi(true);
        // 搜集本地tbs内核信息并上报服务器，服务器返回结果决定使用哪个内核。
        QbSdk.PreInitCallback cb = new QbSdk.PreInitCallback() {

            @Override
            public void onViewInitFinished(boolean b) {
                // x5內核初始化完成的回调，为true表示x5内核加载成功，否则表示x5内核加载失败，会自动切换到系统内核。
                Log.e("TbsReaderView"," onViewInitFinished is " + b);


                if (!b && !QbSdk.getIsSysWebViewForcedByOuter()) { //使用系统内核可能是x5内核在之前未安装成功，重新安装
                    Log.e("TbsReaderView","restart");
                    boolean needDownload = TbsDownloader.needDownload(IFAApplication.getInstance().getApplicationContext(), TbsDownloader.DOWNLOAD_OVERSEA_TBS);
                    Log.e("TbsReaderView", "onCreate: "+needDownload );
                    if (needDownload) {
                        //判断是否是x5内核未下载成功，存在缓存 重置化sdk，这样就清除缓存继续下载了
                        QbSdk.reset(IFAApplication.getInstance().getApplicationContext());
                        //开始下载x5内核
                        TbsDownloader.startDownload(IFAApplication.getInstance().getApplicationContext(),true);//一定要用两个参数的startDownload，且第二个参数传true
                    }else{
                        Log.e("TbsReaderView", "initX5Environment");
                        QbSdk.initX5Environment(IFAApplication.getInstance().getApplicationContext(), this);
                    }
                }
            }

            @Override
            public void onCoreInitFinished() {
                Log.e("TbsReaderView","onCoreInitFinished");
            }
        };
        // x5内核初始化接口
        QbSdk.initX5Environment(IFAApplication.getInstance().getApplicationContext(), cb);
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                Log.e("TbsReaderView", "onDownloadFinish");
            }
            @Override
            public void onInstallFinish(int i) {
                Log.e("TbsReaderView", "onInstallFinish: 内核下载安装成功" );
                QbSdk.initX5Environment(IFAApplication.getInstance().getApplicationContext(), cb);
            }
            @Override
            public void onDownloadProgress(int i) {
                Log.e("TbsReaderView", "onDownloadProgress: "+i);
                QbSdk.initX5Environment(IFAApplication.getInstance().getApplicationContext(), cb);
            }
        });
    }
}
