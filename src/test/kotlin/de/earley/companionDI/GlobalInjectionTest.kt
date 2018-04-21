package de.earley.companionDI

import de.earley.companionDI.GlobalInjectionTest.App.inject
import de.earley.companionDI.mocking.*
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec

internal class GlobalInjectionTest : StringSpec() {

	enum class Profile {
		PROD, TEST
	}

	open class Foo {
		open fun bar(): String = "Impl"
	}

	interface Service {
		fun foo(): String
	}

	class ServiceImpl(
			val foo: Foo
	) : Service {
		override fun foo() = "Impl: " + foo.bar()
	}

	class Unmanaged {
		val service: Service = App.inject(ServiceDI)
	}

	// DI

	object FooDI : Dependency<Foo, Profile> by provide({ p, _ ->
		when (p) {
			Profile.PROD -> Foo()
			Profile.TEST -> throw IllegalArgumentException("Should be mocked")
		}
	})

	object ServiceDI : Dependency<Service, Profile> by provide({ _, injector ->
		ServiceImpl(injector(FooDI))
	})


	object App {
		val inject = MutableInjector(Profile.PROD, mutableMocksOf())
	}


	init {

		"You can inject a dependency from a non managed class" {

			Unmanaged().service.foo() shouldBe "Impl: Impl"

			inject.profile = Profile.TEST

			shouldThrow<IllegalArgumentException> { Unmanaged() }

			inject.mocks.add(FooDI mockCreateBy bean(object : Foo() {
				override fun bar() = "Mock"
			}))

			Unmanaged().service.foo() shouldBe "Impl: Mock"


		}

	}

}
