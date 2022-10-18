package com.geekthings.module_imagepicker.model.repository;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.geekthings.module_imagepicker.R;
import com.geekthings.module_imagepicker.model.ImageFolder;
import com.geekthings.module_imagepicker.model.ImageItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
public class ImageSourceRepository {
    private volatile static ImageSourceRepository sInstance;

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
            MediaStore.Images.Media.DATE_ADDED};    //图片被添加的时间，long型  1450518608


//    private List<ImageFolder> imageFolders = new ArrayList<>();   //所有的图片文件夹

    private ImageSourceRepository(){

    }

    public static ImageSourceRepository getInstance() {
        if (sInstance == null) {
            synchronized (ImageSourceRepository.class) {
                if (sInstance == null) {
                    sInstance = new ImageSourceRepository();
                }
            }
        }
        return sInstance;
    }

    public Observable<List<ImageFolder>> queryAllImagesInLocal(Context context){

        return Observable.create(new ObservableOnSubscribe<List<ImageFolder>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ImageFolder>> e) throws Exception {
                Cursor cursor = context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,null,null,
                        MediaStore.Images.Media.DATE_ADDED+" DESC");
                List<ImageFolder> imageFolders = buildData(context,cursor);
                e.onNext(imageFolders);
                e.onComplete();

            }
        });

    }


    private List<ImageFolder> buildData(Context context ,Cursor data) {
        List<ImageFolder> imageFolders = new ArrayList<>();
        imageFolders.clear();
        if (data != null) {
            ArrayList<ImageItem> allImages = new ArrayList<>();   //所有图片的集合,不分文件夹
            while (data.moveToNext()) {
                //查询数据
                String imageId = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media._ID)) + "";
                String imageName = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME));
                String imagePath = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Uri photoUri = Uri.withAppendedPath(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        imageId);
                String bucket_id = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID));
                String bucket_name = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                long imageSize = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE));
                int imageWidth = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.WIDTH));
                int imageHeight = data.getInt(data.getColumnIndexOrThrow(MediaStore.Images.Media.HEIGHT));
                String imageMimeType = data.getString(data.getColumnIndexOrThrow(MediaStore.Images.Media.MIME_TYPE));
                long imageAddTime = data.getLong(data.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED));
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
                //根据父路径分类存放图片
                File imageFile = new File(imagePath);
                File imageParentFile = imageFile.getParentFile();
                ImageFolder imageFolder = new ImageFolder();
                imageFolder.id = imageItem.bucketId;
                imageFolder.name = imageItem.bucketName;
                imageFolder.path = imageParentFile.getAbsolutePath();

                if (!imageFolders.contains(imageFolder)) {
                    ArrayList<ImageItem> images = new ArrayList<>();
                    images.add(imageItem);
                    imageFolder.cover = imageItem;
                    imageFolder.images = images;
                    imageFolders.add(imageFolder);
                } else {
                    imageFolders.get(imageFolders.indexOf(imageFolder)).images.add(imageItem);
                }
            }
            //防止没有图片报异常
            if (data.getCount() > 0 && allImages.size() > 0) {
                //构造所有图片的集合
                ImageFolder allImagesFolder = new ImageFolder();
                allImagesFolder.id="all";
                allImagesFolder.name = context.getResources().getString(R.string.ip_all_images);
                allImagesFolder.path = "/";
                allImagesFolder.cover = allImages.get(0);
                allImagesFolder.images = allImages;
                imageFolders.add(0, allImagesFolder);  //确保第一条是所有图片
            }
        }

        return imageFolders;

    }


}
