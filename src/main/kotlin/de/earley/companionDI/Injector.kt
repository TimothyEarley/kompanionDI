package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap
import de.earley.companionDI.mocking.MutableMockMap
import de.earley.companionDI.mocking.asMutable

/**
 * Utility interface for creating dependency with a given profile and mocks
 */
interface Injector<P> {

	val mocks: MockMap<P>

	operator fun <T> invoke(provider: Provider<T, P>): T

}

interface MutableInjector<P> : Injector<P> {

	var profile: P
	override val mocks: MutableMockMap<P>

}

internal class InjectorImpl<P>(
		override var profile: P,
		override val mocks: MutableMockMap<P>
) : MutableInjector<P> {

	override operator fun <T> invoke(provider: Provider<T, P>): T =
			// mock the dependency itself
			mocks.get(provider.javaClass, profile)?.invoke(profile, this)
			?: provider.invoke(profile, this)

}