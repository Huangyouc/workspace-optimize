package com.example.module_base

import android.content.Context

class ApplicationUtil {

    companion object{
        /**
         * 初始化工具类
         *
         * @param context 上下文
         */
        private var context: Context? = null
        fun init(ctx: Context) {
            context = ctx.applicationContext
        }

        fun getContext(): Context {
            if (context != null) return context!!
            throw NullPointerException("you should init first")
        }
    }


}