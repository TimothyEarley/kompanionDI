package de.earley.companionDI.mocking

import de.earley.companionDI.Dependency
import de.earley.companionDI.Injector
import de.earley.companionDI.Provider

sealed class Mocking<T : Any, P>(
		val clazz: Class<T>
): Dependency<T, P> {

	internal class DirectMock<T : Any, P>(
			clazz: Class<T>,
			private val value: T
	): Mocking<T, P>(clazz) {
		override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T = value
	}

	internal class ProviderMock<T : Any, P>(
			clazz: Class<T>,
			private val provider: Provider<T, P>
	) : Mocking<T, P>(clazz) {
		override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T =
				provider(profile, Injector(profile, mocks))
	}

}