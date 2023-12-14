package cn.jailedbird.arouter.ksp.compiler

import cn.jailedbird.arouter.ksp.compiler.utils.*
import cn.jailedbird.module.api.AutoSPI
import com.google.devtools.ksp.KspExperimental
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

        private fun log(s: String) {
            logger.warn("::fuck:$s")
        }

        @OptIn(KspExperimental::class)
        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(ROUTE_CLASS_NAME)

            val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

            elements.forEach { router ->
                // 1: check annotation
                val spi: AutoSPI = router.findAnnotationWithType()
                    ?: error("Error ksp process, with [AutoSPISymbolProcessorProvider]")
                val target = spi.target
                if (target.qualifiedName == Void::class.qualifiedName || target.qualifiedName == Unit::class.qualifiedName) {
                    log(target.qualifiedName.toString())
                    val pair = router.getOnlyParent()
                    if (pair.first) {
                        generate(
                            router,
                            pair.second!!.resolve().declaration.qualifiedName!!.asString(),
                            router.toClassName().canonicalName
                        )
                    } else {
                        error("Please configure target")
                    }
                } else {
                    if (router.isSubclassOf(spi.target.qualifiedName!!)) {
                        println("fuck ok")
                        generate(
                            router,
                            spi.target.qualifiedName!!,
                            router.toClassName().toString()
                        )

                    } else {
                        error("AutoSPI.target is ${spi.target.qualifiedName}, but ${router.simpleName.asString()} is not a subclass of  ${spi.target.qualifiedName}")
                    }
                }

                println(target)

            }

            return emptyList()
        }

        private fun generate(element: KSClassDeclaration, interfaceName: String, implName: String) {
            codeGenerator.createNewFile(
                Dependencies(false, element.containingFile!!),
                "META-INF/services",
                interfaceName,
                ""
            ).use {
                it.write(implName.toByteArray())
            }
        }

    }



}

