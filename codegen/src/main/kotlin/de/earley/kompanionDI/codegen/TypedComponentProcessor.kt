package de.earley.kompanionDI.codegen

import com.google.auto.common.MoreElements
import com.google.auto.service.AutoService
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import de.earley.kompanionDI.codegen.codegen.InterfaceSpec
import de.earley.kompanionDI.codegen.codegen.ProviderSpec
import de.earley.kompanionDI.codegen.codegen.getPrimaryInterfaceOrSelf
import de.earley.kompanionDI.codegen.codegen.moduleCodegen
import de.earley.kompanionDI.codegen.codegen.qualifiedName
import de.earley.kompanionDI.codegen.codegen.toTypeName
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
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
    lateinit var modules: Set<Element>
    lateinit var components: Set<TypeElement>
    lateinit var providers: Set<ExecutableElement>
    init {
        process<ComponentModule> {
            modules = it
        }
        process<TypedComponent> { s ->
            @Suppress("UNCHECKED_CAST") // has to be on a class
            components = s as Set<TypeElement>
        }
        process<TypedProvide> { s ->
            @Suppress("UNCHECKED_CAST") // has to be on a function
            providers = s as Set<ExecutableElement>
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
            val provides = providers
                    .filter { e ->
                        e.getAnnotationsByType(TypedProvide::class.java)
                                .any {
                                    it.extractModuleTypeMirror().asElement().qualifiedName() == elem.qualifiedName()
                                }
                    }

            val name = elem.simpleName.toString()
            val packageName = MoreElements.getPackage(elem).takeUnless { it.isUnnamed }?.toString() ?: error("Cannot run processor on elements without a package")
            val deps = module.extractDependenciesTypeMirror().map(::InterfaceSpec)
            val providers = comps.map { ProviderSpec(it.getPrimaryInterfaceOrSelf().asClassName(), it) } +
                    provides.map { ProviderSpec(it.returnType.asTypeName(), it, it) }
            val profile = "Unit".toTypeName() //TODO profile
            val extendFromRef = (module.extractExtendTypeMirror().asTypeName() as ClassName).takeUnless { it.canonicalName == "java.lang.Object" }
            val extendFrom = extendFromRef?.let { ClassName(it.packageName, it.simpleName + "Impl") }

            moduleCodegen(
                    packageName = packageName,
                    interfaces = deps,
                    name = name,
                    profile = profile,
                    providers = providers,
                    extend = extendFrom
            ).writeTo(generatedSourcesRoot)
        }
    }

    private fun TypedComponent.extractModuleTypeMirror(): DeclaredType = try {
        module
        error("Should have thrown")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    } as DeclaredType

    private fun TypedProvide.extractModuleTypeMirror(): DeclaredType = try {
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


    private fun ComponentModule.extractExtendTypeMirror(): TypeMirror = try {
        extend
        error("Should have thrown")
    } catch (e: MirroredTypeException) {
        e.typeMirror
    }
}

