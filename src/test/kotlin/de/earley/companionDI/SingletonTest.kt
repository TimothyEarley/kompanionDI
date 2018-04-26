package de.earley.companionDI


import de.earley.companionDI.mocking.mockedBy
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class SingletonTest : StringSpec() {

	private enum class Profile {
		TEST1, TEST2, TEST3_A, TEST3_B, TEST4
	}

	private class FooProvide {

		companion object : Provider<FooProvide, Profile> by { _, _ ->
			FooProvide.counter++
			FooProvide()
		} {
			var counter = 0
		}

	}

	private class FooSingleton {

		companion object : Provider<FooSingleton, Profile> by singleton({ _, _ ->
			FooSingleton.counter++
			FooSingleton()
		}) {
			var counter = 0
		}

	}

	private val dummy: Provider<Any, Profile> = {_, _ -> TODO() }

	init {

		val inject = createMutableInjector(Profile.TEST1)

		"If you choose the provide dependency multiple instances will be created" {
			inject.profile = Profile.TEST1
			FooProvide.counter = 0
			val one = inject(FooProvide)
			val two = inject(FooProvide)
			FooProvide.counter shouldBe 2
			(one === two) shouldBe false
		}

		"With singletons, only one instance is created" {
			inject.profile = Profile.TEST2
			FooSingleton.counter = 0
			val one = inject(FooSingleton)
			val two = inject(FooSingleton)
			FooSingleton.counter shouldBe 1
			(one === two) shouldBe true
		}

		"Singletons work with different profiles. Each profile gets its own instance" {
			FooSingleton.counter = 0
			inject.profile = Profile.TEST3_A
			val one = inject(FooSingleton)
			inject.profile = Profile.TEST3_B
			val two = inject(FooSingleton)
			FooSingleton.counter shouldBe 2
			(one === two) shouldBe false
		}

		"Even adding new mocks changes the instance since the mocks could change the desired structure" {
			inject.profile = Profile.TEST4
			FooSingleton.counter = 0
			val one = inject(FooSingleton)
			inject.mocks.add(dummy mockedBy dummy)
			val two = inject(FooSingleton)
			FooSingleton.counter shouldBe 2
			(one === two) shouldBe false
		}


	}

}