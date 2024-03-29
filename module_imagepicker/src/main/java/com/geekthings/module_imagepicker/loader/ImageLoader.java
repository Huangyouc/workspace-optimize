package com.geekthings.module_imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;

import com.geekthings.module_imagepicker.model.ImageItem;

import java.io.Serializable;

public interface ImageLoader extends Serializable {

    void displayImage(Activity activity, ImageItem image, ImageView imageView, int width, int height);

    void displayImagePreview(Activity activity, ImageItem image, ImageView imageView, int width, int height);

    void clearMemoryCache();
}
