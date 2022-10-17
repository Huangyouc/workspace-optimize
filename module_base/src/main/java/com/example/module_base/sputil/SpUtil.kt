package com.example.module_base.sputil

import android.text.TextUtils

class SpUtil {
    companion object{
        /**
         * 保存文件数据
         */
        val FILE_SHARED = "file_shared"
        /**
         * 保存持久H5数据文件
         */
        val SHARE_H5_STORAGE = "share_h5_data"

        fun getSp(type:String?):AbstractSp{
            if(TextUtils.equals(FILE_SHARED,type)){
                return SpHelper2.getInstance()
            }else if(TextUtils.equals(SHARE_H5_STORAGE,type)){
                return SpHelper3.getInstance()
            }else{
                return SpHelper.getInstance()
            }
        }
    }
}