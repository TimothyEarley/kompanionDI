package de.earley.kompanionDI

import io.kotlintest.matchers.beOfType
import io.kotlintest.should
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class BasicTest : StringSpec() {

	private interface Foo {
		fun bar(): String

		companion object : Provider<Foo, Unit> by { _, _ -> FooImpl() }
	}

	private class FooImpl : Foo {
		override fun bar() = "FooImpl"

		companion object : Provider<Foo, Unit> by { _, _ -> FooImpl() }

	}

	init {

		"A class with an companion object inheriting from Provider can create an instance" {
			FooImpl.create().bar() shouldBe "FooImpl"
		}

		"Usually this is implemented in an interface" {
			val created = Foo.create()
			created should beOfType<FooImpl>()
			created.bar() shouldBe "FooImpl"
		}

	}

}
