package com.noahwm.kotlin.util

import android.util.Log
import okhttp3.Request
import okhttp3.internal.connection.RealConnection
import okhttp3.internal.http1.Http1ExchangeCodec
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import java.lang.reflect.Field

@Aspect
class OkhttpAspect {
    //    fun writeRequestHeaders(joinPoint: ProceedingJoinPoint) {//如果是 before 或者after类型，JoinPoint类型
    @Before("execution(* okhttp3.internal.http1.Http1ExchangeCodec.writeRequestHeaders(..))")
    fun writeRequestHeaders(joinPoint: JoinPoint) {//如果是 before 或者after类型，JoinPoint类型
        injectCode4StartTime(joinPoint)
    }

//    fun readResponseHeaders(joinPoint: JoinPoint) {//如果是 before 或者after类型，JoinPoint类型
    @Before("execution(* okhttp3.internal.http1.Http1ExchangeCodec.readHeaders(..))")
    fun readHeaders(joinPoint: JoinPoint) {//如果是 before 或者after类型，JoinPoint类型
        injectCode4EndTime(joinPoint)
    }


    fun injectCode4StartTime(joinPoint: JoinPoint) {
        //在原代码之前插入
         val startTime = System.currentTimeMillis()
        val args= joinPoint.args
        val request:Request = args[0] as Request
        val url = request.url().url().toString()
        NetManager.putStartTime(url,startTime)
        NetManager.addHttp1ExchangeCodecAndUrl(joinPoint.target as Http1ExchangeCodec,url)
        //执行原代码
//        joinPoint.proceed()
    }

    fun injectCode4EndTime(joinPoint: JoinPoint) {
       //在原代码之前插入
        var endTime = System.currentTimeMillis()
        NetManager.putEndTime(joinPoint.target as Http1ExchangeCodec,endTime)
        //获取类的字节码对象，通过字节码对象获取方法信息
//        val targetCls: Class<*> = joinPoint.target.javaClass
//        val  field:Field = targetCls.getDeclaredField("realConnection")
//        field.isAccessible = true
//        val realConnection: RealConnection = field.get(joinPoint.target) as RealConnection
//        if (realConnection != null) {
//            var url  = realConnection.route().address().url().url().toString()
//            NetManager.putEndTime(url,endTime)
//        }
        //执行原代码
//        joinPoint.proceed()
    }
}

//CallServerInterceptor
//exchange.responseHeadersStart();
//
//Http1ExchangeCodec.readResponseHeaders 方法第一次执行readHeaderLine() 的时候，就是读返回的该接口的状态信息。
//        在调用Http1ExchangeCodec.readHeaders()的时候，就是计算首包时间最正确的节点