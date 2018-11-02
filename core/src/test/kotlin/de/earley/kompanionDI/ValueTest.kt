package de.earley.kompanionDI

import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

internal class ValueTest : StringSpec() {

    init {
        "values are injected" {
            val valueProvider = value<String, Unit>("value")
            valueProvider.create() shouldBe "value"
        }

        "lazy values are indeed lazy" {
            var ran = false
            val lazyVal = lazyValue<String, Unit> {
                ran = true
                "value"
            }
            ran shouldBe false
            lazyVal.create() shouldBe "value"
            ran shouldBe true
        }

        "lazy runs only once" {
            var count = 0
            val lazyVal = lazyValue<String, Unit> {
                count++
                "value"
            }
            count shouldBe 0
            repeat(10) {
                lazyVal.create() shouldBe "value"
            }
            count shouldBe 1
        }

    }

}
