package de.earley.companionDI

import de.earley.companionDI.mocking.mockedBy
import de.earley.companionDI.mocking.mocksOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class MockingSingleton : StringSpec() {

	private val singleton1: Provider<String, Unit> = singleton({ _, _ -> "1" })
	private val singleton2: Provider<String, Unit> = singleton({ _, _ -> "2" })

	init {

		"A singleton can be mocked without confusing the types" {

			val mocks = mocksOf(singleton2 mockedBy "3")

			singleton1.create(mocks) shouldBe "1"
			singleton2.create(mocks) shouldBe "3"

		}

	}

}