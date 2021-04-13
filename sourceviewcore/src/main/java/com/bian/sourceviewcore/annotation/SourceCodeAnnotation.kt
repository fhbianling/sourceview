package com.bian.sourceviewcore.annotation

/**
 * author fhbianling@163.com
 * date 2021/3/16 11:21
 * 类描述：
 */
@Target(AnnotationTarget.FILE)
@Retention(AnnotationRetention.SOURCE)
annotation class ProvideSourceCode(val key: String)