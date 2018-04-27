package de.earley.companionDI.mocking

import de.earley.companionDI.Provider
import de.earley.companionDI.bean

infix fun <T, T2 : T, P> Provider<T, P>.mockedBy(mock: Provider<T2, P>): MockProvider<T, P> =
		MockProvider(this.javaClass, mock)

infix fun <T, T2 : T, P> Provider<T, P>.mockedBy(mock: T2): MockProvider<T, P> =
		this mockedBy bean(mock)

fun <P> mocksOf(vararg mocks: MockProvider<*, P>): MockMap<P> = mutableMocksOf(*mocks)

fun <P> mutableMocksOf(vararg mocks: MockProvider<*, P>): MutableMockMap<P> = HashMockMap<P>().apply {
	mocks.forEach { add(it) }
}