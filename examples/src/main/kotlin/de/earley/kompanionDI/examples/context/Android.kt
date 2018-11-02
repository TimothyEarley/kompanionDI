package de.earley.kompanionDI.examples.context

import de.earley.kompanionDI.*
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.singleton

object Android {

	interface Activity {
		fun show(msg: String)
	}

	// unmanaged
	class ActivityImpl : Activity {

		private val presenter: Presenter =
			App.inject { presenter }

		fun doStuff() {
			presenter.doStuff(this)
		}

		override fun show(msg: String) {
			println(msg)
		}

	}

	interface Presenter {
		fun doStuff(activity: Activity)
	}

	class PresenterImpl(
			private val interactor: Interactor
	) : Presenter {
		override fun doStuff(activity: Activity) {
			activity.show(interactor.getData())
		}

	}

	interface Interactor {
		fun getData(): String
	}


// DI

	enum class Profile {
		PROD, TEST
	}

	interface DI {
		val presenter: Provider<Presenter, Profile>
		val interactor: Provider<Interactor, Profile>
	}

	class BaseDI() : DI {
		override val presenter: Provider<Presenter, Profile> = { _, inject ->
			PresenterImpl(inject(interactor))
		}

		override val interactor: Provider<Interactor, Profile> = singleton { p, _ ->
			object : Interactor {
				override fun getData() = p.name
			}
		}
	}

// App

	object App {
		// var for test purposes
		var inject: Context<DI, Profile> = Context.create(BaseDI(), Profile.PROD)
	}
}

fun main(args: Array<String>) {

	fun run() {
		Android.ActivityImpl().doStuff()
	}

	run()

	// test
	Android.App.inject = Context.create(Android.BaseDI(), Android.Profile.TEST)
	run()

	// Mock
	val di = Android.BaseDI()
	Android.App.inject = Context.create(
		di,
		Android.Profile.TEST,
		MockMap.of(
				di.interactor.mock withValue object : Android.Interactor {
					override fun getData(): String = "MOCK"
				}
		)
	)
	run()

}