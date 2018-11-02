package de.earley.kompanionDI.mocking

import de.earley.kompanionDI.Provider
import java.util.*

interface MockMap<P> {

	/**
	 * Try to get a mock for [provider]. If none exist in this map, return null.
	 */
	fun <T, D : Provider<T, P>> get(provider: D): D?

	/**
	 * Int representation of the current state
	 */
	fun hashState(): Int = 0

	@Suppress("UNCHECKED_CAST") // EMPTY can be safely cast since it does not do anything
	companion object {
		/**
		 * Create a empty [MockMap]
		 */
		fun <P> empty(): MockMap<P> = EMPTY as MockMap<P>


		/**
		 * Create a [MockMap] from the [mocks].
		 */
		fun <P> of(vararg mocks: MockProvider<*, P>): MockMap<P> =
				if (mocks.isEmpty()) MockMap.empty<P>()
				else MutableMockMap.of(*mocks)

		private val EMPTY = object : MockMap<Any?> {
			override fun <T, D : Provider<T, Any?>> get(provider: D): D? = null
		}
	}
}

interface MutableMockMap<P> : MockMap<P> {
	/**
	 * Add the [mock] to this map. Replaces any previous mock for the same provider.
	 */
	fun <T> add(mock: MockProvider<T, P>)

	companion object {
		/**
		 * Create a [MutableMockMap] from the [mocks].
		 */
		fun <P> of(vararg mocks: MockProvider<*, P>): MutableMockMap<P> = HashMockMap<P>().apply {
			mocks.forEach { add(it) }
		}
	}
}

internal class HashMockMap<P> : MutableMockMap<P> {

	// the correspondence between the *'s is checked by the setter (and MockProvider)
	private val map: MutableMap<Provider<*, P>, Provider<*, P>> = HashMap()

	@Suppress("UNCHECKED_CAST")
	override fun <T, D : Provider<T, P>> get(provider: D): D? {
		return map[provider] as D?
	}

	override fun <T> add(mock: MockProvider<T, P>) {
		map[mock.mockedProvider] = mock
	}

	override fun hashState(): Int {
		// hash each entry
		return map.entries.hashCode()
	}
}