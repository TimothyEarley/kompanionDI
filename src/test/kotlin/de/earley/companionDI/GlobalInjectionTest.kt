package de.earley.companionDI

import de.earley.companionDI.GlobalInjectionTest.App.ctx
import de.earley.companionDI.global.MutableContext
import de.earley.companionDI.mocking.*
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class GlobalInjectionTest : StringSpec() {

	interface Service {
		fun foo(): String

		companion object : Dependency<Service, Unit> by provide({ _, _ ->
			ServiceImpl()
		})
	}

	class ServiceImpl : Service {
		override fun foo() = "Impl"
	}

	class ServiceTest : Service {
		override fun foo() = "Test"
	}

	class Unmanaged {

		val service: Service = ctx.inject(Service)

	}


	object App {
		val ctx = MutableContext(Unit, mutableMocksOf())
	}


	init {

		"You can inject a dependency from a non managed class" {

			Unmanaged().service.foo() shouldBe "Impl"

			ctx.mocks.add(Service mockedBy ServiceTest())

			Unmanaged().service.foo() shouldBe "Test"


		}

	}

}
