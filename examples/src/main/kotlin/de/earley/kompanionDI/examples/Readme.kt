package de.earley.kompanionDI.examples

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.createContext
import de.earley.kompanionDI.createInjector

typealias Profile = Unit // not using profiles here

object ReadmeContexts {

	class Foo {
		fun getData() = 1
	}

	class Bar(private val foo: Foo) {
		fun printData() {
			println(foo.getData())
		}
	}

	interface DI {
		val foo: Provider<Foo, Profile>
		val bar: Provider<Bar, Profile>
	}

	class BaseDI : DI {
		override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
		override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(foo)) }
	}

	// create the context
	val inject: Context<DI, Profile> = createContext(BaseDI())

	// use it somewhere

	fun test() {
		inject { bar }.printData()
	}
}

object ReadmeCompanions {

	interface Foo {
		fun getData(): Int

		companion object : Provider<Foo, Profile> by { _, _ -> FooImpl() }
	}

	interface Bar {
		fun printData()

		companion object : Provider<Bar, Profile> by { _, inject -> BarImpl(inject(Foo)) }
	}

	class FooImpl : Foo {
		override fun getData() = 1
	}

	class BarImpl(private val foo: Foo) : Bar {
		override fun printData() {
			println(foo.getData())
		}
	}

	// create an injector
	val inject = createInjector()

	// use it

	fun test() {
		inject(Bar).printData()
	}

}

