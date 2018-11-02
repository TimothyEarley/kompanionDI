package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.MutableMockMap

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
}

/**
 * The injection function. Creates an instance of T.
 * Uses the mocks and profile it has set.
 */
@Suppress("EXTENSION_SHADOWED_BY_MEMBER") // see deprecated
@Deprecated(
	message = "Moved inside the Injector interface. Remove the import for this function.",
	replaceWith = ReplaceWith("invoke(provider)"),
	level = DeprecationLevel.HIDDEN
)
operator fun <T, P> Injector<P>.invoke(provider: Provider<T, P>): T =
		invoke(provider)


/**
 * An [Injector] which can can be mutated.
 * This can lead to strange behaviour when used directly, i.e. two calls to a provider might differ.
 */
abstract class MutableInjector<P> : Injector<P> {
	abstract override var profile: P
	abstract override val mocks: MutableMockMap<P>
}

// implementations

internal class InjectorImpl<P>(
		override val profile: P,
		override val mocks: MockMap<P>
) : Injector<P>

internal class MutableInjectorImpl<P>(
		override var profile: P,
		override val mocks: MutableMockMap<P>
) : MutableInjector<P>()
