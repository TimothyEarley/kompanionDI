package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec

fun implementationSpec(
        className: String,
        specInterface: TypeName,
        interfaces: List<InterfaceSpec>,
        providers: List<ProviderSpec>,
        profile: TypeName,
        extend: TypeName?
): TypeSpec = TypeSpec.classBuilder(className)
        .addSuperinterface(specInterface)
        .apply {
            if (extend != null) superclass(extend)
        }
        .addModifiers(KModifier.OPEN)
        .primaryConstructor(FunSpec.constructorBuilder()
                .apply {
                    interfaces.forEach {
                        addParameter(it.interfaceParameterSpec())
                    }
                }
                .build()
        )
        .apply {
            interfaces.forEach {
                addSuperinterface(it.interfaceName.toTypeName(), delegate = CodeBlock.of(it.variableName))
            }

            providers.forEach { provider ->
                addProperty(providerCodegen(provider, profile))
            }
        }
        .build()