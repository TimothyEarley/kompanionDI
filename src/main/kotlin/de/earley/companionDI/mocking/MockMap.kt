package de.earley.companionDI.mocking

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
	fun <T: Any> add(mock: Mocking<T, P>)
}

internal class HashMockMap<P> : MutableMockMap<P> {

	private val map: MutableMap<Class<*>, Mocking<*, P>> = HashMap()


	@Suppress("UNCHECKED_CAST")
	override fun <T> get(clazz: Class<T>, profile: P): T? {
		return map[clazz]?.create(profile, this) as? T

	}

	override fun <T : Any> add(mock: Mocking<T, P>) {
		map[mock.clazz] = mock
	}
}