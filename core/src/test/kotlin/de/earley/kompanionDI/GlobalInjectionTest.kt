package de.earley.kompanionDI

import de.earley.kompanionDI.GlobalInjectionTest.App.inject
import de.earley.kompanionDI.mocking.*
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
			private val foo: Foo
	) : Service {
		override fun foo() = "Impl: " + foo.bar()
	}

	class Unmanaged {
		val service: Service = App.inject(ServiceDI)
	}

	// DI

	object FooDI : Provider<Foo, Profile> by { p, _ ->
		when (p) {
			Profile.PROD -> Foo()
			Profile.TEST -> throw IllegalArgumentException("Should be mocked")
		}
	}

	object ServiceDI : Provider<Service, Profile> by { _, injector ->
		ServiceImpl(injector(FooDI))
	}


	object App {
		val inject = createMutableInjector(Profile.PROD)
	}


	init {

		"You can inject a dependency from a non managed class" {

			Unmanaged().service.foo() shouldBe "Impl: Impl"

			inject.profile = Profile.TEST

			shouldThrow<IllegalArgumentException> { Unmanaged() }

			inject.mocks.add(FooDI.mock withValue object : Foo() {
				override fun bar() = "Mock"
			})

			Unmanaged().service.foo() shouldBe "Impl: Mock"


		}

	}

}
