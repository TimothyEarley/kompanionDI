package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.MockProvider

fun <T> Provider<T, Unit>.create(): T = Injector.create()(this)
fun <P, T> Provider<T, P>.create(p:P): T = Injector.create(p)(this)
fun <T> Provider<T, Unit>.create(vararg mocks: MockProvider<*, Unit>): T = Injector.create(MockMap.of(*mocks))(this)
fun <T> Provider<T, Unit>.create(mocks: MockMap<Unit>): T = Injector.create(mocks)(this)