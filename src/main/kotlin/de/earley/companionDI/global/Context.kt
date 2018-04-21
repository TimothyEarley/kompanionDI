package de.earley.companionDI.global

import de.earley.companionDI.Dependency
import de.earley.companionDI.mocking.MockMap
import de.earley.companionDI.mocking.MutableMockMap

/*
 * Context represents a configuration for the DI meaning that it can instantiate dependencies
 * without being invoked from a dependency itself
 */

class Context<P>(
		private val profile: P,
		private val mocks: MockMap<P>
) {
	fun <T> inject(dependency: Dependency<T, P>): T = dependency.create(profile, mocks)
}

class MutableContext<P>(
		var profile: P,
		var mocks : MutableMockMap<P>
) {
	fun <T> inject(dependency: Dependency<T, P>): T = dependency.create(profile, mocks)
}