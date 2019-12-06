package de.earley.kompanionDI.examples.codegen.typedComponent

import de.earley.kompanionDI.Context
import de.earley.kompanionDI.KompanionDI
import de.earley.kompanionDI.codegen.Module
import de.earley.kompanionDI.codegen.Component
import de.earley.kompanionDI.codegen.Provide
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.singleton


interface ServiceA {
    fun a(): String
}

@Component(ServicesDI::class)
class ServiceAImpl : ServiceA {
    override fun a(): String = "prod a"
}

@Component(TestServicesDI::class)
class TestService : ServiceA {
    override fun a(): String = "test a"
}

interface ServiceB {
    fun b(): String
}

@Component(ServicesDI::class)
class ServiceBImpl(private val a: ServiceA) : ServiceB {
    override fun b(): String = "b: " + a.a()
}

interface HttpService {
    fun get(): String
}

@Provide(ServicesDI::class) fun provide(a: ServiceA): HttpService = object : HttpService {
    override fun get(): String = "http <${a.a()}>"
}

@Component(AppDI::class)
class App(
        private val b: ServiceB,
        private val httpService: HttpService
) {
    fun app() {
        println(b.b() + "; " + httpService.get())
    }
}

@Module
object ServicesDI

@Module([ServicesDI::class])
object AppDI

@Module(extend = ServicesDI::class)
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