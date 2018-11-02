package de.earley.kompanionDI

/**
 * A function capable of taking a profile and injector and returning a concrete instance of type T.
 */
// T has out variance
typealias Provider<T, P> = (P, Injector<P>) -> T

// extra providers

/**
 * Key to identify arguments from [[singleton]] calls, i.e. when the profile changes the singletons
 * become invalid.
 */
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
fun <T, P> lazyValue(value: () -> T): Provider<T, P> {
	val v by lazy { value() }
	return { _, _ -> v }
}

/**
 * A lazy [Provider] that provides the result of [value]. The [value] function is called once when the
 * first value is requested.
 */
@Deprecated(
	replaceWith = ReplaceWith("lazyValue(value)"),
	message = "Replace with better named lazyValue",
	level = DeprecationLevel.HIDDEN
)
fun <T, P> lazyBean(value: () -> T): Provider<T, P> = lazyValue(value)

/**
 * A straightforward [Provider] that always returns [value].
 */
fun <T, P> value(value: T): Provider<T, P> = { _, _ -> value }

/**
 * A straightforward [Provider] that always returns [value].
 */
@Deprecated(
	replaceWith = ReplaceWith("value(value)"),
	message = "Replace with better named value",
	level = DeprecationLevel.HIDDEN
)
fun <T, P> bean(value: T): Provider<T, P> = value(value)
