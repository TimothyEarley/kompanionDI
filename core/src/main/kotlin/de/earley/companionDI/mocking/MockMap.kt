package de.earley.companionDI.mocking

import de.earley.companionDI.Provider
import java.util.*

interface MockMap<P> {

	fun <T, D : Provider<T, P>> get(clazz: Class<D>, profile: P): D?

	/**
	 * Int representation of the current state
	 */
	fun hashState(): Int = 0

	@Suppress("UNCHECKED_CAST") // EMPTY can be safely cast since it does not do anything
	companion object {
		fun <P> empty(): MockMap<P> = EMPTY as MockMap<P>

		private val EMPTY = object : MockMap<Any?> {
			override fun <T, D : Provider<T, Any?>> get(clazz: Class<D>, profile: Any?): D? = null
		}
	}
}

interface MutableMockMap<P> : MockMap<P> {
	fun <T> add(mock: MockProvider<T, P>)
}

internal class HashMockMap<P> : MutableMockMap<P> {

	private val map: MutableMap<Class<*>, Provider<*, P>> = HashMap()

	@Suppress("UNCHECKED_CAST")
	override fun <T, D : Provider<T, P>> get(clazz: Class<D>, profile: P): D? {
		return map[clazz] as D?
	}

	override fun <T> add(mock: MockProvider<T, P>) {
		map[mock.clazz] = mock
	}

	override fun hashState(): Int {
		// hash each entry
		return Objects.hash(*map.entries.toTypedArray())
	}
}