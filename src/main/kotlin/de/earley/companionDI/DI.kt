package de.earley.companionDI

//TODO create singleton provide (cache the instance)
//TODO can aspect oriented programming be done? Can every instantitiantion be proxied?

/**
 * Creates a mockable dependency using the provider as a create method
 */
inline fun <reified T, P> provide(noinline provider: Provider<T, P>): Dependency<T, P> =
		MockableDependencyProvider(T::class.java, ProviderDependency(provider))

inline fun <reified T, P> singleton(noinline provider: Provider<T, P>): Dependency<T, P> =
		SingletonDependency(provide(provider))

/* Create a bean TODO: WIP */
fun <T, P> bean(value: T): Dependency<T, P> = ValueDependency(value)