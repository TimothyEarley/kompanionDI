package de.earley.companionDI

/**
 * Creates a mockable dependency using the provider as a create method
 */
inline fun <reified T, P> provide(noinline provider: Provider<T, P>): Dependency<T, P> = provide(T::class.java, provider)

/* forward inline to this method to avoid leaking MockableDependencyProvider */
fun <T, P> provide(clazz: Class<T>, provider: Provider<T, P>): Dependency<T, P> = MockableDependencyProvider(clazz, provider)

/**
 * Create a bean TODO: WIP
 */
inline fun <reified T, P> bean(value: T): Dependency<T, P> = bean(T::class.java, value)

/* forward inline to this method to avoid leaking MockableDependencyProvider */
fun <T, P> bean(clazz: Class<T>, value: T): Dependency<T, P> =
		MockableDependencyProvider(clazz, { _, _ -> value })
