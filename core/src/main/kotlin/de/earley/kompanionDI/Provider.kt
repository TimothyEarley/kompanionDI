package de.earley.kompanionDI

// T has out variance
typealias Provider<T, P> = (P, Injector<P>) -> T

// extra providers

// cache on hash of mocks ( I think this should be sufficient) TODO test/reason
private data class CacheKey<out P>(val profile: P, val mocks: Int)

fun <T, P> singleton(provider: Provider<T, P>): Provider<T, P> {

	val cache = mutableMapOf<CacheKey<P>, T>()

	return { p, i ->
		cache.getOrPut(CacheKey(p, i.mocks.hashState())) {
			provider(p, i)
		}
	}
}

fun <T, P> lazyBean(value: () -> T): Provider<T, P> {
	val v by lazy { value() }
	return { _, _ -> v }
}
fun <T, P> bean(value: T): Provider<T, P> = { _, _ -> value }