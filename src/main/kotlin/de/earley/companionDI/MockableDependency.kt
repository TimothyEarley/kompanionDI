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
		return if (mockable) mockOrCreate(clazz, profile, mocks, dependency)
		else dependency.create(profile, mocks, mockable)
	}

}

internal fun <T, P> mockOrCreate(clazz: Class<T>, profile: P, mocks: MockMap<P>, dependency: Dependency<T, P>): T =
		mocks.get(clazz, profile)
				?: dependency.create(profile, mocks)