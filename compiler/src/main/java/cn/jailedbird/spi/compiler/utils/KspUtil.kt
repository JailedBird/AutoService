package cn.jailedbird.spi.compiler.utils

import com.google.devtools.ksp.KSTypeNotPresentException
import com.google.devtools.ksp.KSTypesNotPresentException
import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.*
import kotlin.reflect.KClass

@OptIn(KspExperimental::class)
internal inline fun <reified T : Annotation> KSAnnotated.findAnnotationWithType(): T? {
    return getAnnotationsByType(T::class).firstOrNull()
}

internal inline fun KSPLogger.check(condition: Boolean, message: () -> String) {
    check(condition, null, message)
}

internal inline fun KSPLogger.check(condition: Boolean, element: KSNode?, message: () -> String) {
    if (!condition) {
        error(message(), element)
    }
}

/**
 * Judge whether a class [KSClassDeclaration] is a subclass of another class [superClassName]
 * https://www.raywenderlich.com/33148161-write-a-symbol-processor-with-kotlin-symbol-processing
 * */
internal fun KSClassDeclaration.isSubclassOf(
    superClassName: String,
): KSDeclaration? {
    val superClasses = superTypes.toMutableList()
    while (superClasses.isNotEmpty()) {
        val current: KSTypeReference = superClasses.first()
        val declaration: KSDeclaration = current.resolve().declaration
        when {
            declaration is KSClassDeclaration && declaration.qualifiedName?.asString() == superClassName -> {
                return declaration
            }

            declaration is KSClassDeclaration -> {
                superClasses.removeAt(0)
                superClasses.addAll(0, declaration.superTypes.toList())
            }

            else -> {
                superClasses.removeAt(0)
            }
        }
    }
    return null
}

/*
fun parseAnnotationClassParameter(block: () -> List<KClass<*>>): List<String> {
    return try { // KSTypesNotPresentException will be thrown
        block.invoke().mapNotNull { it.qualifiedName }
    } catch (e: KSTypesNotPresentException) {
        val res = mutableListOf<String>()
        val ksTypes = e.ksTypes
        for (ksType in ksTypes) {
            val declaration = ksType.declaration
            if (declaration is KSClassDeclaration) {
                declaration.qualifiedName?.asString()?.let {
                    res.add(it)
                }
            }
        }
        res
    }
}*/

@OptIn(KspExperimental::class)
fun parseAnnotationClassParameter(block: () -> KClass<*>): String? {
    return try { // KSTypesNotPresentException will be thrown
        block.invoke().qualifiedName
    } catch (e: KSTypeNotPresentException) {
        var res: String? = null
        val declaration = e.ksType.declaration
        if (declaration is KSClassDeclaration) {
            declaration.qualifiedName?.asString()?.let {
                res = it
            }
        }
        res
    }
}



