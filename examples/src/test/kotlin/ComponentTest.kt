import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import de.earley.kompanionDI.codegen.TypedComponentProcessor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ComponentTest {

    @Test
    fun `kapt test`() {

        val source = SourceFile.kotlin("Test.kt", """
            package test
            import de.earley.kompanionDI.codegen.*

            interface IFoo
            @Component(ServiceDI::class) class Foo : IFoo
            @Component(TestServiceDI::class) class TestFoo : IFoo
            
            @Component(AppDI::class) class Bar
            
            interface Baz
            @Provide(AppDI::class) fun provide(foo: IFoo): Baz = object : Baz {}

            @Module object ServiceDI
            @Module([ServiceDI::class]) object AppDI
            @Module(extend = ServiceDI::class) object TestServiceDI
            """.trimIndent())

        val result = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            annotationProcessors = listOf(TypedComponentProcessor())
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

    }

}