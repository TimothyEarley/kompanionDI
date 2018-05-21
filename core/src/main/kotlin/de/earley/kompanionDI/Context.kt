package de.earley.kompanionDI

interface Context<out DI, P> {
	operator fun <T> invoke(getProvider: DI.() -> Provider<T, P>): T
}


internal class ContextBackedByInjector<out DI, P>(
		private val di: DI,
		private val injector: Injector<P>
): Context<DI, P> {

	override operator fun <T> invoke(getProvider: DI.() -> Provider<T, P>): T = injector(di.getProvider())
}

// a terrible global singleton, but it allows nice initialisation of the DI context
object KompanionDI {

	private lateinit var ctx: Context<*, *>

	fun <DI, P> setupDI(context: Context<DI, P>) {
		ctx = context
	}

	// unsafe cast to not depend on DI
	fun <DI, P> getInject(): Context<DI, P> = ctx as Context<DI, P>

}