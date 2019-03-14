package de.earley.kompanionDI.codegen

import com.google.auto.service.AutoService
import java.io.File
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KompanionProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@Suppress("unused")
class KompanionProcessor : AbstractProcessor() {

	companion object {
		const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
	}

	override fun getSupportedAnnotationTypes(): Set<String> =
		setOf(Module::class.java.name)


	override fun process(
		annotations: MutableSet<out TypeElement>?,
		roundEnv: RoundEnvironment?
	): Boolean {

		val generatedSourcesRoot = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]?.let(::File)
		if (generatedSourcesRoot == null) {
			processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "Cannot find root for generated sources")
			return false
		}
		roundEnv?.getElementsAnnotatedWith(Module::class.java)?.forEach { element ->
			codegen(element, generatedSourcesRoot)
		}

		return false // true
	}

}