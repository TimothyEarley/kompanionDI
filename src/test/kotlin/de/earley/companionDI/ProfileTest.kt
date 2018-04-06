package de.earley.companionDI

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class ProfilesTest : StringSpec() {

	private enum class ExampleProfile {
		PROD, TEST
	}

	private interface Foo {
		fun bar(): String

		companion object : Dependency<Foo, ExampleProfile> by provide({ profile, _ ->
			when (profile) {
				ProfilesTest.ExampleProfile.PROD -> FooProd()
				ProfilesTest.ExampleProfile.TEST -> FooTest()
			}
		})
	}

	private class FooProd : Foo {
		override fun bar() = "Prod"
	}


	private class FooTest: Foo {
		override fun bar() = "Test"
	}

	private class FooWithDependency(private val dependency: Foo) : Foo {
		override fun bar() = "Dependency: ${dependency.bar()}"

		companion object : Dependency<FooWithDependency, ExampleProfile> by provide({ _, inject ->
			FooWithDependency(inject(Foo))
		})
	}

	init {

		"A dependency can take a profile. Note: In one dependency tree only one Profile class can be used" {
			Foo.create(ExampleProfile.PROD).bar() shouldBe "Prod"
			Foo.create(ExampleProfile.TEST).bar() shouldBe "Test"
		}

		"The profile propagates through the tree" {
			FooWithDependency.create(ExampleProfile.PROD).bar() shouldBe "Dependency: Prod"
			FooWithDependency.create(ExampleProfile.TEST).bar() shouldBe "Dependency: Test"
		}

	}
}
