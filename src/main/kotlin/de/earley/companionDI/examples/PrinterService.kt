package de.earley.companionDI.examples

import de.earley.companionDI.Dependency
import de.earley.companionDI.mockedBy
import de.earley.companionDI.mocksOf
import de.earley.companionDI.provide

enum class Profile {
	Prod, Test
}

interface DB {

	fun bar(): String

	companion object : Dependency<DB, Profile> by provide({ profile, _ ->
		when (profile) {
			Profile.Prod -> ProdDB()
			Profile.Test -> TestDB()
		}
	})

}

class ProdDB : DB {
	override fun bar(): String = "Prod"
}

class TestDB : DB {
	override fun bar(): String = "Test"
}

interface Service {

	fun foo(): String

	companion object : Dependency<Service, Profile> by provide({ _, injector ->
		ServiceA(injector.inject(DB))
	})

}

class ServiceA(
		private val db: DB
): Service {
	override fun foo(): String = "A: ${db.bar()}"
}


interface Printer {

	fun print()

	companion object : Dependency<Printer, Profile> by provide({ _, injector ->
		PrinterImpl(
				injector.inject(DB),
				injector.inject(Service)
		)
	})

}

class PrinterImpl(
		private val db: DB,
		private val service: Service
) : Printer {
	override fun print() {
		println("Using db " + db.bar())
		println(service.foo())
	}
}


fun main(args: Array<String>) {
	val printer = Printer.create(Profile.Prod)
	printer.print()

	// use prod for printer but mockedBy for service
	val testPrinter = PrinterImpl(
			DB.create(Profile.Prod),
			Service.create(Profile.Test)
	)
	testPrinter.print()

	// mocking
	val mockedDB = object : DB {
		override fun bar(): String = "Mocked"
	}

	val printerWithMock = Printer.create(Profile.Prod, mocksOf(DB mockedBy mockedDB))
	printerWithMock.print()
}