package de.earley.kompanionDI.examples.codegen.typedComponent

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.KompanionDI
import de.earley.kompanionDI.codegen.ComponentModule
import de.earley.kompanionDI.codegen.TypedComponent
import de.earley.kompanionDI.codegen.TypedProvide
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.singleton


interface ServiceA {
    fun a(): String
}

@TypedComponent(ServicesDI::class)
class ServiceAImpl : ServiceA {
    override fun a(): String = "prod a"
}

@TypedComponent(TestServicesDI::class)
class TestService : ServiceA {
    override fun a(): String = "test a"
}

interface ServiceB {
    fun b(): String
}

@TypedComponent(ServicesDI::class)
class ServiceBImpl(private val a: ServiceA) : ServiceB {
    override fun b(): String = "b: " + a.a()
}

interface HttpService {
    fun get(): String
}

@TypedProvide(ServicesDI::class) fun provide(a: ServiceA): HttpService = object : HttpService {
    override fun get(): String = "http <${a.a()}>"
}

@TypedComponent(AppDI::class)
class App(
        private val b: ServiceB,
        private val httpService: HttpService
) {
    fun app() {
        println(b.b() + "; " + httpService.get())
    }
}

@ComponentModule
object ServicesDI

@ComponentModule([ServicesDI::class])
object AppDI

@ComponentModule(extend = ServicesDI::class)
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