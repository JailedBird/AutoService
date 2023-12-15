package cn.jailedbird.spi.test

interface TestInterface1<T> {
    fun hello()
}

interface TestInterface2<T> {
}


interface TestInterface3 {
    fun hello()
}

open class TestClass3<T>
