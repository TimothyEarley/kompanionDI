package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeName

fun moduleCodegen(
        packageName: String,
        name: String,
        providers: List<ProviderSpec>,
        profile: TypeName,
        interfaces: List<InterfaceSpec>
): FileSpec {
    val spec = (name + "Spec")
    val impl = (name + "Impl")

    return FileSpec.builder(packageName, impl)
            .addType(interfaceSpec(spec, providers, profile))
            .addType(implementationSpec(impl, spec.toTypeName(), interfaces, providers, profile))
            .build()
}