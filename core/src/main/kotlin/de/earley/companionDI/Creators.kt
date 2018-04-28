package de.earley.companionDI

import de.earley.companionDI.mocking.*

fun <P> createInjector(profile: P, vararg mocks: MockProvider<*, P>): Injector<P> = createInjector(profile, mocksOf(*mocks))
fun <P> createInjector(profile: P, mocks: MockMap<P> = MockMap.empty()): Injector<P> = InjectorImpl(profile, mocks)

fun <P> createMutableInjector(profile: P, vararg mocks: MockProvider<*, P>): MutableInjector<P> = createMutableInjector(profile, mutableMocksOf(*mocks))
fun <P> createMutableInjector(profile: P, mocks: MutableMockMap<P> = HashMockMap()): MutableInjector<P> = MutableInjectorImpl(profile, mocks)


/*
 * To create a dependency from scratch in a new context
 * These should not be used in production
 */

fun <T> Provider<T, Unit>.create(mocks: MockMap<Unit> = MockMap.empty()): T =
		this.create(Unit, mocks)

fun <T, P> Provider<T, P>.create(profile: P, vararg mocks: MockProvider<*, P>): T =
		createInjector(profile, *mocks).invoke(this)

fun <T, P> Provider<T, P>.create(profile: P, mocks: MockMap<P> = MockMap.empty()): T =
		createInjector(profile, mocks).invoke(this)