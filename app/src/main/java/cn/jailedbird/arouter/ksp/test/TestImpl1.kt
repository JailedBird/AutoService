package cn.jailedbird.arouter.ksp.test

import cn.jailedbird.module.api.AutoSPI

@AutoSPI
class TestImpl1<T> : TestInterface1<String> {
}


@AutoSPI(target = TestInterface2::class)
class TestImpl2<T> : TestInterface2<String> {
}
