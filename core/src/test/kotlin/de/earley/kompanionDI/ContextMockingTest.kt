package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.mock
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec


internal class ContextMockingTest : StringSpec() {

	// services

	interface Foo {
		val num: Int
	}

	class FooImpl : Foo {
		override val num: Int = 1
	}

	interface Bar {
		fun getANumber(): Int
	}

	class BarImpl(private val foo: Foo) : Bar {
		override fun getANumber() = foo.num
	}

	// di

	interface MyDI {
		val foo: Provider<Foo, Unit>
		val bar: Provider<Bar, Unit>
	}

	open class MyDIImpl : MyDI {
		override val foo: Provider<Foo, Unit> = { _, _ -> FooImpl() }
		override val bar: Provider<Bar, Unit> = { _, inject -> BarImpl(inject(foo)) }
	}

	init {

		"injection works as expected" {

			val ctx: Context<MyDI, Unit> = createContext(MyDIImpl())
			ctx.invoke { bar }.getANumber() shouldBe 1
		}

		val mock: Foo = object : Foo {
			override val num: Int = 2
		}

		"we can  mock the dependency to foo" {
			val di = MyDIImpl()
			val ctx: Context<MyDI, Unit> = createContext(di, di.foo.mock withValue mock)

			ctx.invoke { bar }.getANumber() shouldBe 2
		}

		"Or use a different DI implementation" {
			val di = object : MyDIImpl() {
				override val foo: Provider<Foo, Unit> = value(mock)
			}
			val ctx: Context<MyDI, Unit> = createContext(di)

			ctx.invoke { bar }.getANumber() shouldBe 2
		}

	}

}