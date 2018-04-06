package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

/**
 * Utility class for creating dependency with a given profile and mocks
 */
class Injector<P>(
		val profile: P,
		val mocks: MockMap<P>
) {

	operator fun <T> invoke(dependency: Dependency<T, P>) = dependency.create(profile, mocks)

	// could be replaced with HKT when available
	inline fun <reified T> bean(proof: Dependency<T, P>): T =
		mocks.get(T::class.java, profile) ?: proof.create(profile, mocks)
}