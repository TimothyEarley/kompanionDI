package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import javax.lang.model.element.Element
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

data class ProviderSpec(
        private val iface: TypeElement,
        val clazz: TypeElement
) {

    constructor(iface: DeclaredType, clazz: DeclaredType):
            this(iface.asElement() as TypeElement, clazz.asElement() as TypeElement)

    fun propertySpec(profile: TypeName): PropertySpec.Builder =  PropertySpec.builder(iface.variableName(), providerType(iface, profile))

    private fun providerType(inner: TypeElement, profile: TypeName): TypeName = with(ParameterizedTypeName) {
        return ClassName.bestGuess("de.earley.kompanionDI.Provider").parameterizedBy(inner.asClassName(), profile)
    }

    fun fullyQualifiedConstructor(): String = clazz.qualifiedName()

    fun getConstructor(): ExecutableElement {
        val constructors = clazz.getConstructors().ifEmpty { error("No constructor found") }

        //TODO choose constructor
        return constructors.first() as? ExecutableElement ?: error("Malformed constructor")
    }

}

internal fun providerCodegen(provider: ProviderSpec, profile: TypeName): PropertySpec {

    val parameters = provider.getConstructor().parameters
            .map(VariableElement::asType)
            .map { it as DeclaredType }
            .map { it.variableName() }
            .map { "inject($it)" }

    return provider.propertySpec(profile)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("""
{ profile, inject ->
    ${provider.fullyQualifiedConstructor()}(${parameters.joinToString(separator = ",")})
}
            """)
            .build()
}