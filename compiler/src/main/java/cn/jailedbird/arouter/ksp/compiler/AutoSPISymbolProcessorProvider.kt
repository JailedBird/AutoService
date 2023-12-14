package cn.jailedbird.arouter.ksp.compiler

import cn.jailedbird.arouter.ksp.compiler.utils.KSPLoggerWrapper
import cn.jailedbird.arouter.ksp.compiler.utils.findAnnotationWithType
import cn.jailedbird.arouter.ksp.compiler.utils.getOnlyParent
import cn.jailedbird.arouter.ksp.compiler.utils.isSubclassOf
import cn.jailedbird.module.api.AutoSPI
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName


class AutoSPISymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoSPISymbolProcessor(
            KSPLoggerWrapper(environment.logger), environment.codeGenerator
        )
    }

    class AutoSPISymbolProcessor(
        private val logger: KSPLoggerWrapper,
        private val codeGenerator: CodeGenerator,
    ) : SymbolProcessor {
        companion object {
            private val ROUTE_CLASS_NAME = AutoSPI::class.qualifiedName!!
        }

        private fun log(s: String) {
            logger.warn("::fuck:$s")
        }

        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(ROUTE_CLASS_NAME)

            val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

            elements.forEach { router ->
                val spi: AutoSPI = router.findAnnotationWithType()
                    ?: error("Error ksp process, with [AutoSPISymbolProcessorProvider]")
                val targetClass = spi.target
                if (targetClass.qualifiedName == Void::class.qualifiedName || targetClass.qualifiedName == Unit::class.qualifiedName) {
                    log(targetClass.qualifiedName.toString())
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

                println(targetClass)

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

