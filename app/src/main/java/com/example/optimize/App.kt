package com.example.optimize

import android.app.Application
import com.example.module_base.AppManager
import com.example.module_base.ApplicationUtil
import com.example.module_base.DeviceUtil
import com.hjq.toast.ToastUtils
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        AppManager.getInstance().init(this)
        ApplicationUtil.init(this)
        ToastUtils.init(this)
        DeviceUtil.init(this)

        Logger.addLogAdapter(object : AndroidLogAdapter() {
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return BuildConfig.DEBUG
            }
        })
    }

    companion object{
        private lateinit var instance: Application
        fun getInstance(): Application{
            return instance
        }
    }
}