package de.earley.kompanionDI.codegen

import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asTypeName
import de.earley.kompanionDI.codegen.codegen.InterfaceSpec
import de.earley.kompanionDI.codegen.codegen.ProviderSpec
import de.earley.kompanionDI.codegen.codegen.moduleCodegen
import de.earley.kompanionDI.codegen.codegen.qualifiedName
import de.earley.kompanionDI.codegen.codegen.toTypeName
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@Suppress("unused")
class TypedComponentProcessor : GeneratingProcessor() {
    lateinit var modules: Set<TypeElement>
    lateinit var components: Set<TypeElement>
    init {
        process<ComponentModule> {
            modules = it
        }
        process<TypedComponent> { s ->
            components = s
        }
    }

    override fun finish(roundEnv: RoundEnvironment) {
        // generate each module

        modules.forEach { elem ->
            val module = elem.getAnnotation(ComponentModule::class.java)
            val comps = components
                    .filter { e ->
                        //TODO does not work with repeatable annotations
                        //TODO clean up, no need to compute the annotation all the time
                        e.getAnnotationsByType(TypedComponent::class.java)
                                .any {
                                    it.extractModuleTypeMirror().asElement().qualifiedName() == elem.qualifiedName()
                                }
                    }
            val name = elem.simpleName.toString()
            val packageName = MoreElements.getPackage(elem).takeUnless { it.isUnnamed }?.toString() ?: error("Cannot run processor on elements without a package")
            val deps = module.extractDependenciesTypeMirror().map(::InterfaceSpec)
            val providers = comps.map { ProviderSpec(it.getPrimaryInterfaceOrSelf(), it) }
            val profile = "Unit".toTypeName() //TODO profile
            val inheritFromRef = (module.extractInheritTypeMirror().asTypeName() as ClassName).takeUnless { it.canonicalName == "java.lang.Object" }
            val inheritFrom = inheritFromRef?.let { ClassName(it.packageName, it.simpleName + "Spec") }

            moduleCodegen(
                    packageName = packageName,
                    interfaces = deps,
                    name = name,
                    profile = profile,
                    providers = providers,
                    inheritFrom = inheritFrom
            ).writeTo(generatedSourcesRoot)
        }
    }

    private fun TypedComponent.extractModuleTypeMirror(): DeclaredType = try {
        module
        error("Should have thrown")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    } as DeclaredType

    private fun ComponentModule.extractDependenciesTypeMirror(): List<TypeMirror> = try {
        dependencies
        error("Should have thrown")
    } catch (e: MirroredTypesException) {
        e.typeMirrors
    }


    private fun ComponentModule.extractInheritTypeMirror(): TypeMirror = try {
        inheritFrom
        error("Should have thrown")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }
}

