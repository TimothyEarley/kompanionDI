package de.earley.companionDI.examples

import de.earley.companionDI.mocking.HashMockMap
import de.earley.companionDI.mocking.mockedBy
import de.earley.companionDI.Provider
import de.earley.companionDI.bean
import de.earley.companionDI.create

enum class UserProfile {
	Steve, Peter
}

data class User(val name: String)

class App(
		private val user: User,
		private val sendService: SendService
) {

	fun start() {
		sendService.send(user, "Hello ${user.name}!")
	}

}

interface SendService {
	fun send(to: User, msg: String)
}

abstract class FormattedSendService (
		private val formatter: Formatter
) : SendService {
	override fun send(to: User, msg: String) {
		send(formatter.format(to, msg))
	}

	abstract fun send(formatted: String)
}

class PrintSendService(formatter: Formatter) : FormattedSendService(formatter) {
	override fun send(formatted: String) {
		println("Printing $formatted")
	}
}

class EmailSendService(formatter: Formatter) : FormattedSendService(formatter) {
	override fun send(formatted: String) {
		println("Emailing $formatted")
	}
}

interface Formatter {
	fun format(to: User, msg: String): String
}

object RealFormatter: Formatter {
	override fun format(to: User, msg: String): String = "To: ${to.name}, msg: $msg"
}

object TestFormatter : Formatter {
	override fun format(to: User, msg: String): String = "$to, $msg"
}

// ------------------- dependency setup ------------------

// these could all be static extension functions, if such a thing existed (and HKT)

val formatterBean: Provider<Formatter, UserProfile> = bean(RealFormatter)

val user: Provider<User, UserProfile> = { profile, _ -> when(profile) {
	UserProfile.Steve -> User("Steve")
	UserProfile.Peter -> User("Peter")
}}

val sendService: Provider<SendService, UserProfile> = { _, inject ->
	EmailSendService(inject(formatterBean))
}

val app: Provider<App, UserProfile> = { _, inject ->
	App(inject(user), inject(sendService))
}

// calling app.create(UserProfile.Steve) is the same as calling
// App(User("Steve"), EmailSendService(RealFormatter))

// test dep
val printSendService: Provider<PrintSendService, UserProfile> = { _, inject ->
	PrintSendService(inject(formatterBean))
}

fun main(args: Array<String>) {

	listOf(
			// profiles
			app.create(UserProfile.Steve),
			app.create(UserProfile.Peter),
			// mock by other Dependency
			app.create(UserProfile.Steve, sendService mockedBy printSendService),
			// mock any class by bean
			app.create(UserProfile.Steve, formatterBean mockedBy TestFormatter),
			// mock by bean
			app.create(UserProfile.Steve, user mockedBy User("This is not Steve!")),
			// combine all of them
			app.create(UserProfile.Steve,
					sendService mockedBy printSendService,
					formatterBean mockedBy TestFormatter,
					user mockedBy User("This is not Steve!")
			)
	).forEach(App::start)

	// dynamic example
	val mocks = HashMockMap<UserProfile>()
	if (Math.random() > 0.5) mocks.add(user mockedBy User("Random user!!!"))
	app.create(UserProfile.Steve, mocks).start()

}