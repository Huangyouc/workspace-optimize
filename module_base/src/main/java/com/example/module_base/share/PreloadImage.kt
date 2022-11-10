package com.example.module_base.share

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.module_base.ApplicationUtil

/**
 * 弃用UrlImageViewHelper，相关功能改用Glide实现
 */
class PreloadImage {
    companion object{
        fun load(imgUrl:String){
            Glide.with(ApplicationUtil.getContext()).asBitmap()
                .load(imgUrl).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .into(object : SimpleTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {

                    }
                })
        }

        /****兼容base64 和http的图片***/
        fun load(imgUrl:String,callBack: LoadCallback?){
            if(imgUrl.startsWith("http")){
                Glide.with(ApplicationUtil.getContext()).asBitmap()
                    .load(imgUrl).diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(object : SimpleTarget<Bitmap?>() {
                        override fun onResourceReady(
                            loadedBitmap: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            callBack?.onSuccess(loadedBitmap)
                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            callBack?.onFailed()
                        }
                    })
            }else{
                val loadedBitmap = getBitmapFromBase64(imgUrl)
                callBack?.onSuccess(loadedBitmap)
            }
        }

        fun getBitmapFromBase64(base64: String?): Bitmap? {
            if(TextUtils.isEmpty(base64)) {
                return null
            }
            var str:String?=null
            if(base64!!.contains("base64,")){
                str = base64.split("base64,")[1]
            }else{
                str = base64
            }
            var bitmap: Bitmap? = null
            try {
                val decode = Base64.decode(str, Base64.DEFAULT)
                bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.size);
            } catch (t: Throwable) {
                //do nothing
            }
            return bitmap
        }
    }

    interface LoadCallback{
        fun onSuccess(loadedBitmap: Bitmap?)
        fun onFailed()
    }
}