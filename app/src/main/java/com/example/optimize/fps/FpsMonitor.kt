package com.example.optimize.fps

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView
import com.example.module_base.AppManager
import com.example.optimize.App
import com.example.optimize.R
import java.text.DecimalFormat

object FpsMonitor {

    val fpsView = FpsView()
    fun toggle() {
        fpsView.toggle()
    }

    fun listener(callback: FpsCallback) {
        fpsView.addListener(callback)
    }

    interface FpsCallback {
        fun onFrame(fps: Double)
    }

    class FpsView {
        private var params = WindowManager.LayoutParams()
        private var isPlaying = false
        private var application: Application = App.getInstance()
        private val fpsView:TextView =
            LayoutInflater.from(application).inflate(R.layout.fps_layout, null, false) as TextView
        private val decimal = DecimalFormat("#.0 fps")
        private var windowManager: WindowManager? = null
        private val frameMonitor = FrameMonitor()

        init {
            windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            params.height = WindowManager.LayoutParams.WRAP_CONTENT
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.flags =
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL//不让这个view可以点击
            params.format = PixelFormat.TRANSLUCENT
            params.gravity = Gravity.TOP or Gravity.RIGHT
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                params.type = WindowManager.LayoutParams.TYPE_TOAST
            }

            frameMonitor.addListeners(object : FpsCallback{
                override fun onFrame(fps: Double) {
                    fpsView.text = decimal.format(fps)
                }
            })

            AppManager.getInstance().addFrontBackCallback(object :
                AppManager.FrontBackCallback {
                override fun onChanged(front: Boolean) {
                    if (front) {
                        play()
                    } else {
                        stop()
                    }
                }
            })
        }

        private fun stop() {
            frameMonitor.stop()
            if(isPlaying){
                isPlaying = false
                windowManager!!.removeView(fpsView)
            }
        }

        private fun play() {
            if (!hasOverlayPermission()) {

                startOverlaySettingActivity()
                return
            }
            frameMonitor.start()
            if(!isPlaying){
                isPlaying = true
                windowManager!!.addView(fpsView,params)
            }
        }

        private fun startOverlaySettingActivity() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                AppManager.getInstance().getTopActivity()!!.startActivity(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + application.packageName)
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                )
            }
        }

        private fun hasOverlayPermission(): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(
                application
            )
        }

        fun toggle() {
            if(isPlaying){
                stop()
            }else{
                play()
            }
        }

        fun addListener(callback: FpsMonitor.FpsCallback) {

            frameMonitor.addListeners(callback)
        }
    }
}