package de.earley.kompanionDI.examples.injectorLegacy

import de.earley.kompanionDI.Injector
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock


interface Heater {
	fun heat()

	companion object : Provider<Heater, Unit> by { _, _ ->
		ElectricHeater()
	}
}

class ElectricHeater : Heater {
	override fun heat() {
		println("Electric Heat")
	}
}

interface Pump {
	fun pump()

	companion object : Provider<Pump, Unit> by { _, inject ->
		Thermosiphon(inject(Heater))
	}
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

	companion object : Provider<CoffeeMaker, Unit> by { _, inject ->
		CoffeeMaker(
			inject(Heater),
			inject(Pump)
		)
	}

}


class CoffeeShop(
		val maker: CoffeeMaker
) {

	companion object : Provider<CoffeeShop, Unit> by { _, inject ->
		CoffeeShop(inject(CoffeeMaker))
	}

}

fun main(args: Array<String>) {
	Injector.create()(CoffeeShop).maker.brew()

	println("\n\nMocking....")

	// Mock the heater
	val mockedHeater = object : Heater {
		override fun heat() {
			println("Mocked heater")
		}
	}

	// Mock the maker
	val mockedMaker: Provider<CoffeeMaker, Unit> = { _, inject ->
		object : CoffeeMaker(inject(Heater), inject(
			Pump
		)) {
			override fun brew() {
				println("Mock brew!")
				super.brew()
			}
		}
	}


	val mocks = MockMap.of(
			Heater.mock withValue mockedHeater,
			CoffeeMaker.mock with mockedMaker
	)
	Injector.create(mocks)(CoffeeShop).maker.brew()
}