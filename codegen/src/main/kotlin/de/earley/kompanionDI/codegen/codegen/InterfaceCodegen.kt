package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

class InterfaceSpec private constructor(
        val variableName: String,
        val implementationName: String,
        val interfaceName: String
) {
    constructor(typeMirror: TypeMirror): this(typeMirror as DeclaredType)
    constructor(declaredType: DeclaredType): this(
            variableName = declaredType.variableName(),
            implementationName = declaredType.asElement().simpleName.toString() + "Impl",
            interfaceName = declaredType.asElement().simpleName.toString() + "Spec"
    )
    constructor(simpleName: String) : this(
            variableName = simpleName.variableName(),
            implementationName = simpleName + "Impl",
            interfaceName = simpleName + "Spec"
    )
}

fun TypeName.variableName(): String = when (this) {
    is ClassName -> variableName()
    Dynamic -> TODO()
    is LambdaTypeName -> TODO()
    is ParameterizedTypeName -> TODO()
    is TypeVariableName -> TODO()
    is WildcardTypeName -> TODO()
}
fun ClassName.variableName(): String = simpleName.variableName()
fun DeclaredType.variableName(): String = asElement().simpleName.toString().variableName()
private fun String.variableName(): String = first().toLowerCase() + drop(1)


fun InterfaceSpec.interfaceParameterSpec(): ParameterSpec =
        ParameterSpec.builder(variableName, interfaceName.toTypeName())
                .defaultValue(CodeBlock.of("$implementationName()"))
                .build()

fun interfaceSpec(name: String, providers: List<ProviderSpec>, profile: TypeName): TypeSpec =
        TypeSpec.interfaceBuilder(name)
                .apply {
                    providers.forEach { provider ->
                        addProperty(provider.propertySpec(profile).build())
                    }
                }
                .build()
