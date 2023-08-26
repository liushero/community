package com.futurhero.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 通过注解来确定哪些请求需要拦截器拦截
 * Target：注解作用于什么上（类，属性，方法）
 * Retention：注解的生命周期，一般就选择runtime
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequired {
}
