package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap
import de.earley.companionDI.mocking.Mocking
import de.earley.companionDI.mocking.mocksOf

/**
 * Any interface/class which can be injected needs to have a companion object extending this interface.
 *
 * Profile (P) is intended to be an enum that is consistent throughout the whole app.
 * TODO I can't figure our how to make P into a covariant constraint (since it is used in mocks)
 *
 * The mocks make having singletons provided more complicated, since a mock could change an object
 * further down the dependency hierarchy
 */
interface Dependency<out T, P> {
	/**
	 * Create a new instance of this dependency with the given profile using all the mocks available
	 * Mockable is a one time flag signaling the next object not to be mockable (will not inherit)
	 * //TODO this is the result of not coming up with a better solution
	 */
	fun create(profile: P, mocks: MockMap<P> = MockMap.EMPTY, mockable: Boolean = true): T
}

/**
 * Convenience method for dependencies without a profile
 */
fun <T> Dependency<T, Unit>.create(mocks: MockMap<Unit> = MockMap.EMPTY) = this.create(Unit, mocks)

/**
 * Convenience method for dependencies without a profile and vararg mocks
 */
fun <T> Dependency<T, Unit>.create(vararg mocks: Mocking<*, Unit>) = this.create(mocksOf(*mocks))

/**
 * Convenience method for vararg mocks
 */
fun <T, P> Dependency<T, P>.create(profile: P, vararg mocks: Mocking<*, P>): T = this.create(profile, mocksOf(*mocks))