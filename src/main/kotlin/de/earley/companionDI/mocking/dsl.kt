package de.earley.companionDI.mocking

import de.earley.companionDI.Dependency
import de.earley.companionDI.Provider

/**
 * Mock the class T with a value/object
 */
inline infix fun <reified T : Any, P> Dependency<T, P>.mockedBy(value: T): Mocking<T, P> =
		T::class.java.beanBy(value)

/**
 * Mock the class T with a provider
 */
inline infix fun <reified T : Any, P> Dependency<T, P>.mockedBy(noinline provider: Provider<T, P>): Mocking<T, P> =
		T::class.java.beanBy(provider)

/**
 * Mock the class T by a class with a companion object implementing Dependency
 * Or use any other Dependency implementation
 */
inline infix fun <reified T : Any, P> Dependency<T, P>.mockedBy(dependency: Dependency<T, P>): Mocking<T, P> =
		T::class.java.beanBy(dependency)


//TODO document & test beanBy
//TODO create variations for KClass

infix fun <T : Any, P> Class<T>.beanBy(value: T): Mocking<T, P> =
		Mocking.DirectMock(this, value)

infix fun <T : Any, P> Class<T>.beanBy(provider: Provider<T, P>): Mocking<T, P> =
		Mocking.ProviderMock(this, provider)

infix fun <T : Any, P> Class<T>.beanBy(dependency: Dependency<T, P>): Mocking<T, P> =
		Mocking.ProviderMock(this, { p, injector -> dependency.create(p, injector.mocks, false) })

fun <P> mocksOf(vararg mocks: Mocking<*, P>): MockMap<P> = mutableMocksOf(*mocks)

fun <P> mutableMocksOf(vararg mocks: Mocking<*, P>): MutableMockMap<P> = HashMockMap<P>().apply {
	mocks.forEach { add(it) }
}
