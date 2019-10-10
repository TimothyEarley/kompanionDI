import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import de.earley.kompanionDI.codegen.TypedComponentProcessor
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TypedComponentTest {

    @Test
    fun `kapt test`() {

        val source = SourceFile.kotlin("Test.kt", """
            package test
            import de.earley.kompanionDI.codegen.ComponentModule
            import de.earley.kompanionDI.codegen.TypedComponent

            interface IFoo
            @TypedComponent(ServiceDI::class) class Foo : IFoo
            @TypedComponent(TestServiceDI::class) class TestFoo : IFoo
            @TypedComponent(AppDI::class) class Bar
            @ComponentModule object ServiceDI
            @ComponentModule([ServiceDI::class]) object AppDI
            @ComponentModule(inheritFrom = ServiceDI::class) object TestServiceDI
        """.trimIndent())

        val result = KotlinCompilation().apply {
            sources = listOf(source)
            inheritClassPath = true
            annotationProcessors = listOf(TypedComponentProcessor())
        }.compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)

    }

}