package de.earley.companionDI

import kotlin.collections.HashMap

interface MockMap<in P> {

	fun <T> get(clazz: Class<T>, profile: P): T?

	companion object {
		val EMPTY = object : MockMap<Any?> {
			override fun <T> get(clazz: Class<T>, profile: Any?): T? = null
		}
	}
}

interface MutableMockMap<P> : MockMap<P> {
	fun <T: Any> put(clazz: Class<T>, mock: Mocking<T, P>)
}

class HashMockMap<P> : MutableMockMap<P> {

	private val map: MutableMap<Class<*>, Mocking<*, P>> = HashMap()


	@Suppress("UNCHECKED_CAST")
	override fun <T> get(clazz: Class<T>, profile: P): T? {
		return map[clazz]?.create(profile, this) as? T

	}

	override fun <T : Any> put(clazz: Class<T>, mock: Mocking<T, P>) {
		map[clazz] = mock
	}
}


sealed class Mocking<out T : Any, P>(
		private val clazz: Class<T>
): Dependency<T, P> {

	fun addTo(map: MutableMockMap<P>) {
		map.put(clazz, this)
	}

	class DirectMock<out T : Any, P>(
			clazz: Class<T>,
			private val value: T
	): Mocking<T, P>(clazz) {
		override fun create(profile: P, mocks: MockMap<P>): T = value

	}

	class ProviderMock<out T : Any, P>(
			clazz: Class<T>,
			private val provider: Provider<T, P>
	) : Mocking<T, P>(clazz) {
		override fun create(profile: P, mocks: MockMap<P>): T {
			return provider(profile, Injector(profile, mocks))
		}
	}

}


inline infix fun <reified T : Any, P> Dependency<T, P>.mockedBy(value: T) =
		Mocking.DirectMock<T, P>(T::class.java, value)

inline infix fun <reified T : Any, P> Dependency<T, P>.mockedBy(noinline provider: Provider<T, P>) =
		Mocking.ProviderMock(T::class.java, provider)


fun <P> mocksOf(vararg mocks: Mocking<*, P>): MockMap<P> = mutableMocksOf(*mocks)

fun <P> mutableMocksOf(vararg mocks: Mocking<*, P>): MutableMockMap<P> = HashMockMap<P>().apply {
	mocks.forEach { mock -> mock.addTo(this) }
}
