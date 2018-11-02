package de.earley.kompanionDI

import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.mocking.mocksOf
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class MockingSingleton : StringSpec() {

	private val singleton1: Provider<String, Unit> = singleton({ _, _ -> "1" })
	private val singleton2: Provider<String, Unit> = singleton({ _, _ -> "2" })

	init {

		"A singleton can be mocked without confusing the types" {

			val mocks = mocksOf(singleton2.mock withValue "3")

			singleton1.create(mocks) shouldBe "1"
			singleton2.create(mocks) shouldBe "3"

		}

	}

}