package de.earley.companionDI

/**
 * Any interface/class which can be injected needs to have a companion object extending this interface.
 *
 * Profile (P) is intended to be an enum that is consistent throughout the whole app
 *
 * The mocks make having singletons provided more complicated, since a mock could change an object
 * further down the dependency hierarchy
 */
interface Dependency<out T, P> {
	/**
	 * Create a new instance of this dependency with the given profile using all the mocks available
	 */
	fun create(profile: P, mocks: MockMap<P> = MockMap.EMPTY): T
}

/**
 * Convenience method for dependencies without a profile
 */
fun <T> Dependency<T, Unit>.create(mocks: MockMap<Unit> = MockMap.EMPTY) = this.create(Unit, mocks)

/**
 * Utility class for creating dependency with a given profile and mocks
 */
class Injector<P>(
		private val profile: P,
		private val mocks: MockMap<P>
) {
	fun <T> inject(dependency: Dependency<T, P>): T = dependency.create(profile, mocks)
}

typealias Provider<T, P> = (P, Injector<P>) -> T

fun <T, P> Provider<T, P>.create(profile: P, mocks: MockMap<P>): T =
	this(profile, Injector(profile, mocks))



/**
 * Basic implementation satisfying the contract for de.earley.companionDI.create
 */
class MockableDependencyProvider<T, P>(
		private val clazz: Class<T>,
		private val provider: Provider<T, P>
) : Dependency<T, P> {
	override fun create(profile: P, mocks: MockMap<P>): T = mocks.get(clazz, profile) ?: provider.create(profile, mocks)

}

inline fun <reified T, P> provide(noinline provider: Provider<T, P>): Dependency<T, P> = MockableDependencyProvider(T::class.java, provider)