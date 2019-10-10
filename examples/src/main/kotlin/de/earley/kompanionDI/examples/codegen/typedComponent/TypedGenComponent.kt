package de.earley.kompanionDI.examples.codegen.typedComponent

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.KompanionDI
import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.codegen.ComponentModule
import de.earley.kompanionDI.codegen.TypedComponent
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.singleton


interface ServiceA {
    fun a(): String
}

@TypedComponent(ServicesDI::class)
class ServiceAImpl : ServiceA {
    override fun a(): String = "a"
}

@TypedComponent(TestServicesDI::class)
class TestService : ServiceA {
    override fun a(): String = "testing"
}

interface ServiceB {
    fun b(): String
}
//TODO repeated annotations allow us to specify that a class should be injected in both prod and test
@TypedComponent(ServicesDI::class)
class ServiceBImpl(private val a: ServiceA) : ServiceB {
    override fun b(): String = "b: " + a.a()
}
@TypedComponent(TestServicesDI::class)
class ServiceBTest(private val a: ServiceA) : ServiceB {
    override fun b(): String = "b: " + a.a()
}

//TODO add provider annotation to inject constructed (e.g. for retrofit)
//@Provider(ServicesDI::class) fun provide(a: ServiceA): ServiceB = TODO()

@TypedComponent(AppDI::class)
class App(private val b: ServiceB) {
    fun app() {
        println(b.b())
    }
}

@ComponentModule
object ServicesDI

@ComponentModule([ServicesDI::class])
object AppDI

@ComponentModule(inheritFrom = ServicesDI::class)
object TestServicesDI

fun main() {

    val di = AppDIImpl()
    KompanionDI.setupDI(Context.create(di))
    val inject = KompanionDI.getInject<AppDISpec, Unit>()
    inject { app }.app()

    (Context.create(AppDIImpl(TestServicesDIImpl()))) { app }.app()

    Context.create(di, MockMap.of(di.serviceA.mock with singleton { _, _ -> object : ServiceA {
        override fun a(): String = "MOCK"
    }})).invoke { app }.app()

}