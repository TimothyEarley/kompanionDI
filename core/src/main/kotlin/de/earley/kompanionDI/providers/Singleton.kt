package de.earley.kompanionDI.providers

import de.earley.kompanionDI.Injector
import de.earley.kompanionDI.Provider
import java.util.concurrent.ConcurrentHashMap

/**
 * A [Provider] that uses a cache to only have one instance per [Injector].
 * This assumes the profile and mocks are immutable
 */
fun <T, P> singleton(provider: Provider<T, P>): Provider<T, P> {
    // thread safe store for mapping injectors to values
    val cache = ConcurrentHashMap<Injector<P>, T>()
    return { p, i -> cache.getOrPut(i) {
        provider(p, i)
    }}
}