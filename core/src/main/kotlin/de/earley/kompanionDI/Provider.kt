package de.earley.kompanionDI

/**
 * A function capable of taking a profile and injector and returning a concrete instance of type T.
 */
// T has out variance
typealias Provider<T, P> = (P, Injector<P>) -> T

// extra providers

// cache on hash of mocks ( I think this should be sufficient) TODO test/reason
private data class CacheKey<out P>(val profile: P, val mocks: Int)

/**
 * A [Provider] that uses a cache to only have one instance per profile/mocks.
 */
fun <T, P> singleton(provider: Provider<T, P>): Provider<T, P> {

	val cache = mutableMapOf<CacheKey<P>, T>()

	return { p, i ->
		cache.getOrPut(CacheKey(p, i.mocks.hashState())) {
			provider(p, i)
		}
	}
}

/**
 * A lazy [Provider] that provides the result of [value]. The [value] function is called once when the
 * first value is requested.
 */
//TODO change name to lazyValueProvider ?
fun <T, P> lazyBean(value: () -> T): Provider<T, P> {
	val v by lazy { value() }
	return { _, _ -> v }
}

/**
 * A straightforward [Provider] that always returns [value].
 */
//TODO change name to valueProvider ?
fun <T, P> bean(value: T): Provider<T, P> = { _, _ -> value }