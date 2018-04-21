package de.earley.companionDI

import de.earley.companionDI.mocking.beanBy
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class SingletonTest : StringSpec() {

	private enum class Profile {
		TEST1, TEST2, TEST3_A, TEST3_B, TEST4
	}

	private class FooProvide {

		companion object : Dependency<FooProvide, Profile> by provide({ _, _ ->
			FooProvide.counter++
			FooProvide()
		}) {
			var counter = 0
		}

	}

	private class FooSingleton {

		companion object : Dependency<FooSingleton, Profile> by singleton({ _, _ ->
			FooSingleton.counter++
			FooSingleton()
		}) {
			var counter = 0
		}

	}

	init {

		"If you choose the provide dependency mutliple instances will be created" {
			FooProvide.counter = 0
			val one = FooProvide.create(Profile.TEST1)
			val two = FooProvide.create(Profile.TEST1)
			FooProvide.counter shouldBe 2
			(one === two) shouldBe false
		}

		"With singletons, only one instance is created" {
			FooSingleton.counter = 0
			val one = FooSingleton.create(Profile.TEST2)
			val two = FooSingleton.create(Profile.TEST2)
			FooSingleton.counter shouldBe 1
			(one === two) shouldBe true
		}

		"Singletons work with different profiles. Each profile gets its own instance" {
			FooSingleton.counter = 0
			val one = FooSingleton.create(Profile.TEST3_A)
			val two = FooSingleton.create(Profile.TEST3_B)
			FooSingleton.counter shouldBe 2
			(one === two) shouldBe false
		}

		"Even adding new mocks changes the instance since the mocks could change the desired structure" {
			FooSingleton.counter = 0
			val one = FooSingleton.create(Profile.TEST4)
			val two = FooSingleton.create(Profile.TEST4, String::class.java beanBy "Mock value")
			FooSingleton.counter shouldBe 2
			(one === two) shouldBe false
		}


	}

}