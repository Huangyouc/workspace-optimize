package com.example.processor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/***
 * 注解的申明和注解处理器要分module处理，这样打包的时候，处理器所在的module不会被打入apk中
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface MethodProcessor {
    String name() default "Method";
}
