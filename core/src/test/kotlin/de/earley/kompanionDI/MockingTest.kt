package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class MockingTest : StringSpec() {

	private interface Foo {
		fun bar(): String

		companion object : Provider<Foo, Unit> by { _, _ -> FooImpl() }
	}


	private class FooImpl : Foo {
		override fun bar() = "Impl"
	}

	private class OtherFooImpl : Foo {
		override fun bar() = "Other Impl"

		companion object : Provider<Foo, Unit> by { _, _ -> OtherFooImpl() }
	}

	private class FooWithDependency(
			private val dependency: Foo
	) : Foo {

		override fun bar() = "Dependency: ${dependency.bar()}"

		companion object : Provider<FooWithDependency, Unit> by { _, inject ->
			FooWithDependency(inject(Foo))
		}
	}

	private class FooTwoDependencies(
			private val one: Foo,
			private val two: Foo
	) : Foo {
		override fun bar() = "One: ${one.bar()}, Two: ${two.bar()}"

		companion object : Provider<FooTwoDependencies, Unit> by { _, inject ->
			FooTwoDependencies(inject(Foo), inject(Foo))
		}
	}

	init {

		"A dependency can have further dependencies" {
			FooWithDependency.create().bar() shouldBe "Dependency: Impl"

		}

		"This makes the dependency mockable" {
			FooWithDependency.create(Foo.mock with OtherFooImpl)
					.bar() shouldBe "Dependency: Other Impl"
		}

		"Or use an object" {
			FooWithDependency.create(
					Foo.mock withValue object : Foo {
						override fun bar() = "Mock"
					}
			).bar() shouldBe "Dependency: Mock"
		}

		"You can also create a new instance per Mock" {
			var count = 0
			FooTwoDependencies.create(
					Foo.mock with { _, _ ->
						count++
						val now = count
						object : Foo {
							override fun bar() = "$now"
						}
					}
			).bar() shouldBe "One: 1, Two: 2"
		}

	}

}