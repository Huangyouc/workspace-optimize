package com.geekthings.module_imagepicker.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.geekthings.module_imagepicker.ImagePicker;
import com.geekthings.module_imagepicker.R;
import com.geekthings.module_imagepicker.model.ImageItem;
import com.geekthings.module_imagepicker.util.BitmapUtil;
import com.geekthings.module_imagepicker.view.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class ImageCropActivity extends ImageBaseActivity implements View.OnClickListener, CropImageView.OnBitmapSaveCompleteListener {
    private CropImageView mCropImageView;
    private Bitmap mBitmap;
    private boolean mIsSaveRectangle;
    private int mOutputX;
    private int mOutputY;
    private ImageItem mImageItem;
    private ImagePicker imagePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        imagePicker = ImagePicker.getInstance();

        //初始化View
        findViewById(R.id.btn_back).setOnClickListener(this);
        Button btn_ok = (Button) findViewById(R.id.btn_ok);
        btn_ok.setText(getString(R.string.ip_send));
        btn_ok.setOnClickListener(this);
        TextView tv_des = (TextView) findViewById(R.id.tv_des);
        tv_des.setText(getString(R.string.ip_photo_crop));
        mCropImageView = (CropImageView) findViewById(R.id.cv_crop_image);
        mCropImageView.setOnBitmapSaveCompleteListener(this);

        //获取需要的参数
        mOutputX = imagePicker.getOutPutX();
        mOutputY = imagePicker.getOutPutY();
        mIsSaveRectangle = imagePicker.isSaveRectangle();

        mImageItem = getIntent().getParcelableExtra("seleted_image");

        mCropImageView.setFocusStyle(imagePicker.getStyle());
        mCropImageView.setFocusWidth(imagePicker.getFocusWidth());
        mCropImageView.setFocusHeight(imagePicker.getFocusHeight());

        //缩放图片
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageItem.uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        options.inSampleSize = calculateInSampleSize(options, displayMetrics.widthPixels, displayMetrics.heightPixels);
        options.inJustDecodeBounds = false;

        try {
            mBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(mImageItem.uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //设置默认旋转角度
        if(mBitmap != null){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                mCropImageView.setImageBitmap(mCropImageView.rotate(mBitmap, BitmapUtil.getBitmapDegree(this,mImageItem.uri)));

            }else{
                mCropImageView.setImageBitmap(mCropImageView.rotate(mBitmap, BitmapUtil.getBitmapDegree(mImageItem.path)));

            }
        }
//        mCropImageView.setImageURI(Uri.fromFile(new File(imagePath)));
    }

    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = width / reqWidth;
            } else {
                inSampleSize = height / reqHeight;
            }
        }
        return inSampleSize;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_back) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.btn_ok) {
            mCropImageView.saveBitmapToFile(imagePicker.getCropCacheFolder(this), mOutputX, mOutputY, mIsSaveRectangle);
        }
    }

    @Override
    public void onBitmapSaveSuccess(File file) {
//        Toast.makeText(ImageCropActivity.this, "裁剪成功:" + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();

        Uri uri= Uri.fromFile(file);
        ImageItem imageItem = new ImageItem();
        imageItem.path = file.getAbsolutePath();
        imageItem.uri = uri;



        Intent intent = new Intent();
        intent.putExtra(ImagePicker.EXTRA_RESULT_ITEM, (Parcelable) imageItem);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBitmapSaveError(File file) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCropImageView.setOnBitmapSaveCompleteListener(null);
        if (null != mBitmap && !mBitmap.isRecycled()) {
            mBitmap.recycle();
            mBitmap = null;
        }
    }

}
