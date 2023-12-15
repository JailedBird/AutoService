package cn.jailedbird.spi.test

import android.util.Log
import cn.jailedbird.spi.api.AutoService

@AutoService
open class TestImpl1 : TestInterface1<String> {
    override fun hello() {
        Log.d("TAG", "TestImpl1@${this.hashCode()} hello ")
    }
}


@AutoService(target = TestInterface2::class)
class TestImpl2<T> : TestInterface2<String> {
}


@AutoService
open class TestImpl3 : TestInterface3 {
    override fun hello() {
        Log.d("TAG", "TestImpl3@${this.hashCode()} hello ")
    }
}

// @AutoService(/*target = TestClass3::class*/)
// class TestImpl3<T> : TestClass3<String>() {
// }
