package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap

/**
 * Ability to get an instance from a [Provider].
 */
interface Context<out DI, P> {

	/**
	 * Instantiate an object from the provider returned by [[getProvider]]
	 */
	operator fun <T> invoke(getProvider: DI.() -> Provider<T, P>): T

	companion object {

		/**
		 * Create the DI context backed by [di] with [Unit] as the profile and [mocks].
		 * [DI] contains all instances of [Provider] available to the user.
		 */
		fun <DI> create(di: DI, mocks: MockMap<Unit> = MockMap.empty()): Context<DI, Unit> = create(di, Unit, mocks)

		/**
		 * Create the DI context backed by [di] with [profile] and [mocks].
		 * [DI] contains all instances of [Provider] available to the user.
		 */
		fun <DI, P> create(di: DI, profile: P, mocks: MockMap<P> = MockMap.empty()): Context<DI, P>
				= ContextBackedByInjector(di, Injector.create(profile, mocks))

	}
}

/**
 * Main implementation of [Context].
 * Stores a reference to the collection of providers ([DI]) and an [Injector]
 */
internal class ContextBackedByInjector<out DI, P>(
		private val di: DI,
		private val injector: Injector<P>
): Context<DI, P> {
	override operator fun <T> invoke(getProvider: DI.() -> Provider<T, P>): T = injector(di.getProvider())
}