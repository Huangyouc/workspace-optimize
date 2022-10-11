package com.example.optimize.aspectj

import android.util.Log
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect

@Aspect
class ActivityAspect {
//    @Around("execution(* androidx.appcompat.app.AppCompatActivity.setContentView(..))")
//    fun setContentView(joinPoint: ProceedingJoinPoint) {//如果是 before 或者after类型，JoinPoint类型
//        AdviceCode(joinPoint)
//    }
//
//
//    //所有标注了 MethodTrace 注解的方法
//    @Around("execution(@com.example.optimize.aspectj.MethodTrace * *(..))")
//    fun  methodTrace(joinPoint: ProceedingJoinPoint){
//        AdviceCode(joinPoint)
//    }
//
//    private fun AdviceCode(joinPoint: ProceedingJoinPoint) {
//        val signature = joinPoint.signature
//        val className = signature.declaringType.simpleName
//        val methodName = signature.name
//
//        val time = System.currentTimeMillis()
//        joinPoint.proceed()
//        Log.e(
//            "ActivityAspect",
//            className + ":" + methodName + "  cost=" + (System.currentTimeMillis() - time)
//        )
//    }
}