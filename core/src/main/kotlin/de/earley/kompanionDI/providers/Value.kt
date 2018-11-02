package de.earley.kompanionDI.providers

import de.earley.kompanionDI.Provider

/**
 * A lazy [Provider] that provides the result of [value]. The [value] function is called once when the
 * first value is requested.
 */
fun <T, P> lazyValue(value: () -> T): Provider<T, P> {
    val v by lazy { value() }
    return { _, _ -> v }
}

/**
 * A straightforward [Provider] that always returns [value].
 */
fun <T, P> value(value: T): Provider<T, P> = { _, _ -> value }

