package de.earley.kompanionDI.codegen

import kotlin.reflect.KClass

//TODO add profile type here?
@Target(AnnotationTarget.CLASS)
annotation class Module(
        val dependencies: Array<KClass<*>> = [],
        val extend: KClass<*> = Any::class
)

//TODO we could merge these two annotations and figure out if it is a method or class at codegen time

@Target(AnnotationTarget.CLASS)
@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class Component(
        val module: KClass<*>
//TODO injection type (singleton, scoped?, factory)
)

@Target(AnnotationTarget.FUNCTION)
annotation class Provide(
        val module: KClass<*>
)