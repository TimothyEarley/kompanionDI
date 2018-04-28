package de.earley.companionDI.mocking

import de.earley.companionDI.Provider

class MockProvider<out T, P>(
		val mockedProvider: Provider<T, P>,
		provider: Provider<T, P>
) : Provider<T, P> by provider