package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap

/**
 * Utility interface for creating dependency with a given profile and mocks.
 * Used as an arguments to [Provider]
 */
interface Injector<P> {
	val profile: P
	val mocks: MockMap<P>

	/**
	 * The injection function. Creates an instance of T.
	 * Uses the mocks and profile it has set.
	 */
	operator fun <T> invoke(provider: Provider<T, P>): T =
			mocks.get(provider)?.invoke(profile, this)
					?: provider.invoke(profile, this)

	companion object {

		/**
		 * Create a new injector with [Unit] as the profile and the given [mocks].
		 */
		fun create(mocks: MockMap<Unit> = MockMap.empty()): Injector<Unit> = create(Unit, mocks)

		/**
		 * Create a new injector with a given [profile] and the given [mocks].
		 */
		fun <P> create(profile: P, mocks: MockMap<P> = MockMap.empty()): Injector<P> = InjectorImpl(profile, mocks)

	}

}

private class InjectorImpl<P>(
		override val profile: P,
		override val mocks: MockMap<P>
) : Injector<P>
