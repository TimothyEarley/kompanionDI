package de.earley.kompanionDI.examples.injector

import de.earley.kompanionDI.Provider
import de.earley.kompanionDI.createMutableInjector
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.singleton

object Android {

	interface Activity {
		fun show(msg: String)
	}

	// unmanaged
	class ActivityImpl : Activity {

		private val presenter: Presenter =
			App.inject(DI.presenter)

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



	object DI {

		val presenter: Provider<Presenter, Profile> = { _, inject ->
			PresenterImpl(inject(interactor))
		}

		val interactor: Provider<Interactor, Profile> = singleton { p, _ ->
			object : Interactor {
				override fun getData() = p.name
			}
		}
	}

// App

	object App {

		val inject = createMutableInjector(Profile.PROD)

	}
}

fun main(args: Array<String>) {

	fun run() {
		Android.ActivityImpl().doStuff()

	}

	run()

	// test
	Android.App.inject.profile =
			Android.Profile.TEST
	run()

	// Mock
	Android.App.inject.mocks.add(
			Android.DI.interactor.mock withValue object :
				Android.Interactor {
				override fun getData(): String = "MOCK"
			}
	)
	run()

}