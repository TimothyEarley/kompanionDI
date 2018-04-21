package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap
import de.earley.companionDI.mocking.MutableMockMap
import de.earley.companionDI.mocking.asMutable

/**
 * Utility interface for creating dependency with a given profile and mocks
 */
interface Injector<P> {

	val mocks: MockMap<P>

	operator fun <T> invoke(dependency: Dependency<T, P>): T

	fun <T> bean(clazz: Class<T>, proof: Dependency<T, P>): T

}


internal class MutableInjector<P>(
		var profile: P,
		override val mocks: MutableMockMap<P>
) : Injector<P> {

	constructor(profile: P, mocks: MockMap<P>): this(profile, mocks.asMutable())

	override operator fun <T> invoke(dependency: Dependency<T, P>): T =
			// mock the dependency itself
			mocks.get(dependency.javaClass, profile)?.create(profile, mocks)
			?: dependency.create(profile, mocks)

	//TODO needs manuel mocking - rethink design?
	override fun <T> bean(clazz: Class<T>, proof: Dependency<T, P>): T = mockOrCreate(clazz, profile, mocks, proof)

}

// could be replaced with HKT when available
inline fun <reified T, P> Injector<P>.bean(proof: Dependency<T, P>): T = bean(T::class.java, proof)
