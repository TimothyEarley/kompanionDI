package de.earley.kompanionDI.mocking

import de.earley.kompanionDI.Provider

/**
 * A [Provider] that mocks another provider ([[mockedProvider]]).
 */
class MockProvider<out T, P>(
		val mockedProvider: Provider<T, P>,
		provider: Provider<T, P>
) : Provider<T, P> by provider