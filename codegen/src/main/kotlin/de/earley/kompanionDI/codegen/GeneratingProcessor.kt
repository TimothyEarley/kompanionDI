package de.earley.kompanionDI.codegen

import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import kotlin.reflect.KClass

const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

abstract class GeneratingProcessor : AbstractProcessor() {

    protected val processors: MutableMap<KClass<out Annotation>, (Set<TypeElement>) -> Unit> = HashMap()

    protected inline fun <reified T : Annotation> process(noinline f: (Set<TypeElement>) -> Unit) {
        processors[T::class] = f
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String?> = processors.keys.map { it.java.name }.toMutableSet()

    lateinit var generatedSourcesRoot: File

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {

        generatedSourcesRoot = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let(::File) ?: return false.also {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Cannot find root for generated sources")
        }

        processors.forEach { (type, func) ->
            @Suppress("UNCHECKED_CAST")
            func(roundEnv.getElementsAnnotatedWith(type.java) as Set<TypeElement>)
        }

        finish(roundEnv)

        return false // true
    }

    open fun finish(roundEnv: RoundEnvironment) = Unit

}