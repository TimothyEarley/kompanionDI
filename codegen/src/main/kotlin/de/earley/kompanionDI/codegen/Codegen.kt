package de.earley.kompanionDI.codegen

import com.google.auto.common.MoreElements
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.buildCodeBlock
import me.eugeniomarletti.kotlin.metadata.jvm.descriptor
import me.eugeniomarletti.kotlin.metadata.kotlinMetadata
import java.io.File
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ElementVisitor
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.lang.model.element.TypeParameterElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

fun codegen(
        element: Element,
        dir: File
) {

    val module = element.getAnnotation(Module::class.java)
    val name = element.simpleName.toString()
    val packageName = MoreElements.getPackage(element).toString()

    val interfaces = (element as TypeElement).interfaces.map { it as DeclaredType }

    val mappings = module.l.map(ProviderFor::extractTypeMirrors)
    val profile = module.extractProfileTypeMirror().asTypeName()

    val interfaceSpec = TypeSpec.interfaceBuilder(element.interfaceName())
            .apply {
                mappings.forEach { (iface, _) ->
                    addProperty(iface.propertySpec(profile).build())
                }
            }
            .build()

    val type = TypeSpec.classBuilder(element.className())
            .addSuperinterface(element.interfaceName().asTypeName())
            .addModifiers(KModifier.OPEN)
            .primaryConstructor(FunSpec.constructorBuilder()
                    .apply {
                        interfaces.forEach {
                            addParameter(
                                    ParameterSpec.builder(it.variableName(), it.asElement().interfaceName().asTypeName())
                                            .defaultValue(CodeBlock.of("${it.asElement().className()}()"))
                                            .build()
                            )
                        }
                    }
                    .build())
            .apply {
                interfaces.forEach {
                    addSuperinterface(it.asElement().interfaceName().asTypeName(), delegate = CodeBlock.of(it.variableName()))
                }


               mappings.forEach { (iface, clazz) ->
                   val constructors = (clazz.asElement() as? TypeElement)
                           ?.getConstructors() ?: error("No constructor found")

                   //TODO choose constructor
                   val constructor = constructors.first() as? ExecutableElement ?: error("Malformed constructor")

                   val parameters = constructor.parameters
                           .map(VariableElement::asType)
                           .map { it as DeclaredType }
                           .map { it.variableName() }
                           .map { "inject($it)" }

                   val property = iface.propertySpec(profile)
                           .addModifiers(KModifier.OVERRIDE)
                           .initializer("""
{ profile, inject ->
    ${clazz.qualifiedName()}(${parameters.joinToString(separator = ",")})
}
                                        """)
                           .build()

                   addProperty(property)
               }
            }
            .build()

    FileSpec.builder(packageName, name + "Impl")
            .addType(interfaceSpec)
            .addType(type)
            .build()
            .writeTo(dir)

}


// see http://hauchee.blogspot.com/2015/12/compile-time-annotation-processing-getting-class-value.html
// we are using the lazy approach, as the annotation is nested and things get really complicated quickly
private fun ProviderFor.extractTypeMirrors(): Pair<DeclaredType, DeclaredType> {
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

    return ifaceMirror to clazzMirror
}

private fun Module.extractProfileTypeMirror(): DeclaredType = try {
    profile
    error("Should have thrown")
} catch (e: MirroredTypeException) {
    e.typeMirror
} as DeclaredType

private fun providerType(declaredType: DeclaredType, profile: TypeName): TypeName = with(ParameterizedTypeName) {
    return ClassName.bestGuess("de.earley.kompanionDI.Provider")
            .parameterizedBy(declaredType.asTypeName(), profile)
}

private fun TypeElement.getConstructors() = enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }
private fun Element.className() = simpleName.toString() + "Impl"
private fun Element.interfaceName() = simpleName.toString() + "Spec"
private fun DeclaredType.variableName() = asElement().simpleName.toString().lowerCaseFirst()
private fun String.lowerCaseFirst(): String = first().toLowerCase() + drop(1)
private fun DeclaredType.qualifiedName() = asElement().asType().toString()
private fun String.asTypeName() = ClassName.bestGuess(this)
private fun DeclaredType.propertySpec(profile: TypeName): PropertySpec.Builder =
        PropertySpec.builder(variableName(), providerType(this, profile))