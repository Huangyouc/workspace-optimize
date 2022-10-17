package com.example.module_base.sputil

import android.content.SharedPreferences

abstract class AbstractSp() {
    var sp: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
    constructor(spType:String?) : this() {
        initInstance(spType)
    }

    abstract fun initInstance(spType:String?)
    open fun put(key: String?, obj: Any) {
        if (obj is String) {
            editor!!.putString(key, obj)
        } else if (obj is Int) {
            editor!!.putInt(key, obj)
        } else if (obj is Boolean) {
            editor!!.putBoolean(key, obj)
        } else if (obj is Float) {
            editor!!.putFloat(key, obj)
        } else if (obj is Long) {
            editor!!.putLong(key, obj)
        } else {
            editor!!.putString(key, obj.toString())
        }
        editor!!.apply()
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     */
    open operator fun get(key: String?, defaultObject: Any?): Any? {
        if (defaultObject is String) {
            return sp!!.getString(key, defaultObject as String?)
        } else if (defaultObject is Int) {
            return sp!!.getInt(key, (defaultObject as Int?)!!)
        } else if (defaultObject is Boolean) {
            return sp!!.getBoolean(key, (defaultObject as Boolean?)!!)
        } else if (defaultObject is Float) {
            return sp!!.getFloat(key, (defaultObject as Float?)!!)
        } else if (defaultObject is Long) {
            return sp!!.getLong(key, (defaultObject as Long?)!!)
        }
        return null
    }

    /**
     * 移除某个key值已经对应的值
     */
    open fun remove(key: String?) {
        editor!!.remove(key)
        editor!!.apply()
    }

    /**
     * 清除所有数据
     */
    open fun clear() {
        editor!!.clear()
        editor!!.apply()
    }

    /**
     * 查询某个key是否已经存在
     */
    open operator fun contains(key: String?): Boolean {
        return sp!!.contains(key)
    }

    /**
     * 返回所有的键值对
     */
    open fun getAll(): Map<String?, *>? {
        return sp!!.all
    }
}