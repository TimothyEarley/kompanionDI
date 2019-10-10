package de.earley.kompanionDI.examples.codegen

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.codegen.Component
import de.earley.test.DIImpl


object ComponentGenTest {

    interface ServiceA {
        fun a(): String
    }

    @Component("Services")
    class ServiceAImpl : ServiceA {
        override fun a(): String = "a"
    }

    interface ServiceB {
        fun b(): String
    }

    @Component("Services")
    class ServiceBImpl(private val a: ServiceA) : ServiceB {
        override fun b(): String = "b: " + a.a()
    }

    class ServiceBTest : ServiceB {
        override fun b(): String = "b test"
    }

    @Component
    class App(private val b: ServiceB) {
        fun app() {
            println(b.b())
        }
    }

    class TestDI : DIImpl() {
        override val serviceB: Provider<ServiceB, Unit> = { _, _ -> ServiceBTest() }
    }


}

fun main() {

    (Context.create(DIImpl())) { app }.app()
    (Context.create(ComponentGenTest.TestDI())) { app }.app()

}