package cn.jailedbird.arouter.ksp.compiler

import cn.jailedbird.arouter.ksp.compiler.utils.KspLoggerWrapper
import cn.jailedbird.arouter.ksp.compiler.utils.findAnnotationWithType
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
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration

class AutoServiceSymbolProcessorProvider : SymbolProcessorProvider {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return AutoServiceSymbolProcessor(
            KspLoggerWrapper(environment.logger), environment.codeGenerator
        )
    }

    class AutoServiceSymbolProcessor(
        private val logger: KspLoggerWrapper,
        private val codeGenerator: CodeGenerator,
    ) : SymbolProcessor {
        companion object {
            private val AUTO_SERVICE_CLASS_NAME = AutoService::class.qualifiedName!!
            private val VOID_LIST =
                listOf(Unit::class.qualifiedName!!, Void::class.qualifiedName!!)
        }

        @OptIn(KspExperimental::class)
        override fun process(resolver: Resolver): List<KSAnnotated> {
            val symbol = resolver.getSymbolsWithAnnotation(AUTO_SERVICE_CLASS_NAME)

            val elements = symbol.filterIsInstance<KSClassDeclaration>().toList()

            elements.forEach { element ->
                val spi: AutoService = element.findAnnotationWithType()
                    ?: error("Error ksp process, with [AutoSPISymbolProcessorProvider]")
                val targetInterfaceClassName = try {
                    spi.target.qualifiedName.toString()
                } catch (e: Exception) {
                    /**
                     * Bug: ksp: com.google.devtools.ksp.KSTypeNotPresentException: java.lang.ClassNotFoundException: cn.jailedbird.arouter.ksp.test.TestInterface
                     * Official document: https://github.com/google/ksp/issues?q=ClassNotFoundException++KClass%3C*%3E
                     * temporary fix method as follows, but it is not perfect!!!
                     * TODO completely fix it!
                     * */
                    ((e as? KSTypeNotPresentException)?.cause as? ClassNotFoundException)?.message.toString()
                }

                val targetImplClassName = element.qualifiedName!!.asString()

                if (targetInterfaceClassName in VOID_LIST) {
                    val supers = element.superTypes.toList()
                    if (supers.size == 1) {
                        val sp: KSDeclaration = supers[0].resolve().declaration
                        if (sp is KSClassDeclaration) {
                            if (sp.classKind != ClassKind.INTERFACE) {
                                error("${sp.qualifiedName!!.asString()} must be a interface")
                            }
                        } else {
                            error("Error type")
                        }
                        if ((sp as? KSClassDeclaration)?.classKind != ClassKind.INTERFACE) {
                            error("")
                        }
                        generate(
                            element, sp.qualifiedName!!.asString(), targetImplClassName
                        )
                    } else {
                        error("Please configure target")
                    }
                } else {
                    if (element.isSubclassOf(targetInterfaceClassName)) {
                        // TODO check targetInterfaceClassName:String is interface
                        generate(
                            element,
                            targetInterfaceClassName, targetImplClassName
                        )

                    } else {
                        error("AutoSPI.target is ${targetInterfaceClassName}, but ${element.simpleName.asString()} is not a subclass of  $targetInterfaceClassName")
                    }
                }

            }

            return emptyList()
        }

        private fun generate(element: KSClassDeclaration, interfaceName: String, implName: String) {
            codeGenerator.createNewFile(
                Dependencies(false, element.containingFile!!),
                "META-INF/services", interfaceName, ""
            ).use {
                it.write(implName.toByteArray())
            }
        }

    }


}

