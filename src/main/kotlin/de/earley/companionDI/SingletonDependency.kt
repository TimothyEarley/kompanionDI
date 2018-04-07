package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

private data class CacheKey<P>(val profile: P, val mocks: MockMap<P>, val mockable: Boolean)

class SingletonDependency<T, P>(
		private val dependency: Dependency<T, P>
) : Dependency<T, P> {

	// not thread safe?
	private val cache = mutableMapOf<CacheKey<P>, T>()

	override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T =
			cache.getOrPut(CacheKey(profile, mocks, mockable)) {
				dependency.create(profile, mocks, mockable)
			}

}