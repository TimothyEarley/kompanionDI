package de.earley.kompanionDI


import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.singleton
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlin.concurrent.thread

internal class SingletonTest : StringSpec() {

	object Profile

	private class FooProvide {

		companion object : Provider<FooProvide, Profile> by { _, _ ->
			FooProvide.counter++
			FooProvide()
		} {
			var counter = 0
		}

	}

	@Suppress("MoveLambdaOutsideParentheses") // breaks code
	private class FooSingleton {

		companion object : Provider<FooSingleton, Profile> by singleton({ _, _ ->
			FooSingleton.counter++
			FooSingleton()
		}) {
			var counter = 0
		}

	}

	init {
		"If you choose the provide dependency multiple instances will be created" {
			val inject = Injector.create(Profile)
			FooProvide.counter = 0
			val one = inject(FooProvide)
			val two = inject(FooProvide)
			FooProvide.counter shouldBe 2
			(one === two) shouldBe false
		}

		"With singletons, only one instance is created" {
			val inject = Injector.create(Profile)
			FooSingleton.counter = 0
			val one = inject(FooSingleton)
			val two = inject(FooSingleton)
			FooSingleton.counter shouldBe 1
			(one === two) shouldBe true
		}

		"Crude test for thread safety" {

			val inject = Injector.create(Profile)
			FooSingleton.counter = 0
			val instance = inject(FooSingleton)
			(1..100).map {
				thread {
					inject(FooSingleton) shouldBe instance
				}
			}.forEach {
				it.join()
			}
			FooSingleton.counter shouldBe 1
		}
	}

}