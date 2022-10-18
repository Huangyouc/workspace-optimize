package com.example.module_base;

import com.geekthings.module_imagepicker.ImagePicker;
import com.geekthings.module_imagepicker.loader.GlideImageLoader;
import com.geekthings.module_imagepicker.view.CropImageView;

/**
 * @ClassName: ImageUtils
 * @Author: cz
 * @CreateDate: 2019/4/23 10:12 AM
 * @Description:
 */
public class ImageUtils {

    public static void initImagePicker(boolean allowsEditing) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());  //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(allowsEditing);                          //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                  //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(1000);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(1000);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

}
