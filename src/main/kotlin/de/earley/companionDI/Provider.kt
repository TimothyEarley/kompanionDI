package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap

typealias Provider<T, P> = (P, Injector<P>) -> T

internal fun <T, P> Provider<T, P>.create(profile: P, mocks: MockMap<P>): T =
		this(profile, Injector(profile, mocks))

