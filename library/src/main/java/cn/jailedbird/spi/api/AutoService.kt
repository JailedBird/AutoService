package cn.jailedbird.spi.api

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class AutoService(val target: KClass<*> = Unit::class)