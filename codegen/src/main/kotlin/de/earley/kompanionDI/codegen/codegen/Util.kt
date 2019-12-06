package de.earley.kompanionDI.codegen.codegen

import com.squareup.kotlinpoet.ClassName
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType

internal fun TypeElement.getConstructors() = enclosedElements.filter { it.kind == ElementKind.CONSTRUCTOR }


internal fun Element.qualifiedName() = asType().toString()
internal fun String.toTypeName() = ClassName.bestGuess(this)

fun TypeElement.getPrimaryInterfaceOrSelf(): TypeElement
        = (interfaces.singleOrNull() as? DeclaredType)?.asElement() as? TypeElement ?: this
