package com.example.module_base.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.module_base.R;
import com.geekthings.module_imagepicker.model.ImageItem;
import com.hjq.toast.ToastUtils;

import java.util.ArrayList;

/**
 * android Q以上和以下的  图片，视频，语音，文件的写入
 */
public class FileWriteDemoActivity extends AppCompatActivity {

    private Activity context;
    private static final int WRITE_FILE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_write_demo);

        context = this;

        TextView aboveQ_single_img = findViewById(R.id.aboveQ_single_img);
        TextView aboveQ_imgs = findViewById(R.id.aboveQ_imgs);
        TextView aboveQ_single_file = findViewById(R.id.aboveQ_single_file);
        TextView aboveQ_single_video = findViewById(R.id.aboveQ_single_video);
        TextView aboveQ_videos = findViewById(R.id.aboveQ_videos);
        TextView aboveQ_single_audio = findViewById(R.id.aboveQ_single_audio);
        TextView aboveQ_audios = findViewById(R.id.aboveQ_audios);

        TextView belowQ_single_img = findViewById(R.id.belowQ_single_img);
        TextView belowQ_imgs = findViewById(R.id.belowQ_imgs);
        TextView belowQ_single_file = findViewById(R.id.belowQ_single_file);
        TextView belowQ_single_video = findViewById(R.id.belowQ_single_video);
        TextView belowQ_videos = findViewById(R.id.belowQ_videos);
        TextView belowQ_single_audio = findViewById(R.id.belowQ_single_audio);
        TextView belowQ_audios = findViewById(R.id.belowQ_audios);


        TextView close_tv = findViewById(R.id.close_tv);
        ImageView show_img = findViewById(R.id.show_img);


        aboveQ_single_img.setOnClickListener(v -> {
            ToastUtils.show("详情参考 ViewConvertBitmap.addBitmapToAlbum 方法");

        });
        aboveQ_imgs.setOnClickListener(v -> {

//            ToastUtils.show(" ViewConvertBitmap.addBitmapToAlbum 方法");
        });

        aboveQ_single_file.setOnClickListener(v -> {
            /**
             * Android 10在MediaStore中新增了一种Downloads集合，专门用于执行文件下载操作;
             * Android 10 以下还是使用默认的处理方式
             */
            ToastUtils.show("详情参考 DownloadFileUtils.download方法");
        });
        aboveQ_single_video.setOnClickListener(v -> {

        });
        aboveQ_videos.setOnClickListener(v -> {

        });
        aboveQ_single_audio.setOnClickListener(v -> {

        });
        aboveQ_audios.setOnClickListener(v -> {

        });



        belowQ_single_img.setOnClickListener(v -> {
            ToastUtils.show("详情参考 ViewConvertBitmap.addBitmapToAlbum 方法");
        });
        belowQ_imgs.setOnClickListener(v -> {

        });
        belowQ_single_file.setOnClickListener(v -> {
            ToastUtils.show("详情参考 DownloadFileUtils.download方法");
        });
        belowQ_single_video.setOnClickListener(v -> {

        });
        belowQ_videos.setOnClickListener(v -> {

        });
        belowQ_single_audio.setOnClickListener(v -> {

        });
        belowQ_audios.setOnClickListener(v -> {

        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!= Activity.RESULT_OK || data == null){
            return;
        }


    }
}