package de.earley.kompanionDI.examples

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.Injector
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock

typealias Profile = Unit // not using profiles here

object ReadmeContexts {
	// these classes could/should be hidden behind interfaces
	class Foo {
		fun getData() = 1
	}

	class Bar(private val foo: Foo) {
		fun printData() {
			println(foo.getData())
		}
	}

	// setup DI

	// this could also be a class containing environment specific configuration
	//	typealias Profile = Unit

	interface DI {
		val foo: Provider<Foo, Profile>
		val bar: Provider<Bar, Profile>
	}

	class BaseDI : DI {
		override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
		override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(foo)) }
	}

	// create the context
	val inject: Context<DI, Profile> = Context.create(BaseDI())

	// use it somewhere

	fun test() {
		inject { bar }.printData()
	}
}


object ReadmeMultiModule {
	class Foo {
		fun getData() = 1
	}

	class Bar(private val foo: Foo) {
		fun printData() {
			println(foo.getData())
		}
	}

// setup DI
//	typealias Profile = Unit

	interface ModuleA {
		val foo: Provider<Foo, Profile>
	}

	interface ModuleB {
		val bar: Provider<Bar, Profile>
	}

	// These are the available modules for manual injection, i.e. not everything
// managed by DI has to be publicly visible.
// In this case only module B can be manually injected
	interface DI : ModuleB

	class BaseModuleA() : ModuleA {
		override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
	}

	class BaseModuleB(private val moduleA: ModuleA): ModuleB {
		// here we use foo from module A
		override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(moduleA.foo)) }
	}

	class BaseDI(
		moduleA: ModuleA = BaseModuleA(),
		moduleB: ModuleB = BaseModuleB(moduleA)
	) : DI, ModuleB by moduleB

	// create the context
	val inject: Context<DI, Profile> = Context.create(BaseDI())

// use it somewhere

	fun test() {
		inject { bar }.printData()
	}
}

object ReadmeCompanion {
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
	val inject = Injector.create()

// use it

	fun test() {
		inject(Bar).printData()
	}
}


object ReadmeMocking {

	// open up Foo for mocking or use your favorite mocking framework
	open class Foo {
		open fun getData() = 1
	}

	class Bar(private val foo: Foo) {
		fun printData() {
			println(foo.getData())
		}
	}

	// setup DI

	// this could also be a class containing environment specific configuration
	//	typealias Profile = Unit

	interface DI {
		val foo: Provider<Foo, Profile>
		val bar: Provider<Bar, Profile>
	}

	class BaseDI : DI {
		override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
		override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(foo)) }
	}

	// create the context
	val di = BaseDI()
	val inject: Context<DI, Profile> = Context.create(di, MockMap.of(
		di.foo.mock withValue object : Foo() {
			override fun getData(): Int = 2
		}
	))

	// use it somewhere

	fun test() {
		inject { bar }.printData() // now prints "2"
	}


}