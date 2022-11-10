package com.example.module_base

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.TextUtils
import android.util.Base64
import android.widget.ImageView

/**
 * Created by weiyue on 2018/9/5.
 */
class BitmapUtil {

    companion object {
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

        fun showBitmapByBase64(iv: ImageView?, base64: String?) {
            if(iv == null || TextUtils.isEmpty(base64)) {
                return
            }
            try {
                iv.setImageBitmap(getBitmapFromBase64(base64))
            } catch (t: Throwable) {
                //do nothing
            }
        }
    }
}
