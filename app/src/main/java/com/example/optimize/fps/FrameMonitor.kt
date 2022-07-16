package com.example.optimize.fps

import android.util.Log
import android.view.Choreographer
import java.util.concurrent.TimeUnit

internal  class FrameMonitor : Choreographer.FrameCallback{
    private val choreographer = Choreographer.getInstance()
    private var frameStartTime: Long=0//这个记录上一帧到达的时间
    private var listeners = arrayListOf<FpsMonitor.FpsCallback>()
    private var frameCount=0//累计绘制了多少帧
    override fun doFrame(p0: Long) {
      var currentTimeMillis = TimeUnit.NANOSECONDS.toMillis(p0) //将纳秒转换成毫秒
        if(frameStartTime> 0){
            frameCount++
            var timeSpan = currentTimeMillis-frameStartTime
            if(timeSpan>1000){//超过1秒就计算下fps
                val fps = frameCount*1000/timeSpan.toDouble()
                Log.e("FrameMonitor","fps = "+fps)
                for (listener in listeners){
                    listener.onFrame(fps)
                }
            }
        }else{
            frameStartTime = currentTimeMillis
            frameCount=0
        }

        start()
    }

    fun start(){
        choreographer.postFrameCallback(this)
    }

    fun stop(){
        frameCount=0
        listeners.clear()
        choreographer.removeFrameCallback(this)
    }
    fun addListeners(callback: FpsMonitor.FpsCallback){
        listeners.add(callback)
    }
}