package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

/**
 * Mock the dependency or fall back onto the dependency
 * TODO test T with out variance
 */
class MockableDependencyProvider<T, P>(
		private val clazz: Class<T>,
		private val dependency: Dependency<T, P>
): Dependency<T, P> {

	override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T {
		return if (mockable) mocks.get(clazz, profile) ?: dependency.create(profile, mocks, mockable)
		else dependency.create(profile, mocks, mockable)
	}

}