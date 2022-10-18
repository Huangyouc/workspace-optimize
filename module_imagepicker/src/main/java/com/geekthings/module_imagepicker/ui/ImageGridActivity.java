package com.geekthings.module_imagepicker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.geekthings.module_imagepicker.ImagePicker;
import com.geekthings.module_imagepicker.R;
import com.geekthings.module_imagepicker.adapter.ImageFolderAdapter;
import com.geekthings.module_imagepicker.adapter.ImageRecyclerAdapter;
import com.geekthings.module_imagepicker.loader.GlideImageLoader;
import com.geekthings.module_imagepicker.model.ImageFolder;
import com.geekthings.module_imagepicker.model.ImageItem;
import com.geekthings.module_imagepicker.model.repository.ImageSourceRepository;
import com.geekthings.module_imagepicker.util.Utils;
import com.geekthings.module_imagepicker.view.FolderPopUpWindow;
import com.geekthings.module_imagepicker.view.GridSpacingItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ImageGridActivity extends ImageBaseActivity
        implements ImagePicker.OnImageSelectedListener,ImageRecyclerAdapter.OnImageItemClickListener,View.OnClickListener{
    public static final int REQUEST_PERMISSION_STORAGE = 0x01;
    public static final int REQUEST_PERMISSION_CAMERA = 0x02;
    public static final String EXTRAS_TAKE_PICKERS = "TAKE";
    public static final String EXTRAS_IMAGES = "IMAGES";



    private ImagePicker imagePicker;
    private CompositeDisposable mCompositeDisposable;

    private boolean isOrigin = false;  //是否选中原图
    private ImageView btnBack;
    private View mFooterBar;     //底部栏
    private Button mBtnOk;       //确定按钮
    private View mllDir; //文件夹切换按钮
    private TextView mtvDir; //显示当前文件夹
    private TextView mBtnPre;      //预览按钮
    private ImageFolderAdapter mImageFolderAdapter;    //图片文件夹的适配器
    private FolderPopUpWindow mFolderPopupWindow;  //ImageSet的PopupWindow
    private List<ImageFolder> mImageFolders;   //所有的图片文件夹
    private boolean directPhoto = false; // 默认不是直接调取相机
    private RecyclerView mRecyclerView;
    private ImageRecyclerAdapter mRecyclerAdapter;



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        directPhoto = savedInstanceState.getBoolean(EXTRAS_TAKE_PICKERS, false);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRAS_TAKE_PICKERS, directPhoto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_grid);
        mCompositeDisposable = new CompositeDisposable();



        imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.clear();
        imagePicker.addOnImageSelectedListener(this);


        Intent data = getIntent();
        // 新增可直接拍照
        if (data != null && data.getExtras() != null) {
            directPhoto = data.getBooleanExtra(EXTRAS_TAKE_PICKERS, false); // 默认不是直接打开相机
            if (directPhoto) {
                if (!(checkPermission(Manifest.permission.CAMERA))) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, ImageGridActivity.REQUEST_PERMISSION_CAMERA);
                } else {
                    imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
                }
            }
