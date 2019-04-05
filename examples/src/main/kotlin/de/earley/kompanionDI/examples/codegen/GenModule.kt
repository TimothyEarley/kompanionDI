package de.earley.kompanionDI.examples.codegen

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.codegen.Module
import de.earley.kompanionDI.codegen.ProviderFor
import de.earley.kompanionDI.providers.value

object ModuleGenTest {

    interface ServiceA {
        fun a(): String
    }

    class ServiceAImpl : ServiceA {
        override fun a(): String = "a"
    }

    interface ServiceB {
        fun b(): String
    }

    class ServiceBImpl(private val a: ServiceA) : ServiceB {
        override fun b(): String = "b: " + a.a()
    }

    class ServiceBTest : ServiceB {
        override fun b(): String = "b test"
    }

    class App(private val b: ServiceB) {
        fun app() {
            println(b.b())
        }
    }

    @Module([
        ProviderFor(ServiceA::class, ServiceAImpl::class),
        ProviderFor(ServiceB::class, ServiceBImpl::class)
    ], profile = Unit::class)
    interface Services

    @Module([
        ProviderFor(App::class, App::class)
    ], profile = Unit::class)
    interface DI : Services

    @Module([
        ProviderFor(ServiceB::class, ServiceBTest::class)
    ], profile = Unit::class)
    interface TestDI : DI

//    class ManualTest : DIImpl() {
//        override val serviceB: Provider<ServiceB, Unit> = value(ServiceBTest())
//    }
}

fun main() {
//    test(Context.create(DIImpl()))
//    test(Context.create(TestDIImpl()))
//    test(Context.create(ModuleGenTest.ManualTest()))
}
//
//fun test(inject: Context<DISpec, Unit>) {
//    inject { app }.app()
//}