package de.earley.kompanionDI.mocking

import de.earley.kompanionDI.Provider

interface MockMap<P> {

	/**
	 * Try to get a mock for [provider]. If none exist in this map, return null.
	 */
	fun <T, D : Provider<T, P>> get(provider: D): D?

	/**
	 * Returns true if no mocks are registered, otherwise false.
	 */
	fun isEmpty(): Boolean

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
				if (mocks.isEmpty()) MockMap.empty()
				else HashMockMap(mocks.associateBy { it.mockedProvider })

		private val EMPTY = object : MockMap<Any?> {
			override fun <T, D : Provider<T, Any?>> get(provider: D): D? = null
			override fun isEmpty(): Boolean = true
		}
	}
}

private class HashMockMap<P>(
		private val map: Map<Provider<*, P>, Provider<*, P>>
) : MockMap<P> {

	@Suppress("UNCHECKED_CAST") // guaranteed by MockProvider
	override fun <T, D : Provider<T, P>> get(provider: D): D? {
		return map[provider] as D?
	}
	override fun isEmpty(): Boolean = map.isEmpty() // should always be false, but call to make sure
}