//            ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(EXTRAS_IMAGES);
//            imagePicker.setSelectedImages(images);
        }

        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recyclerview);
        mBtnOk = (Button) findViewById(R.id.btn_ok);
        mBtnOk.setOnClickListener(this);
        mBtnPre = (TextView) findViewById(R.id.btn_preview);
        mBtnPre.setOnClickListener(this);
        mFooterBar = findViewById(R.id.footer_bar);
        mllDir = findViewById(R.id.ll_dir);
        mllDir.setOnClickListener(this);
        mtvDir = (TextView) findViewById(R.id.tv_dir);


        if (imagePicker.isMultiMode()) {
            mBtnOk.setVisibility(View.VISIBLE);
            mBtnPre.setVisibility(View.VISIBLE);
        } else {
            mBtnOk.setVisibility(View.GONE);
            mBtnPre.setVisibility(View.GONE);
        }



        mImageFolderAdapter = new ImageFolderAdapter(this, null);
        mRecyclerAdapter = new ImageRecyclerAdapter(this, null);

        onImageSelected(0, null, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                queryAllPhotos(this);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_STORAGE);
            }
        } else {
            queryAllPhotos(this);
        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                queryAllPhotos(this);
            } else {
                String message = getString(R.string.user_text_need_permissions, "存储权限","读取手机本地相册功能");
                showDialog(message);
            }
        } else if (requestCode == REQUEST_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                imagePicker.takePicture(this, ImagePicker.REQUEST_CODE_TAKE);
            } else {
                String message = getString(R.string.user_text_need_permissions, getString(R.string.user_permission_camera),"拍照功能");
                showDialog(message);
            }
        }
    }
    private void showDialog(String message){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton(R.string.user_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                        intent.setData(uri);
//                        try {
//                            startActivity(intent);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                        getActivity().finish();
                    }
                }).create().show();
    }
    /**
     * Rxjava 查询图片
     * @param context
     */
    private void queryAllPhotos(Context context){
        Disposable disposable =  ImageSourceRepository.getInstance().queryAllImagesInLocal(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<ImageFolder>>() {
                    @Override
                    public void accept(List<ImageFolder> imageFolders) throws Exception {
                        queryImageCompleted(imageFolders);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        mCompositeDisposable.add(disposable);

    }

    private void queryImageCompleted(List<ImageFolder> imageFolders){
        this.mImageFolders = imageFolders;
        imagePicker.setImageFolders(imageFolders);
        if (imageFolders.size() == 0) {
            mRecyclerAdapter.refreshData(null);
        } else {
            mRecyclerAdapter.refreshData(imageFolders.get(0).images);
        }
        mRecyclerAdapter.setOnImageItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(4, Utils.dp2px(this, 1), false));
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mImageFolderAdapter.refreshData(imageFolders);
    }



    /**
     * 创建弹出的ListView
     */
    private void createPopupFolderList() {
        mFolderPopupWindow = new FolderPopUpWindow(this, mImageFolderAdapter);
        mFolderPopupWindow.setOnItemClickListener(new FolderPopUpWindow.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                mImageFolderAdapter.setSelectIndex(position);
                imagePicker.setCurrentImageFolderPosition(position);
                mFolderPopupWindow.dismiss();
                ImageFolder imageFolder = (ImageFolder) adapterView.getAdapter().getItem(position);
                if (null != imageFolder) {
                    mRecyclerAdapter.refreshData(imageFolder.images);
                    mtvDir.setText(imageFolder.name);
                }
            }
        });
        mFolderPopupWindow.setMargin(mFooterBar.getHeight());
    }


    @Override
    public void onClick(View view) {

        int id = view.getId();
        if (id == R.id.btn_ok) {

            returnBackWithImages();

        } else if (id == R.id.ll_dir) {
            if (mImageFolders == null) {
                Log.i("ImageGridActivity", "您的手机没有图片");
                return;
            }
            //点击文件夹按钮
            createPopupFolderList();
            mImageFolderAdapter.refreshData(mImageFolders);  //刷新数据
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            } else {
                mFolderPopupWindow.showAtLocation(mFooterBar, Gravity.NO_GRAVITY, 0, 0);
                //默认选择当前选择的上一个，当目录很多时，直接定位到已选中的条目
                int index = mImageFolderAdapter.getSelectIndex();
                index = index == 0 ? index : index - 1;
                mFolderPopupWindow.setSelection(index);
            }
        } else if (id == R.id.btn_preview) {


            ImageFolder selectImagesFolder = new ImageFolder();//创建一个假的相册 存放已经选中的照片
            selectImagesFolder.id = "selected";
            selectImagesFolder.name="selected images";
            selectImagesFolder.images = (ArrayList<ImageItem>) ImagePicker.getInstance().getSelectedImages();

            Intent intent_preview = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent_preview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, 0);
            intent_preview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS,selectImagesFolder);
            startActivityForResult(intent_preview, ImagePicker.REQUEST_CODE_PREVIEW);


        } else if (id == R.id.btn_back) {
            //点击返回按钮
            Intent intent = new Intent();
            setResult(RESULT_CANCELED, intent);
            finish();
        }

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();
    }


    @Override
    public void onImageItemClick(View view, ImageItem imageItem, int position) {

        //根据是否有相机按钮确定位置
        position = imagePicker.isShowCamera() ? position - 1 : position;
        if (imagePicker.isMultiMode()) {
            Intent intent = new Intent(ImageGridActivity.this, ImagePreviewActivity.class);
            intent.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
            intent.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS,mImageFolders.get(imagePicker.getCurrentImageFolderPosition()));
            startActivityForResult(intent, ImagePicker.REQUEST_CODE_PREVIEW);  //如果是多选，点击图片进入预览界面
        } else {
//            imagePicker.clearSelectedImages();
//            imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
            if (imagePicker.isCrop()) {
                Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                intent.putExtra("seleted_image", (Parcelable) imageItem);
                startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
            } else {
                imagePicker.clearSelectedImages();
                imagePicker.addSelectedImageItem(position, imagePicker.getCurrentImageFolderItems().get(position), true);
                returnBackWithImages();
            }
        }




    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onImageSelected(int position, ImageItem item, boolean isAdd) {
        if (imagePicker.getSelectImageCount() > 0) {
            mBtnOk.setText(getString(R.string.ip_select_send, imagePicker.getSelectImageCount(), imagePicker.getSelectLimit()));
            mBtnOk.setEnabled(true);
            mBtnPre.setEnabled(true);
            mBtnPre.setText(getResources().getString(R.string.ip_preview_count, imagePicker.getSelectImageCount()));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_primary_inverted));
        } else {
            mBtnOk.setText(getString(R.string.ip_send));
            mBtnOk.setEnabled(false);
            mBtnPre.setEnabled(false);
            mBtnPre.setText(getResources().getString(R.string.ip_preview));
            mBtnPre.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
            mBtnOk.setTextColor(ContextCompat.getColor(this, R.color.ip_text_secondary_inverted));
        }

        for (int i = imagePicker.isShowCamera() ? 1 : 0; i < mRecyclerAdapter.getItemCount(); i++) {
            if (mRecyclerAdapter.getItem(i).uri != null && mRecyclerAdapter.getItem(i).uri.equals(item.uri)) {
                mRecyclerAdapter.notifyItemChanged(i);
                return;
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
        imagePicker.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        ImageItem imageItem = null;
        Log.e("sunny","sunny requestCode "+requestCode+" resultCode " +resultCode);

        switch (requestCode){

            case ImagePicker.REQUEST_CODE_PREVIEW:
                if(resultCode==RESULT_OK){
                    returnBackWithImages();
                }
                break;
            case ImagePicker.REQUEST_CODE_TAKE:
                if(resultCode==RESULT_OK){

                    Uri takeImageUri = imagePicker.getTakeImageUri();
                    imageItem = new ImageItem();
                    imageItem.uri = takeImageUri;

                    if (imagePicker.isCrop()) {
                        Intent intent = new Intent(ImageGridActivity.this, ImageCropActivity.class);
                        intent.putExtra("seleted_image", (Parcelable) imageItem);
                        startActivityForResult(intent, ImagePicker.REQUEST_CODE_CROP);  //单选需要裁剪，进入裁剪界面
                    } else {
                        List<Uri> selectedImageUris = new ArrayList<>();
                        selectedImageUris.add(takeImageUri);


                        returnBackWithImageUris((ArrayList<Uri>) selectedImageUris);
                    }
                }else{
                    if(directPhoto){
                        finish();

                    }
                }
                break;
            case ImagePicker.REQUEST_CODE_CROP:
                if(resultCode==RESULT_OK){
                   if(data!=null){
                       imageItem = data.getParcelableExtra(ImagePicker.EXTRA_RESULT_ITEM);
                       List<Uri> selectedImageUris = new ArrayList<>();
                       if(imageItem!=null){
                           selectedImageUris.add(imageItem.uri);
                       }
                       returnBackWithImageUris((ArrayList<Uri>)selectedImageUris);

                   }

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }


    private void returnBackWithImages(){
        List<ImageItem> selectedImages = imagePicker.getSelectedImages();
        ArrayList<Uri> uris = new ArrayList<>();
        for(int i =0;i<selectedImages.size();i++){
            ImageItem imageItem = selectedImages.get(i);
//            Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,imageItem.id);
            uris.add(imageItem.uri);

        }

        returnBackWithImageUris(uris);

    }

    private void returnBackWithImageUris(ArrayList<Uri> uris){
        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEMS,  uris);
        setResult(RESULT_OK, intent);
        finish();
    }




}
