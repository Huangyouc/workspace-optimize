package com.example.optimize.aspectj

@Target(AnnotationTarget.FUNCTION,AnnotationTarget.CONSTRUCTOR)
@Retention(AnnotationRetention.RUNTIME)
annotation class MethodTrace {

}