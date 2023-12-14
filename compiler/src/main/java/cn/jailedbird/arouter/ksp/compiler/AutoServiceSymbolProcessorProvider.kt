package cn.jailedbird.arouter.ksp.compiler

import cn.jailedbird.arouter.ksp.compiler.utils.KSPLoggerWrapper
import cn.jailedbird.arouter.ksp.compiler.utils.findAnnotationWithType
import cn.jailedbird.arouter.ksp.compiler.utils.getOnlyParent
import cn.jailedbird.arouter.ksp.compiler.utils.isSubclassOf
import cn.jailedbird.module.api.AutoService
import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.ksp.toClassName


class AutoServiceSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoServiceSymbolProcessor(
            KSPLoggerWrapper(environment.logger), environment.codeGenerator
        )
    }

    class AutoServiceSymbolProcessor(
        private val logger: KSPLoggerWrapper,
        private val codeGenerator: CodeGenerator,
    ) : SymbolProcessor {
        companion object {
            private val AUTO_SERVICE_CLASS_NAME = AutoService::class.qualifiedName!!
        }

        private fun log(str: String) {
            logger.warn(str)
        }

        @OptIn(KspExperimental::class)
        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(AUTO_SERVICE_CLASS_NAME)

            val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

            elements.forEach { element ->
                val spi: AutoService = element.findAnnotationWithType()
                    ?: error("Error ksp process, with [AutoSPISymbolProcessorProvider]")

                val targetClassName = try {
                    spi.target.qualifiedName.toString()
                } catch (e: Exception) {
                    // log("bean")
                    // log(((e as? KSTypeNotPresentException)?.cause as? ClassNotFoundException)?.message.toString())
                    ((e as? KSTypeNotPresentException)?.cause as? ClassNotFoundException)?.message.toString()
                }

                if (targetClassName == Void::class.qualifiedName || targetClassName == Unit::class.qualifiedName) {
                    val pair = element.getOnlyParent()
                    if (pair.first) {
                        generate(
                            element,
                            pair.second!!.resolve().declaration.qualifiedName!!.asString(),
                            element.toClassName().canonicalName
                        )
                    } else {
                        error("Please configure target")
                    }
                } else {
                    if (element.isSubclassOf(targetClassName)) {
                        println("fuck ok")
                        generate(
                            element,
                            targetClassName,
                            element.toClassName().toString()
                        )

                    } else {
                        error("AutoSPI.target is ${targetClassName}, but ${element.simpleName.asString()} is not a subclass of  ${spi.target.qualifiedName}")
                    }
                }


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

