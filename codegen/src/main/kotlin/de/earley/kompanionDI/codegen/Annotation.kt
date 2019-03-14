package de.earley.kompanionDI.codegen

import kotlin.reflect.KClass

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
