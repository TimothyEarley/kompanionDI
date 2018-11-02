package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.*

/**
 * Helper methods for creating [Injector] and [Context] instances
 */

fun createInjector(): Injector<Unit> = createInjector(Unit)
fun createInjector(mocks: MockMap<Unit>): Injector<Unit> = createInjector(Unit, mocks)
fun <P> createInjector(profile: P, vararg mocks: MockProvider<*, P>): Injector<P> = createInjector(profile, mocksOf(*mocks))
fun <P> createInjector(profile: P, mocks: MockMap<P> = MockMap.empty()): Injector<P> = InjectorImpl(profile, mocks)

fun <P> createMutableInjector(profile: P, vararg mocks: MockProvider<*, P>): MutableInjector<P> = createMutableInjector(profile, mutableMocksOf(*mocks))
fun <P> createMutableInjector(profile: P, mocks: MutableMockMap<P> = HashMockMap()): MutableInjector<P> = MutableInjectorImpl(profile, mocks)

fun <DI, P> createContext(di: DI, profile: P, vararg mocks: MockProvider<*, P>): Context<DI, P> = createContext(di, profile, mocksOf(*mocks))
fun <DI> createContext(di: DI, vararg mocks: MockProvider<*, Unit>): Context<DI, Unit> = createContext(di, Unit, mocksOf(*mocks))
fun <DI> createContext(di: DI, mocks: MockMap<Unit> = MockMap.empty()): Context<DI, Unit> = createContext(di, Unit, mocks)
fun <DI, P> createContext(di: DI, profile: P, mocks: MockMap<P> = MockMap.empty()): Context<DI, P> = ContextBackedByInjector(di, createInjector(profile, mocks))

/*
 * To create a dependency from scratch in a new context
 * These should not be used in production
 */

internal fun <T> Provider<T, Unit>.create(mocks: MockMap<Unit> = MockMap.empty()): T =
		this.create(Unit, mocks)

internal fun <T, P> Provider<T, P>.create(profile: P, vararg mocks: MockProvider<*, P>): T =
		createInjector(profile, *mocks).invoke(this)

internal fun <T, P> Provider<T, P>.create(profile: P, mocks: MockMap<P> = MockMap.empty()): T =
		createInjector(profile, mocks).invoke(this)