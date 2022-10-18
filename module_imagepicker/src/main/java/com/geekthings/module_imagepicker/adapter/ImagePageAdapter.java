package com.geekthings.module_imagepicker.adapter;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.viewpager.widget.PagerAdapter;
import com.geekthings.module_imagepicker.ImagePicker;
import com.geekthings.module_imagepicker.model.ImageItem;
import com.geekthings.module_imagepicker.util.Utils;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;

/**
 * Created by sunny on 2017/11/12.
 */

public class ImagePageAdapter extends PagerAdapter {
    private int screenWidth;
    private int screenHeight;
    private ImagePicker imagePicker;
    private ArrayList<ImageItem> images = new ArrayList<>();
    private Activity mActivity;
    public PhotoViewClickListener listener;
    private OnItemPhotoClickListener mOnItemPhotoClickListener;

    public ImagePageAdapter(Activity activity, ArrayList<ImageItem> images) {
        this.mActivity = activity;
        this.images = images;

        DisplayMetrics dm = Utils.getScreenPix(activity);
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;
        imagePicker = ImagePicker.getInstance();
    }

    public void setData(ArrayList<ImageItem> images) {
        this.images = images;
    }

    public void setPhotoViewClickListener(PhotoViewClickListener listener) {
        this.listener = listener;
    }

    public void setOnItemPhotoClickListener(OnItemPhotoClickListener listener){
        this.mOnItemPhotoClickListener = listener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        PhotoView photoView = new PhotoView(mActivity);
        ImageItem imageItem = images.get(position);
        imagePicker.getImageLoader().displayImagePreview(mActivity, imageItem, photoView, screenWidth, screenHeight);
        photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
            @Override
            public void onPhotoTap(ImageView view, float x, float y) {
                if (listener != null) listener.OnPhotoTapListener(view, x, y);

            }
        });
        if(mOnItemPhotoClickListener != null){
            photoView.setOnClickListener(v -> mOnItemPhotoClickListener.onItemClick(position));
        }
        if(mOnItemPhotoClickListener != null){
            photoView.setOnLongClickListener(v -> {
                mOnItemPhotoClickListener.onItemLongClick(position);
                return true;
            });
        }
        container.addView(photoView);
        return photoView;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public interface PhotoViewClickListener {
        void OnPhotoTapListener(View view, float v, float v1);
    }

    public interface OnItemPhotoClickListener{

        void onItemLongClick(int position);

        void onItemClick(int position);
    }
}
