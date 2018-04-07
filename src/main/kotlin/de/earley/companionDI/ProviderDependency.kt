package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

class ProviderDependency<out T, P>(
		private val provider: Provider<T, P>
) : Dependency<T, P> {

	override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T = provider.create(profile, mocks)
}