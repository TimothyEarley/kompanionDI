@file:Suppress("unused")

package de.earley.kompanionDI.examples.context

import de.earley.kompanionDI.*
import de.earley.kompanionDI.mocking.mock
import java.lang.reflect.Proxy

object MultiInterfaceDI {

// Interfaces

	interface HasMessage {
		val msg: String
	}

	interface RestBase {
		fun <T: HasMessage> create(clazz: Class<T>): T
	}
	interface RestServerA : HasMessage
	interface RestServerB : HasMessage
	interface ServiceA : HasMessage
	interface ServiceB : HasMessage

// Impl

	class ServiceAImpl(
			restServerA: RestServerA
	) : ServiceA {
		override val msg = "A: (${restServerA.msg})"
	}

	class ServiceBImpl(
		serviceA: ServiceA,
		restServerB: RestServerB
	): ServiceB {
		override val msg = "B: (${serviceA.msg}: ${restServerB.msg})"
	}

// DI

	interface NetworkDI {
		val restBase: Provider<RestBase, Unit>
		val restServerA: Provider<RestServerA, Unit>
		val restServerB: Provider<RestServerB, Unit>
	}

	interface ServiceDI {
		val serviceA: Provider<ServiceA, Unit>
		val serviceB: Provider<ServiceB, Unit>
	}

	open class BaseNetworkDI: NetworkDI {
		@Suppress("UNCHECKED_CAST")
		override val restBase: Provider<RestBase, Unit> = lazyValue { object : RestBase {
			override fun <T: HasMessage> create(clazz: Class<T>): T = Proxy.newProxyInstance(
					clazz.classLoader,
					arrayOf(clazz)
			) { _, method, _ -> method.name + " by proxy" } as T
		}}

		override val restServerA: Provider<RestServerA, Unit> = { _, inject -> inject(restBase)
			.create(RestServerA::class.java)
		}

		override val restServerB: Provider<RestServerB, Unit> = { _, inject -> inject(restBase)
			.create(RestServerB::class.java)
		}
	}

	open class BaseServiceDI(
			networkDI: NetworkDI
	) : ServiceDI, NetworkDI by networkDI {
		override val serviceA: Provider<ServiceA, Unit> = singleton { _, inject ->
			ServiceAImpl(inject(restServerA))
		}
		override val serviceB: Provider<ServiceB, Unit> = singleton { _, inject ->
			ServiceBImpl(
				inject(serviceA),
				inject(restServerB)
			)
		}
	}

	// pulling it all together

	open class BaseDI(
		networkDI: NetworkDI = BaseNetworkDI(),
		serviceDI: ServiceDI = BaseServiceDI(
			networkDI
		)
	) : NetworkDI by networkDI, ServiceDI by serviceDI

	val inject: Context<ServiceDI, Unit>
		get() = KompanionDI.getInject()

	// app
	class Unmanaged {
		private val s = inject { serviceB }
		fun foo() = println(s.msg)
	}

	// test
	class TestNetworkDI : BaseNetworkDI() {
		override val restServerA: Provider<RestServerA, Unit> = value(object :
			RestServerA {
			override val msg: String = "Test A"
		})
		override val restServerB: Provider<RestServerB, Unit> = value(object :
			RestServerB {
			override val msg: String = "Test B"
		})
	}

	fun main() {
		val ctx = createContext(BaseDI())
		KompanionDI.setupDI(ctx)


		// Usage
		Unmanaged().foo()
	}

	fun test() {

		val di = BaseDI(networkDI = TestNetworkDI())

		val mock = object : ServiceA {
			override val msg: String = "Mocked A!"
		}

		val ctx = createContext(di, di.serviceA.mock withValue mock)
		KompanionDI.setupDI(ctx)

		Unmanaged().foo()

	}

}

fun main(args: Array<String>) {
	MultiInterfaceDI.main()
	MultiInterfaceDI.test()
}