package de.earley.kompanionDI.codegen

import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
annotation class ComponentModule(
        val dependencies: Array<KClass<*>> = [],
        val extend: KClass<*> = Any::class
)

@Target(AnnotationTarget.CLASS)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class TypedComponent(
        val module: KClass<*>
//TODO injection type (singleton, scoped?, factory)
)

@Target(AnnotationTarget.FUNCTION)
annotation class TypedProvide(
        val module: KClass<*>
)