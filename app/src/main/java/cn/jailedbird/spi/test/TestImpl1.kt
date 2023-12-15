package cn.jailedbird.spi.test

import cn.jailedbird.spi.api.AutoService

@AutoService
class TestImpl1<T> : TestInterface1<String> {
}


@AutoService(target = TestInterface2::class)
class TestImpl2<T> : TestInterface2<String> {
}

// @AutoService(/*target = TestClass3::class*/)
// class TestImpl3<T> : TestClass3<String>() {
// }
