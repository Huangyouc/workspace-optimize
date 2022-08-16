package com.noahwm.kotlin.util

import android.util.Log
import okhttp3.internal.http1.Http1ExchangeCodec

object NetManager {
     var codecMap = mutableMapOf<Http1ExchangeCodec,String>()
    var urlMap = mutableMapOf<String,Long>()

    fun addHttp1ExchangeCodecAndUrl(exchangeCodec: Http1ExchangeCodec,url: String){
        codecMap.put(exchangeCodec,url)
    }
    fun putStartTime(url:String,startTime:Long){
        Log.e("NetManager putStartTime","url = $url")
        Log.e("NetManager putStartTime","startTime = $startTime")
        urlMap.put(url,startTime)
    }

    fun putEndTime(exchangeCodec: Http1ExchangeCodec,endTime:Long){
        if(codecMap.containsKey(exchangeCodec)){
            val url = codecMap.get(exchangeCodec)
            codecMap.remove(exchangeCodec)
            Log.e("NetManager putEndTime","Http1ExchangeCodec = $exchangeCodec")
            if(urlMap.containsKey(url)){
                var diffTime = endTime - urlMap.get(url)!!
                urlMap.remove(url)
                Log.e("NetManager putEndTime","url = $url")
                Log.e("NetManager putEndTime","endTime = $endTime")
                Log.e("NetManager putEndTime","diffTime = $diffTime")
            }
        }
    }
}