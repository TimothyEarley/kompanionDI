package de.earley.companionDI.examples

import de.earley.companionDI.Dependency
import de.earley.companionDI.Provider
import de.earley.companionDI.create
import de.earley.companionDI.mocking.mockedBy
import de.earley.companionDI.mocking.mocksOf
import de.earley.companionDI.provide

interface Heater {
	fun heat()

	companion object : Dependency<Heater, Unit> by provide({ _, _ ->
		ElectricHeater()
	})
}

class ElectricHeater : Heater {
	override fun heat() {
		println("Electric Heat")
	}
}

interface Pump {
	fun pump()

	companion object : Dependency<Pump, Unit> by provide({ _, inject ->
		Thermosiphon(inject(Heater))
	})
}

class Thermosiphon(
		private val heater: Heater
) : Pump {
	override fun pump() {
		heater.heat()
		println("Thermo pump")
	}
}

open class CoffeeMaker(
		private val heater: Heater,
		private val pump: Pump
) {

	open fun brew() {
		heater.heat()
		pump.pump()
		println("Coffee!")
	}

	companion object : Dependency<CoffeeMaker, Unit> by provide({ _, inject ->
		CoffeeMaker(
				inject(Heater),
				inject(Pump)
		)
	})

}


class CoffeeShop(
		val maker: CoffeeMaker
) {

	companion object : Dependency<CoffeeShop, Unit> by provide({ _, inject ->
		CoffeeShop(inject(CoffeeMaker))
	})

}

fun main(args: Array<String>) {
	CoffeeShop.create().maker.brew()


	println("\n\nMocking....")

	// mock the heater
	val mockedHeater = object : Heater {
		override fun heat() {
			println("Mocked heater")
		}
	}

	// mock the maker
	val mockedMaker: Provider<CoffeeMaker, Unit> = { _, inject ->
		object : CoffeeMaker(inject(Heater), inject(Pump)) {
			override fun brew() {
				println("Mock brew!")
				super.brew()
			}
		}
	}


	val mocks = mocksOf(
			Heater mockedBy mockedHeater,
			CoffeeMaker mockedBy mockedMaker
	)
	CoffeeShop.create(mocks).maker.brew()
}