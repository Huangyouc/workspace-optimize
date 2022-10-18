package com.geekthings.module_imagepicker.loader;

import android.app.Activity;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.geekthings.module_imagepicker.R;
import com.geekthings.module_imagepicker.model.ImageItem;

/**
 * Created by sunny on 2017/11/12.
 */

public class GlideImageLoader implements ImageLoader {

    private static final RequestOptions mOptions;
    static {
        mOptions = new RequestOptions()
                .placeholder(R.drawable.ic_default_image)
                .error(R.drawable.ic_default_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL);//缓存全尺寸

    }

    @Override
    public void displayImage(Activity activity, ImageItem image, ImageView imageView, int width, int height) {
        Glide.with(activity)                             //配置上下文
                .load(image.uri)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .apply(mOptions)
                .into(imageView);
    }

    @Override
    public void displayImagePreview(Activity activity, ImageItem image, ImageView imageView, int width, int height) {
        Glide.with(activity)                             //配置上下文
                .load(image.uri != null ? image.uri : image.path)      //设置图片路径(fix #8,文件名包含%符号 无法识别和显示)
                .apply(mOptions)
                .into(imageView);
    }

    @Override
    public void clearMemoryCache() {
    }
}
