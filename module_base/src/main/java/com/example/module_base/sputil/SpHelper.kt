package com.example.module_base.sputil

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.module_base.ApplicationUtil

/**
 * 默认的SharedPreferences，保存文件路径为 data/data/包名/shared_prefs/context.getPackageName() + "_preferences"
 */
class SpHelper() : AbstractSp("") {
    companion object{
        private var instance:SpHelper?=null

        @Synchronized
        fun getInstance():SpHelper{
            if(instance==null){
                instance = SpHelper()
            }
            return instance!!
        }
    }

    override fun initInstance(spType: String?) {
        sp = PreferenceManager.getDefaultSharedPreferences(ApplicationUtil.getContext()) as SharedPreferences
        editor = sp!!.edit()
    }
}