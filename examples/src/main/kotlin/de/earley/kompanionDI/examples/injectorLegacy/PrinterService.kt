package de.earley.kompanionDI.examples.injectorLegacy

import de.earley.kompanionDI.Injector
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock

object PrinterService {

	enum class Profile {
		Prod, Test
	}

	interface DB {

		fun bar(): String

		companion object : Provider<DB, Profile> by { profile, _ ->
			when (profile) {
				Profile.Prod -> ProdDB()
				Profile.Test -> TestDB()
			}
		}

	}

	class ProdDB : DB {
		override fun bar(): String = "Prod"
	}

	class TestDB : DB {
		override fun bar(): String = "Test"
	}

	interface Service {

		fun foo(): String

		companion object : Provider<Service, Profile> by { _, inject ->
			ServiceA(inject(DB))
		}

	}

	class ServiceA(
			private val db: DB
	) : Service {
		override fun foo(): String = "A: ${db.bar()}"
	}


	interface Printer {

		fun print()

		companion object : Provider<Printer, Profile> by { _, inject ->
			PrinterImpl(
				inject(DB),
				inject(Service)
			)
		}

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

	fun main() {
		val prodInject = Injector.create(Profile.Prod)
		val testInject = Injector.create(Profile.Test)

		val printer = prodInject(Printer)
		printer.print()

		// use prod for printer but mockedBy for service
		val testPrinter = PrinterImpl(
			prodInject(DB),
			testInject(Service)
		)
		testPrinter.print()

		// mocking
		val mockedDB = object : DB {
			override fun bar(): String = "Mocked"
		}

		val mockInject = Injector.create(Profile.Prod, MockMap.of(DB.mock withValue mockedDB))

		val printerWithMock = mockInject(Printer)
		printerWithMock.print()
	}

}

fun main(args: Array<String>) {
	PrinterService.main()
}