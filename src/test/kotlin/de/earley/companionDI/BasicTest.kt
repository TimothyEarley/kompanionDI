package de.earley.companionDI

import de.earley.companionDI.mocking.MockMap
import io.kotlintest.matchers.beOfType
import io.kotlintest.*
import io.kotlintest.specs.StringSpec

internal class BasicTest : StringSpec() {

	private interface Foo {
		fun bar(): String

		companion object : Dependency<Foo, Unit> by provide({ _, _ -> FooWithCreate() })
	}

	private class FooWithCreate: Foo {

		override fun bar() = "FooWithCreate"

		companion object : Dependency<FooWithCreate, Unit> {
			override fun create(profile: Unit, mocks: MockMap<Unit>, mockable: Boolean): FooWithCreate {
				return FooWithCreate()
			}
		}
	}

	private class FooWithProvide : Foo {
		override fun bar() = "FooWithProvide"

		companion object : Dependency<FooWithProvide, Unit> by provide({ _, _ -> FooWithProvide() })

	}

	init {

		"A class with an companion object inheriting from Dependency can create an instance" {
			FooWithCreate.create().bar() shouldBe "FooWithCreate"
		}

		"Usually this is implemented in an interface" {
			val created = Foo.create()
			created should beOfType<FooWithCreate>()
			created.bar() shouldBe "FooWithCreate"
		}

		"The companion object should be implemented by a provide delegation" {
			FooWithProvide.create().bar() shouldBe "FooWithProvide"
		}

	}

}
