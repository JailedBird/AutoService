package cn.jailedbird.module.api

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AutoSPI(val target: KClass<*> = Unit::class)