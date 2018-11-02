package de.earley.kompanionDI.examples.context

import de.earley.kompanionDI.*

object InterfaceDI {

	fun String.times(n: Int) = generateSequence { this }
			.take(n)
			.fold("", String::plus)

	open class Instrumented(vararg instrumented: Instrumented) {
		private val fields = instrumented

		fun foo(indent: Int = 0) {
			println("\t".times(indent) + this.javaClass.simpleName)
			fields.forEach { it.foo(indent + 1) }
		}
	}

	// Usage

	open class DepA : Instrumented()

	class DepB(a: DepA) : Instrumented(a)

	class App(b: DepB) : Instrumented(b)

	class Unmanaged {

		init {
			inject { app }.foo()
		}

	}

	// Setup

	interface MyDI {
		val a: Provider<DepA, Unit>
		val b: Provider<DepB, Unit>
		val app: Provider<App, Unit>
	}

	abstract class MyDIBase : MyDI {
		override val a: Provider<DepA, Unit> = { _, _ -> DepA() }
		override val b: Provider<DepB, Unit> = { _, inject ->
			DepB(inject(a))
		}
		override val app: Provider<App, Unit> = { _, inject ->
			App(inject(b))
		}
	}

	class TestA : DepA()

	object MyDIProd : MyDIBase()

	object MyDITest : MyDIBase() {

		override val a: Provider<DepA, Unit> = { _, _ -> TestA() }

	}

	val inject: Context<MyDI, Unit>
		get() = KompanionDI.getInject()

	fun main() {

		// switch global profile here
		val ctx: Context<MyDI, Unit> = Context.create(MyDIProd)

		ctx { app }.foo()

		val testCtx = Context.create(MyDITest)

		testCtx { this.app }.foo()


		KompanionDI.setupDI(Context.create(MyDIProd))
		Unmanaged()

	}

}




fun main(args: Array<String>) {
	InterfaceDI.main()
}