package cn.jailedbird.arouter.ksp.test

import cn.jailedbird.module.api.AutoService

@AutoService
class TestImpl1<T> : TestInterface1<String> {
}


@AutoService(target = TestInterface2::class)
class TestImpl2<T> : TestInterface2<String> {
}
