package de.earley.kompanionDI

/**
 * Ability to get an instance from a [Provider].
 */
interface Context<out DI, P> {

	/**
	 * Instantiate an object from the provider returned by [[getProvider]]
	 */
	operator fun <T> invoke(getProvider: DI.() -> Provider<T, P>): T
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

/**
 * Global store for a [Context].
 * Use this when you want to decouple the context usage from its setup.
 */
object KompanionDI {

	private lateinit var ctx: Context<*, *>

	/**
	 * Set the global [Context]
	 * WARNING: can only be called once
	 */
	fun <DI, P> setupDI(context: Context<DI, P>) {
		ctx = context
	}

	// unsafe cast to not depend on DI
	/**
	 * Returns the global context as set by [KompanionDI.setupDI].
	 * NOTE: make sure to use the same DI and P generic parameters as in the setupDI method,
	 * otherwise a cast exception can be thrown
	 */
	@Suppress("UNCHECKED_CAST")
	fun <DI, P> getInject(): Context<DI, P> = ctx as Context<DI, P>

}