package de.earley.kompanionDI

import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import java.lang.ClassCastException

internal class KompanionTest : StringSpec() {

    init {

        "we can use the KompanionDI to store context" {

            val context = Context.create(null)
            KompanionDI.setupDI(context)

            KompanionDI.getInject<Nothing?, Unit>() shouldBe context
        }

    }

}
