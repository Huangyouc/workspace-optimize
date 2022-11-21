package com.example.module_base

import android.app.Activity
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.view.View
import android.view.Window
import android.view.WindowManager


class NotchUtils {

    companion object {

        val DEFAULT = 0
        val HUAWEI = 1
        val OPPO = 2
        val VIVO = 3
        val XIAOMI = 4

        //华为刘海屏全屏显示FLAG
        const val HUAWEI_FLAG_NOTCH_SUPPORT = 0x00010000

        //小米刘海屏开启配置
        const val XIAOMI_FLAG_OPEN_CONFIG = 0x00000100

        //小米刘海屏竖屏配置
        const val XIAOMI_FLAG_PORTRAIT = 0x00000200

        //小米刘海屏横屏配置 没有横屏页面 暂不需要
        //const val XIAOMI_FLAG_LANDSCAPE = 0x00000400

        //VIVO刘海屏判断
        const val VIVO_FLAG_NOTCH_SCREEN = 0x00000020

        fun getRom(): Int {
            if (RomUtil.isEMUI() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return HUAWEI
            } else if (RomUtil.isOPPO() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return OPPO
            } else if (RomUtil.isVIVO() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return VIVO
            } else if (RomUtil.isMIUI() && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
                return XIAOMI
            } else {
                return DEFAULT
            }
        }

        fun isNotchScreen(): Boolean {
            //原生判断刘海屏是异步流程，因此不建议先判断是否是刘海屏
            return true
        }

        fun isNotchScreenHuawei(ctx: Context?): Boolean {
            try {
                ctx?.let {
                    val cl = it.classLoader
                    val HwNotchSizeUtil = cl?.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                    val get = HwNotchSizeUtil?.getMethod("hasNotchInScreen")
                    return (get?.invoke(HwNotchSizeUtil) as Boolean?) ?: false
                }
                return false
            } catch (e: Exception) {
                //e.printStackTrace()
                return false
            }
        }

        fun isNotchScreenXiaomi(): Boolean {
            try {
                val str: String? = RomUtil.getSystemProperty("ro.miui.notch", "")
                return "1".equals(str)
            } catch (e: Exception) {
                //e.printStackTrace()
                return false
            }
        }

        fun isNotchScreenOppo(ctx: Context?): Boolean {
            try {
                ctx?.let {
                    return (it.packageManager?.hasSystemFeature("com.oppo.feature.screen.heteromorphism") ?: false)
                }
                return false
            } catch (e: Exception) {
                //e.printStackTrace()
                return false
            }
        }

        fun isNotchScreenVivo(ctx: Context?): Boolean {
            try {
                ctx?.let {
                    val cl = it.classLoader
                    val ftFeature = cl?.loadClass("android.util.FtFeature");
                    val method = ftFeature?.getMethod("isFeatureSupport", Int::class.javaPrimitiveType);
                    return ((method?.invoke(ftFeature, VIVO_FLAG_NOTCH_SCREEN) as Boolean?) ?: false)
                }
                return false
            } catch (e: Exception) {
                //e.printStackTrace()
                return false
            }
        }

        /**
         * 针对刘海屏全屏
         */
        fun fullScreen(activity: Activity?) {
            try {
                this.fullScreen(activity, true)
            } catch (e: Exception) {
                //e.printStackTrace()
            }
        }

        /**
         * @param open true 开启 false 关闭
         */
        fun fullScreen(activity: Activity?, open: Boolean? = true) {
            if (!isNotchScreen()) {
                return
            }
            val _open = open ?: true
            activity?.let {
                val type = getRom()
                when (type) {
                    DEFAULT -> fullScreenDefault(it.window, _open)
                    HUAWEI -> fullScreenHuawei(it.window, _open)
                    OPPO -> {
                        //需要用户去系统设置里面开启全屏显示
                    }
                    VIVO -> {
                        //需要用户去系统设置里面开启全屏显示
                    }
                    XIAOMI -> fullScreenXiaomi(it.window, _open)
                }
            }
        }

        /**
         * 供我的名片-使用
         * 将title下移statusbar的高度 防止被刘海屏遮挡
         */
        fun translateViewInNotch(activity: Activity?, view: View?) {
            if (!isNotchScreen()) {
                return
            }
            if (activity == null || view == null) {
                return
            }
            val window = activity.window
            val type = getRom()
            when (type) {
                DEFAULT -> translateViewInNotchDefault(window, view)
                HUAWEI -> translateViewInNotchHuawei(view)
                OPPO -> {
                    //需要判断系统设置里面该应用的全屏设置是否开启，方法暂未知
                }
                VIVO -> {
                    //需要判断系统设置里面该应用的全屏设置是否开启，方法暂未知
                }
                XIAOMI -> translateViewInNotchXiaomi(view)
            }

        }

        /**
         * @param open true 延伸到刘海屏 false 不延伸到刘海屏
         */
        fun fullScreenDefault(window: Window?, open: Boolean) {
            window?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    try {
                        it.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
                        val attr = it.attributes
                        if (open) {
                            attr.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                        } else {
                            attr.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_DEFAULT
                        }
                        it.attributes = attr
                    } catch (e: Exception) {
                        //e.printStackTrace()
                    }
                }
            }
        }

        /**
         * @param open true 延伸到刘海屏 false 不延伸到刘海屏
         */
        fun fullScreenHuawei(window: Window?, open: Boolean) {
            window?.let {
                try {
                    val layoutParams = it.attributes
                    val layoutParamsExCls = Class.forName("com.huawei.android.view.LayoutParamsEx")
                    val con = layoutParamsExCls.getConstructor(WindowManager.LayoutParams::class.java)
                    val layoutParamsExObj = con.newInstance(layoutParams)
                    val methodName = if (open) "addHwFlags" else "clearHwFlags"
                    val method = layoutParamsExCls.getMethod(methodName, Int::class.javaPrimitiveType)
                    method.invoke(layoutParamsExObj, HUAWEI_FLAG_NOTCH_SUPPORT)
                    if (it.decorView.layoutParams != null) {
                        it.windowManager?.updateViewLayout(it.decorView, it.decorView.layoutParams)
                    }
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }

        fun fullScreenXiaomi(window: Window?, open: Boolean) {
            window?.let {
                try {
                    val flag = XIAOMI_FLAG_OPEN_CONFIG or XIAOMI_FLAG_PORTRAIT
                    val methodName = if (open) "addExtraFlags" else "clearExtraFlags"
                    val method = Window::class.java.getMethod(methodName, Int::class.javaPrimitiveType)
                    method.invoke(window, flag)
                    if (it.decorView.layoutParams != null) {
                        it.windowManager?.updateViewLayout(it.decorView, it.decorView.layoutParams)
                    }
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }

        fun translateViewInNotchDefault(window: Window?, view: View?) {
            window?.let {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        val decorView = window.decorView
                        decorView?.post {
                            try {
                                val displayCutout = decorView?.rootWindowInsets?.displayCutout
                                val safeInsetTop = displayCutout?.safeInsetTop ?: 0
                                if (safeInsetTop > 0) {
                                    view?.translationY = safeInsetTop.toFloat()
                                }
                            } catch (e: Exception) {
                                //e.printStackTrace()
                            }
                        }
                    }
                } catch (e: Exception) {
                    //e.printStackTrace()
                }
            }
        }

        fun translateViewInNotchHuawei(view: View?) {
            var ret = intArrayOf(0, 0)
            try {
                view?.let {
                    val cl = it.context?.getClassLoader()
                    val HwNotchSizeUtil = cl?.loadClass("com.huawei.android.util.HwNotchSizeUtil")
                    val get = HwNotchSizeUtil?.getMethod("getNotchSize")
                    ret = get?.invoke(HwNotchSizeUtil) as IntArray
                    if (ret[1] > 0) {
                        it.translationY = ret[1].toFloat()
                    }
                }
            } catch (e: Exception) {
                //e.printStackTrace()
            }
        }

        fun translateViewInNotchXiaomi(view: View?) {
            view?.let {
                if (isNotchScreenXiaomi()) {
                    try {
                        /**
                         * MIUI 针对 Notch 设备，有一个“隐藏屏幕刘海”的设置项（设置-全面屏-隐藏屏幕刘海），
                         * 具体表现是：系统会强制盖黑状态栏（无视应用的Notch使用声明），视觉上达到隐藏刘海的效果。
                         * 但会给某些应用带来适配问题（控件/内容遮挡或过于靠边等）
                         *
                         * 通过查询以下 Global settings 来确定「隐藏屏幕刘海」是否开启
                         */
                        val flag = (Settings.Global.getInt(it.context.contentResolver, "force_black", 0) == 1)
                        if (flag) {
                            return
                        }
                    } catch (e: Exception) {
                        //e.printStackTrace()
                    }
                    //MIUI 10 新增了获取刘海宽和高的方法，需升级至8.6.26开发版及以上版本
                    //这里先取notch高度，取不到则取statusBar高度
                    var height = 0
                    try {
                        try {
                            val resourceId = it.context.resources.getIdentifier("notch_height", "dimen", "android")
                            if (resourceId > 0) {
                                height = it.context.resources.getDimensionPixelSize(resourceId)
                            }
                        } catch (e: Exception) {
                            //e.printStackTrace()
                        }
                        if (height <= 0) {
                            try {
                                val resourceId2 = it.context.resources.getIdentifier("status_bar_height", "dimen", "android")
                                if (resourceId2 > 0) {
                                    height = it.context.resources.getDimensionPixelSize(resourceId2)
                                }
                            } catch (e: Exception) {
                                //e.printStackTrace()
                            }
                        }
                        if (height > 0) {
                            it.translationY = height.toFloat()
                        }
                    } catch (e: Exception) {
                        //e.printStackTrace()
                    }
                }
            }
        }

    }
}
