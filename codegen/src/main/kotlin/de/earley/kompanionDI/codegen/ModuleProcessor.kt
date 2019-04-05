package de.earley.kompanionDI.codegen

import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.asTypeName
import de.earley.kompanionDI.codegen.codegen.InterfaceSpec
import de.earley.kompanionDI.codegen.codegen.ProviderSpec
import de.earley.kompanionDI.codegen.codegen.moduleCodegen
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@Suppress("unused")
class ModuleProcessor : GeneratingProcessor() {
	init {
		process<Module> { elements ->
			elements.forEach { element ->

				val module = element.getAnnotation(Module::class.java)
				val name = element.simpleName.toString()
				val packageName = MoreElements.getPackage(element).toString()
				val interfaces = element.interfaces.map { InterfaceSpec(it) }
				val providers = module.l.map(ProviderFor::extractTypeMirrors)
				val profile = module.extractProfileTypeMirror().asTypeName()

				moduleCodegen(packageName, name, providers, profile, interfaces)
						.writeTo(generatedSourcesRoot)
			}
		}
	}
}

// see http://hauchee.blogspot.com/2015/12/compile-time-annotation-processing-getting-class-value.html
// we are using the lazy approach, as the annotation is nested and things get really complicated quickly
private fun ProviderFor.extractTypeMirrors(): ProviderSpec {
	val ifaceMirror = try {
		iface
		error("Should have thrown")
	} catch (e: MirroredTypeException) {
		e.typeMirror
	} as DeclaredType

	val clazzMirror = try {
		clazz
		error("Should have thrown")
	} catch (e: MirroredTypeException) {
		e.typeMirror
	} as DeclaredType

	return ProviderSpec(ifaceMirror, clazzMirror)
}

private fun Module.extractProfileTypeMirror(): DeclaredType = try {
	profile
	error("Should have thrown")
} catch (e: MirroredTypeException) {
	e.typeMirror
} as DeclaredType