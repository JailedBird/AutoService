package cn.jailedbird.arouter.ksp.test

import cn.jailedbird.module.api.AutoSPI

@AutoSPI
class TestImpl1<T> : TestInterface<String> {
}


@AutoSPI(target = TestInterface::class)
class TestImpl2<T> : TestInterface<String> {
}
