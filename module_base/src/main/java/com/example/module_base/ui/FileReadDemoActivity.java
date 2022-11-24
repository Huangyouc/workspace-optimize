package com.example.module_base.ui;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.module_base.R;
import com.geekthings.module_imagepicker.ImagePicker;
import com.geekthings.module_imagepicker.model.ImageItem;
import com.geekthings.module_imagepicker.ui.ImageGridActivity;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.ToastUtils;
import com.orhanobut.logger.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**作用域存储
 * 将应用程序更新为面向 Android 11 后，系统会忽略 requestLegacyExternalStorage 标志。
 *
 * android Q以上和以下的  图片，视频，语音，文件的读取。
 *
 * 参考链接：https://blog.csdn.net/qq_34681580/article/details/114338516
 *
 *        https://blog.csdn.net/dongxianfei/article/details/122086235
 *
 * 2022.11.24 文件下载后，再打开的功能，没有测试；aboveQ_single_file写的不好，不能打开具体目录下的具体文件
 *
 * google官方文档 https://developer.android.com/training/data-storage/shared/documents-files
 *
 */
public class FileReadDemoActivity extends AppCompatActivity{
    private Context context;
    private static final int PICK_FILE = 1;
    private  ActivityResultLauncher<Intent> intentActivityResultLauncher;
    private final String[] IMAGE_PROJECTION = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,   //图片的显示名称  aaa.jpg
            MediaStore.Images.Media.DATA,           //图片的真实路径  /storage/emulated/0/pp/downloader/wallpaper/aaa.jpg
            MediaStore.Images.Media.BUCKET_ID,      //文件夹id
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME, //文件夹name
            MediaStore.Images.Media.SIZE,           //图片的大小，long型  132492
            MediaStore.Images.Media.WIDTH,          //图片的宽度，int型  1920
            MediaStore.Images.Media.HEIGHT,         //图片的高度，int型  1080
            MediaStore.Images.Media.MIME_TYPE,      //图片的类型     image/jpeg
            MediaStore.Images.Media.DATE_ADDED};

//    private final String FILE_PROJECTION = new String[]{
//            MediaStore.Images.ImageColumns.
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_read_demo);
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
        TextView file_read = findViewById(R.id.file_read);


        TextView close_tv = findViewById(R.id.close_tv);
        ImageView show_img = findViewById(R.id.show_img);
        LinearLayout show_ll = findViewById(R.id.show_ll);


        aboveQ_single_img.setOnClickListener(v -> {
            ContentResolver resolver = context.getContentResolver();
            String sortOrder = MediaStore.Images.Media.DATE_MODIFIED + " DESC";//根据日期降序查询
            String name = "1668591895868.jpg";//name是文件名称
            String selection = MediaStore.Images.Media.DISPLAY_NAME + "='" + name + "'";   //查询条件 “显示名称为？”
            Cursor cursor = resolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,selection,null,
                    sortOrder);
//            Cursor cursor = resolver.query(
//                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                    IMAGE_PROJECTION,null,null,
//                    sortOrder);
            if(cursor!=null){
                ArrayList<ImageItem> allImages = new ArrayList<>();   //获取该名称的所有图片的集合
                while (cursor.moveToNext()){
                    //查询数据
                    String imageId = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)) + "";
                    String imageName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                    Uri photoUri = Uri.withAppendedPath(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            imageId);
                    String bucket_id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                    String bucket_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                    long imageSize = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                    int imageWidth = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                    int imageHeight = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                    String imageMimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                    long imageAddTime = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
                    //封装实体
                    ImageItem imageItem = new ImageItem();
                    imageItem.id = imageId;
                    imageItem.uri = photoUri;
                    imageItem.name = imageName;
                    imageItem.path = imagePath;
                    imageItem.bucketId = bucket_id;
                    imageItem.bucketName = bucket_name;
                    imageItem.size = imageSize;
                    imageItem.width = imageWidth;
                    imageItem.height = imageHeight;
                    imageItem.mimeType = imageMimeType;
                    imageItem.addTime = imageAddTime;
                    allImages.add(imageItem);
                }

                cursor.close();


                if(!allImages.isEmpty()){
                    Logger.e("allImages size = "+allImages.size());
                    ImageItem imageItem = allImages.get(0);
                    Logger.e("uri = "+imageItem.uri);
                    Logger.e("url = "+imageItem.path);
                    show_ll.setVisibility(View.VISIBLE);
                    Glide.with(this).load(imageItem.uri).into(show_img);
                }
