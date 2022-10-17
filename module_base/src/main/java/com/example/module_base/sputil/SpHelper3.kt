package com.example.module_base.sputil

import android.content.Context
import com.example.module_base.ApplicationUtil

class SpHelper3(type:String) : AbstractSp(type) {

    companion object{
        private var instance:SpHelper3?=null

        @Synchronized
        fun getInstance():SpHelper3{
            if(instance==null){
                instance = SpHelper3(SpUtil.SHARE_H5_STORAGE)
            }
            return instance!!
        }
    }

    override fun initInstance(spType: String?) {
        sp = ApplicationUtil.getContext().getSharedPreferences(spType,Context.MODE_PRIVATE)
        editor = sp!!.edit()
    }
}