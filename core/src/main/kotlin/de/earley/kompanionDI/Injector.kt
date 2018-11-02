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
		 * Create an injector with [Unit] as the profile and the given [mocks].
		 */
		fun create(mocks: MockMap<Unit> = MockMap.empty()): Injector<Unit> =
				if (mocks.isEmpty()) EmptyInjector
				else create(Unit, mocks)

		/**
		 * Create an injector with a given [profile] and the given [mocks].
		 */
		fun <P> create(profile: P, mocks: MockMap<P> = MockMap.empty()): Injector<P> = InjectorImpl(profile, mocks)

	}

}

/**
 * Fasttrack implementation for injectors with [Unit] profile and no mocks
 */
internal object EmptyInjector : Injector<Unit> {
	override val profile = Unit
	override val mocks: MockMap<Unit> = MockMap.empty()

	override fun <T> invoke(provider: Provider<T, Unit>): T = provider.invoke(Unit, this)
}

internal class InjectorImpl<P>(
		override val profile: P,
		override val mocks: MockMap<P>
) : Injector<P>
