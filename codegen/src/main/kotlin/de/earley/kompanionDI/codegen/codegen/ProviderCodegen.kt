package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType

data class ProviderSpec(
        private val iface: TypeName,
        val clazz: Element,
        val constructor: ExecutableElement
) {
    companion object {
        operator fun invoke(iface: ClassName, clazz: TypeElement) = ProviderSpec(iface, clazz, getConstructor(clazz))
    }

    fun propertySpec(profile: TypeName): PropertySpec.Builder =  PropertySpec.builder(iface.variableName(), providerType(iface, profile))

    private fun providerType(inner: TypeName, profile: TypeName): TypeName = with(ParameterizedTypeName) {
        return ClassName.bestGuess("de.earley.kompanionDI.Provider").parameterizedBy(inner, profile)
    }

}

private fun getConstructor(typeElement: TypeElement): ExecutableElement {
    val constructors = typeElement.getConstructors().ifEmpty { error("No constructor found") }

    //TODO choose constructor
    return constructors.first() as? ExecutableElement ?: error("Malformed constructor")
}

internal fun providerCodegen(provider: ProviderSpec, profile: TypeName): PropertySpec {

    val parameters = provider.constructor.parameters
            .map(VariableElement::asType)
            .map { it as DeclaredType }
            .map { it.variableName() }
            .map { "inject($it)" }


    val call = when (provider.constructor.kind) {
        //TODO what about the package?
        //TODO if we detect a function is returning a provider, we can use that (-> is this a good way to allow for singleton, etc.?)
        ElementKind.METHOD -> provider.constructor.simpleName
        ElementKind.CONSTRUCTOR -> provider.clazz.qualifiedName()
        ElementKind.STATIC_INIT -> TODO()
        ElementKind.INSTANCE_INIT -> TODO()
        else -> error("was expecting an executable")
    }

    return provider.propertySpec(profile)
            .addModifiers(KModifier.OVERRIDE)
            .initializer("""
{ profile, inject ->
    $call(${parameters.joinToString(separator = ",")})
}
            """)
            .build()
}