package com.example.optimize

import android.app.Application
import com.example.module_base.ApplicationUtil
import com.example.optimize.fps.FpsMonitor
import com.hjq.toast.ToastUtils

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
        ActivityManager.instance.init(this)
        ApplicationUtil.init(this)
        ToastUtils.init(this)
    }

    companion object{
        private lateinit var instance: Application
        fun getInstance(): Application{
            return instance
        }
    }
}