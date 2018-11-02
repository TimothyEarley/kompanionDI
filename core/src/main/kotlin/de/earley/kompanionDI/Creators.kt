package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.HashMockMap
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.MockProvider
import de.earley.kompanionDI.mocking.MutableMockMap
import de.earley.kompanionDI.mocking.mutableMocksOf

//TODO move into namespaces/objects

/*
 * Helper methods for creating [Injector] and [Context] instances
 */

/**
 * Create an injector with [Unit] as the profile and no mocks.
 */
fun createInjector(): Injector<Unit> = createInjector(profile = Unit)

/**
 * Create an injector with [Unit] as the profile and all mocks contained in [mocks]
 */
fun createInjector(mocks: MockMap<Unit>): Injector<Unit> = createInjector(Unit, mocks)

/**
 * Create an injector with a given [profile] and [mocks] as varargs.
 */
fun <P> createInjector(profile: P, vararg mocks: MockProvider<*, P>): Injector<P>
		= createInjector(profile, MockMap.of(*mocks))

/**
 * Create an injector with a given [profile] and [mocks].
 */
fun <P> createInjector(profile: P, mocks: MockMap<P> = MockMap.empty()): Injector<P> = InjectorImpl(profile, mocks)

/**
 * Deprecated
 */
@Deprecated(
	message = "Move away from mutability",
	replaceWith = ReplaceWith("createInjector(profile, mocks)")
)
@Suppress("SpreadOperator")
fun <P> createMutableInjector(profile: P, vararg mocks: MockProvider<*, P>): MutableInjector<P>
		= createMutableInjector(profile, mutableMocksOf(*mocks))

/**
 * Deprecated
 */
@Deprecated(
	message = "Move away from mutability",
	replaceWith = ReplaceWith("createInjector(profile, mocks)")
)
@Suppress("SpreadOperator")
fun <P> createMutableInjector(profile: P, mocks: MutableMockMap<P> = HashMockMap()): MutableInjector<P>
		= MutableInjectorImpl(profile, mocks)

/**
 * Create the DI context backed by [di] with [profile] and [mocks].
 * [DI] contains all instances of [Provider] available to the user.
 */
fun <DI, P> createContext(di: DI, profile: P, vararg mocks: MockProvider<*, P>): Context<DI, P>
		= createContext(di, profile, MockMap.of(*mocks))

/**
 * Create the DI context backed by [di] with Unit as the profile and [mocks].
 * [DI] contains all instances of [Provider] available to the user.
 */
fun <DI> createContext(di: DI, vararg mocks: MockProvider<*, Unit>): Context<DI, Unit>
		= createContext(di, Unit, MockMap.of(*mocks))

/**
 * Create the DI context backed by [di] with Unit as the profile and [mocks].
 * [DI] contains all instances of [Provider] available to the user.
 */
fun <DI> createContext(di: DI, mocks: MockMap<Unit> = MockMap.empty()): Context<DI, Unit>
		= createContext(di, Unit, mocks)

/**
 * Create the DI context backed by [di] with [profile] and [mocks].
 * [DI] contains all instances of [Provider] available to the user.
 */
fun <DI, P> createContext(di: DI, profile: P, mocks: MockMap<P> = MockMap.empty()): Context<DI, P>
		= ContextBackedByInjector(di, createInjector(profile, mocks))

/*
 * To create a dependency from scratch in a new context
 * These should not be used in production
 */

@Deprecated(
	message = "Use context or injector"
)
@Suppress("SpreadOperator")
internal fun <T> Provider<T, Unit>.create(mocks: MockMap<Unit> = MockMap.empty()): T =
		this.create(Unit, mocks)

@Deprecated(
	message = "Use context or injector"
)
@Suppress("SpreadOperator")
internal fun <T, P> Provider<T, P>.create(profile: P, vararg mocks: MockProvider<*, P>): T =
		createInjector(profile, MockMap.of(*mocks)).invoke(this)

@Deprecated(
	message = "Use context or injector"
)
@Suppress("SpreadOperator")
internal fun <T, P> Provider<T, P>.create(profile: P, mocks: MockMap<P> = MockMap.empty()): T =
		createInjector(profile, mocks).invoke(this)
