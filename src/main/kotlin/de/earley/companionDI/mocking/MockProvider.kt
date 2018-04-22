package de.earley.companionDI.mocking

import de.earley.companionDI.Provider

class MockProvider<out T, P>(
		val clazz: Class<*>,
		provider: Provider<T, P>
) : Provider<T, P> by provider