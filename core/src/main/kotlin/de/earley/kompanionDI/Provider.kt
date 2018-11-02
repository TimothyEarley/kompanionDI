package de.earley.kompanionDI

/**
 * A function capable of taking a profile and injector and returning a concrete instance of type T.
 */
// T has out variance
typealias Provider<T, P> = (P, Injector<P>) -> T