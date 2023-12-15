# AutoServiceKspCompiler
[AutoService KSP annotation processor](https://github.com/JailedBird/AutoServiceKspCompiler)

## 简介

AutoServiceKspCompiler是自动为Service Provider Interface（SPI）生成 `META-INF/services` 配置的高性能KSP注解处理器插件；

效果如图：

![image-20231215171801088](https://zhaojunchen-1259455842.cos.ap-nanjing.myqcloud.com//imgimage-20231215171801088.png)

背过Java面试题的应该都知道SPI，对于Android开发者来说，可以使用SPI实现基于接口的模块解耦；具体可以参考这位大佬的文章 [Android 中 SPI 的使用](https://juejin.cn/post/6844903478272196615) ， 这里不在赘述；

## 接入 [![](https://jitpack.io/v/JailedBird/AutoServiceKspCompiler.svg)](https://jitpack.io/#JailedBird/AutoServiceKspCompiler)

1、 添加jitpack仓库，最新版本 [![](https://jitpack.io/v/JailedBird/AutoServiceKspCompiler.svg)](https://jitpack.io/#JailedBird/AutoServiceKspCompiler)

```kotlin
maven { url 'https://jitpack.io' }
```

2、 为项目接入ksp插件，详见 [build.gradle](https://github.com/JailedBird/AutoServiceKspCompiler/blob/main/build.gradle) ，如已配置则忽略

```kotlin
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id 'com.google.devtools.ksp' version '1.7.20-1.0.8' apply false
}
```



*注意：本项目使用kotlin1.7.20 & ksp 1.7.20-1.0.8， 低于kotlin1.7.20的可能会出现错误，高于kotlin1.7.20应该是会兼容的；*

3、 导入AutoService注解对应依赖

```kotlin
implementation("com.github.JailedBird.AutoServiceKspCompiler:AutoServiceApi:VERSION")
```

4、 需要使用插件的各个模块，使用ksp导入插件

```kotlin
ksp("com.github.JailedBird.AutoServiceKspCompiler:AutoServiceKspCompiler:VERSION")
```



## 使用

详见 [AutoServiceKspCompiler/app](https://github.com/JailedBird/AutoServiceKspCompiler)

1、 添加SPI机制需要的接口

```
interface TestInterface1<T> {
    fun hello()
}

interface TestInterface2<T> {
}
```

**注意：仅支持接口，非接口会在编译时报错**

2、 添加SPI机制的实现类

```
@AutoService
open class TestImpl1 : TestInterface1<String> {
    override fun hello() {
        Log.d("TAG", "TestImpl1@${this.hashCode()} hello ")
    }
}


@AutoService(target = TestInterface2::class)
class TestImpl2<T> : TestInterface2<String> {
}
```

其中，如果实现类是单继承接口则可忽略`target`，否则必须显示指定；

***注意：出于KSP注解处理性能（涉及增量编译）的考量，这里只支持 单接口对单实现的映射关系，多对一会对注解处理性能影响较大，得不偿失；***

3、 通过ServiceLoader加载实现类；

```
findViewById<Button>(R.id.test1).setOnClickListener {
            val service = ServiceLoader.load(TestInterface1::class.java).firstOrNull()
            if (service != null) {
                Toast.makeText(this, "已加载", Toast.LENGTH_SHORT).show()
            }

            service?.hello()
        }
```

**注意： 每次加载都会生成新的对象！**

```
cn.jailedbird.spi          D  TestImpl1@138565316 hello 
cn.jailedbird.spi          D  TestImpl1@208673584 hello 
cn.jailedbird.spi          D  TestImpl1@175356508 hello 
```



## 其他

如果本插件对您的学习或工作有帮助，请点亮star支持作者😘

PS：安利本人的Arouter Ksp注解处理器项目 [ArouterKspCompiler](https://github.com/JailedBird/ArouterKspCompiler) ，非常深入地使用 *ksp* 框架；
