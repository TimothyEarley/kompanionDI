package de.earley.kompanionDI.codegen

import com.google.auto.service.AutoService
import de.earley.kompanionDI.codegen.codegen.InterfaceSpec
import de.earley.kompanionDI.codegen.codegen.ProviderSpec
import de.earley.kompanionDI.codegen.codegen.moduleCodegen
import de.earley.kompanionDI.codegen.codegen.qualifiedName
import de.earley.kompanionDI.codegen.codegen.toTypeName
import javax.annotation.processing.Processor
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedOptions(KAPT_KOTLIN_GENERATED_OPTION_NAME)
@Suppress("unused")
class ComponentProcessor : GeneratingProcessor() {

    init {
        process<Component> { elements ->
            val m = elements
                    .map { it to it.getAnnotation(Component::class.java) }
                    .groupBy { (_, a) -> a.module }

            // two pass (first collect provides information, then create modules)

            val provides = m.mapValues { (_, elements) ->
                elements.map { (e, _) ->
                    e.getPrimaryInterfaceOrSelf().qualifiedName()
                }
            }

            m.forEach { (module, elements) ->
                val providers = elements.map { (e, _) ->
                    ProviderSpec(e.getPrimaryInterfaceOrSelf(), e)
                }

                val dependencies = providers
                        .flatMap { provider ->
                            provider.getConstructor().parameters.map { param ->
                                val dependency = param.asType().toString()
                                provides.filterValues { it.contains(dependency) }.entries.singleOrNull()?.key
                                        ?: error("Could not satisfy dependency $dependency for ${provider.clazz}")
                            }
                        }
                        .filter { it != module }
                        .map(::InterfaceSpec)

                //TODO profile type
                //TODO package
                moduleCodegen("de.earley.test", module, providers, "Unit".toTypeName(), dependencies)
                        .writeTo(generatedSourcesRoot)
            }
        }
    }

}


private fun TypeElement.getPrimaryInterfaceOrSelf(): TypeElement =
        (interfaces.singleOrNull() as? DeclaredType)?.asElement() as? TypeElement ?: this