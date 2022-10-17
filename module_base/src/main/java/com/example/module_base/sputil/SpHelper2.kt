package com.example.module_base.sputil

import android.content.Context
import com.example.module_base.ApplicationUtil

class SpHelper2(type:String) : AbstractSp(type) {

    companion object{
        private var instance:SpHelper2?=null

        @Synchronized
        fun getInstance():SpHelper2{
            if(instance==null){
                instance = SpHelper2(SpUtil.FILE_SHARED)
            }
            return instance!!
        }
    }

    override fun initInstance(spType: String?) {
        sp = ApplicationUtil.getContext().getSharedPreferences(spType,Context.MODE_PRIVATE)
        editor = sp!!.edit()
    }
}