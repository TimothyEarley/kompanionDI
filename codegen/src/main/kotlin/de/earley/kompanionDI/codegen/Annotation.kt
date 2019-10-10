package de.earley.kompanionDI.codegen

import java.lang.annotation.Inherited
import kotlin.reflect.KClass


//TODO add options for wrapper (e.g. singleton)

// should be generic, but that is not possible in java which is used by kapt
// https://discuss.kotlinlang.org/t/using-generic-annotation-classes-from-java/4332
annotation class ProviderFor(
        val iface: KClass<*>,
        /**
         * Needs to be a subclass of iface.
         */
        val clazz: KClass<*>
)

//TODO target, retention

annotation class Module(
        val l: Array<ProviderFor>,
        val profile: KClass<*>
)


annotation class Component(
        val module: String = "DI"
)

annotation class ComponentModule(
        val dependencies: Array<KClass<*>> = [],
        val inheritFrom: KClass<*> = Any::class
)

@Repeatable
@Retention(AnnotationRetention.SOURCE)
annotation class TypedComponent(
        val module: KClass<*>
//TODO injection type (singleton, scoped?, factory)
)