package de.earley.kompanionDI.examples.injectorLegacy

import de.earley.kompanionDI.*
import de.earley.kompanionDI.mocking.MockMap
import de.earley.kompanionDI.mocking.mock
import de.earley.kompanionDI.providers.value

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

val formatterValue: Provider<Formatter, UserProfile> = value(RealFormatter)

val user: Provider<User, UserProfile> = { profile, _ -> when(profile) {
	UserProfile.Steve -> User("Steve")
	UserProfile.Peter -> User("Peter")
}}

val sendService: Provider<SendService, UserProfile> = { _, inject ->
	EmailSendService(inject(formatterValue))
}

val app: Provider<App, UserProfile> = { _, inject ->
	App(inject(user), inject(sendService))
}

// calling app.create(UserProfile.Steve) is the same as calling
// App(User("Steve"), EmailSendService(RealFormatter))

// test dep
val printSendService: Provider<PrintSendService, UserProfile> = { _, inject ->
	PrintSendService(inject(formatterValue))
}

fun main(args: Array<String>) {

	listOf(
			// profiles
			Injector.create(UserProfile.Steve),
			Injector.create(UserProfile.Peter),
			// Mock by other Dependency
			Injector.create(UserProfile.Steve, MockMap.of(sendService.mock with printSendService)),
			// Mock any class by value
			Injector.create(UserProfile.Steve,  MockMap.of(formatterValue.mock withValue TestFormatter)),
			// Mock by value
			Injector.create(UserProfile.Steve,  MockMap.of(user.mock withValue User("This is not Steve!"))),
			// combine all of them
			Injector.create(UserProfile.Steve, MockMap.of(
					sendService.mock with printSendService,
					formatterValue.mock withValue TestFormatter,
					user.mock withValue User("This is not Steve!")
			))
	).map { it.invoke(app) }.forEach(App::start)
}