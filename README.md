# KompanionDI

A small compile-time, type-safe dependency injection library without reflection.

Originally intended to be used by creating providers in Kotlin's companion objects, the library now can be used with more traditional styles as well. 

The library provides three core parts:

- **Provider:** A provider is simply a function that can create a instance for a given type. It can use an Injector to do so.
- **Injector:** An injector can create an instance given a provider. It stores a profile and a list of mocks.
- **Context:** A context connects providers and an injector completing the system.

You can choose to use the complete abstraction with contexts or handle providers and injectors yourself either with companion objects or other global variables.

## Examples

We have two classes/interfaces `Foo` amd `Bar` where `Bar` is supposed to have a dependency on `Foo`.

### Using Context
```kotlin

// these classes can be hidden behind an interface no problem
class Foo {
	fun getData() = 1
}

class Bar(private val foo: Foo) {
	fun printData() {
		println(foo.getData())
	}
}

// setup DI

interface DI {
	val foo: Provider<Foo, Profile>
	val bar: Provider<Bar, Profile>
}

class BaseDI : DI {
	override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
	override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(foo)) }
}

// create the context
val inject: Context<DI, Profile> = createContext(BaseDI())

// use it somewhere

fun test() {
	inject { bar }.printData()
}
```

### Using Companions / Injectors

```kotlin
interface Foo {
	fun getData(): Int
	
	companion object : Provider<Foo, Profile> by { _, _ -> FooImpl() }
}

interface Bar {
	fun printData()
	
	companion object : Provider<Bar, Profile> by { _, inject -> BarImpl(inject(Foo)) }
}

class FooImpl : Foo {
	override fun getData() = 1
}

class BarImpl(private val foo: Foo) : Bar {
	override fun printData() {
		println(foo.getData())
	}
}

// create an injector
val inject = createInjector()

// use it

fun test() {
	inject(Bar).printData()
}
```

## Helpers
To help create providers there are wrappers to create singleton providers and providers that return a static (lazy) value.

## Mocking
Mocking is not done on a per class basis, but on a per providers basis. This simplifies the reasoning about the process and should be sufficient in most cases.

TODO: example

## Original Idea

The use of companion providers is inspired by [this blog post](https://blog.kotlin-academy.com/effective-java-in-kotlin-item-1-consider-static-factory-methods-instead-of-constructors-8d0d7b5814b2).