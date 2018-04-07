package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

class ValueDependency<out T, P>(
		private val value: T
) : Dependency<T, P> {
	override fun create(profile: P, mocks: MockMap<P>, mockable: Boolean): T = value
}