//                如果你没有使用Glide或其他图片加载框架，想在不借助第三方库的情况下直接将一个Uri对象解析成图片，可以使用如下代码：
//                val fd = contentResolver.openFileDescriptor(uri, "r")
//                if (fd != null) {
//                    val bitmap = BitmapFactory.decodeFileDescriptor(fd.fileDescriptor)
//                    fd.close()
//                    imageView.setImageBitmap(bitmap)
//                }
            }

        });
        aboveQ_imgs.setOnClickListener(v -> {

            ToastUtils.show("详情参考 ImageSourceRepository.getInstance().queryAllImagesInLocal 方法");
        });
        aboveQ_single_file.setOnClickListener(v -> {

            //获取指定字段
//            String[] columns = new String[]{MediaStore.Files.FileColumns._ID, MediaStore.Files.FileColumns.MIME_TYPE, MediaStore.Files.FileColumns
//                    .SIZE, MediaStore.Files.FileColumns.DATE_MODIFIED, MediaStore.Files.FileColumns.DATA};
//            c = mContentResolver.query(MediaStore.Files.getContentUri("external"), columns, null, null, null);

            ContentResolver contentResolver = context.getContentResolver();
//            String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "='" + "2月资产配置报告.pdf" + "'";
//            String selection = MediaStore.Files.FileColumns.DISPLAY_NAME + "='" + "VID_20220530_164927.mp4" + "'";
//            String selection = MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME + "='" + "2月资产配置报告.pdf" + "'";
            String sortOrder = MediaStore.Downloads.DATE_MODIFIED + " DESC";//根据日期降序查询
//            Uri.parse("content://media/external/files"),
            Logger.e("hyc>>>>"+MediaStore.Files.getContentUri("external"));
            Logger.e("hyc>>>>"+MediaStore.Downloads.getContentUri("external"));
            Cursor cursor = contentResolver.query(
                    MediaStore.Files.getContentUri("external/download"),
                     null,null,null,
                    sortOrder);
            if(cursor!=null){
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
//                    Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    Uri uri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external/download"), id);
                    String fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME));
                    String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                    String bucket_name = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME));

                    Logger.e("file uri is "+uri);
                    Logger.e("file fileName is "+fileName);
                    Logger.e("file filePath is "+filePath);
                    Logger.e("file bucket_name is "+bucket_name);
                }
                cursor.close();
            }

        });
        aboveQ_single_video.setOnClickListener(v -> {
            ImagePicker.getInstance().setMultiMode(false);
            Intent intent0 = new Intent(FileReadDemoActivity.this, ImageGridActivity.class);
            startActivityForResult(intent0, 12);
        });
        aboveQ_videos.setOnClickListener(v -> {

        });
        aboveQ_single_audio.setOnClickListener(v -> {

        });
        aboveQ_audios.setOnClickListener(v -> {

        });



        belowQ_single_img.setOnClickListener(v -> {
            ToastUtils.show("和这个一样aboveQ_single_img");
        });
        belowQ_imgs.setOnClickListener(v -> {
            ToastUtils.show("详情参考 ImageSourceRepository.getInstance().queryAllImagesInLocal 方法");
        });
        belowQ_single_file.setOnClickListener(v -> {

        });
        belowQ_single_video.setOnClickListener(v -> {

        });
        belowQ_videos.setOnClickListener(v -> {

        });
        belowQ_single_audio.setOnClickListener(v -> {

        });
        belowQ_audios.setOnClickListener(v -> {

        });

//        如果我们要读取SD卡上非图片、音频、视频类的文件，比如说打开一个PDF文件，
//        这个时候就不能再使用MediaStore API了，而是要使用文件选择器。
        file_read.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
//            txt的mimeType是 "text/plain",其他文件的则使用相对应的type,比如pdf,则传application/pdf，图片文件传image/*
            intentActivityResultLauncher.launch(intent);
//            startActivityForResult(intent,PICK_FILE);
        });

        close_tv.setOnClickListener(v -> {
            ((View)close_tv.getParent()).setVisibility(View.GONE);
        });


        //这个registerForActivityResult方法要写在Activity的onCreate方法里（在Activity创建的时候就要创建出来，
        // 不能等到使用的时候再创建，不然会报错LifecycleOwner  is attempting to register while current state is RESUMED. LifecycleOwners must call register before they are STARTED.）。
        intentActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //获取返回的结果
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            try {
                                // 执行文件读取操作
                                InputStream inputStream = getContentResolver().openInputStream(uri);
                                //todo
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });


        XXPermissions.with(this)
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(new OnPermissionCallback() {
                    @Override
                    public void onGranted(List<String> permissions, boolean all) {

                    }
                });

//        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12121);
    }


}