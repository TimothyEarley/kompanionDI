package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.MutableMockMap

/**
 * Utility interface for creating dependency with a given profile and mocks
 */
interface Injector<P> {
	val profile: P
	val mocks: MockMap<P>
}

/**
 * The injection function. Creates an instance of T.
 * Uses the mocks and profile it has set.
 */
operator fun <T, P> Injector<P>.invoke(provider: Provider<T, P>): T =
		mocks.get(provider)?.invoke(profile, this)
				?: provider.invoke(profile, this)

abstract class MutableInjector<P> : Injector<P> {
	abstract override var profile: P
	abstract override val mocks: MutableMockMap<P>
}

internal class InjectorImpl<P>(
		override val profile: P,
		override val mocks: MockMap<P>
) : Injector<P>

internal class MutableInjectorImpl<P>(
		override var profile: P,
		override val mocks: MutableMockMap<P>
) : MutableInjector<P>()
