package cn.jailedbird.arouter.ksp.compiler

import cn.jailedbird.arouter.ksp.compiler.utils.*
import cn.jailedbird.module.api.AutoSPI
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import java.util.*


class AutoSPISymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoSPISymbolProcessor(
            KSPLoggerWrapper(environment.logger), environment.codeGenerator, environment.options
        )
    }

    class AutoSPISymbolProcessor(
        private val logger: KSPLoggerWrapper,
        private val codeGenerator: CodeGenerator,
        options: Map<String, String>
    ) : SymbolProcessor {
        @Suppress("SpellCheckingInspection")
        companion object {
            private val ROUTE_CLASS_NAME = AutoSPI::class.qualifiedName!!
            private val IROUTE_GROUP_CLASSNAME = Consts.IROUTE_GROUP.quantifyNameToClassName()
            private val IPROVIDER_GROUP_CLASSNAME = Consts.IPROVIDER_GROUP.quantifyNameToClassName()
        }

        private val moduleName = options.findModuleName(logger)
        private val generateDoc = Consts.VALUE_ENABLE == options[Consts.KEY_GENERATE_DOC_NAME]

        @OptIn(KspExperimental::class)
        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(ROUTE_CLASS_NAME)

            val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

            elements.forEach { router ->
                // 检测是否存在对应的接口
                router.getAnnotationsByType(AutoSPI::class)
                codeGenerator.createNewFile(
                    Dependencies(false, router.containingFile!!),
                    "META-INF/services",
                    router.toClassName().toString(),
                    ""
                ).use {
                    it.write(router.qualifiedName.toString().toByteArray())
                }

            }

            return emptyList()
        }
    }

}

