package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

/**
 * Basic implementation satisfying the contract for de.earley.companionDI.create
 * TODO test T with out variance
 */
internal class MockableDependencyProvider<T, P>(
		private val clazz: Class<T>,
		private val provider: Provider<T, P>
) : Dependency<T, P> {

	override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T =
			if (mockable) mocks.get(clazz, profile) ?: provider.create(profile, mocks)
			else provider.create(profile, mocks)

}