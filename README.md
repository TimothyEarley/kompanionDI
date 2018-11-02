[![](https://jitpack.io/v/TimothyEarley/kompanionDI.svg)](https://jitpack.io/#TimothyEarley/kompanionDI)
[![Build Status](https://travis-ci.com/TimothyEarley/kompanionDI.svg?branch=master)](https://travis-ci.com/TimothyEarley/kompanionDI)
[![Maintainability](https://api.codeclimate.com/v1/badges/33385e66fe6c56422f23/maintainability)](https://codeclimate.com/github/TimothyEarley/kompanionDI/maintainability)
[![Test Coverage](https://api.codeclimate.com/v1/badges/33385e66fe6c56422f23/test_coverage)](https://codeclimate.com/github/TimothyEarley/kompanionDI/test_coverage)

# KompanionDI

A small compile-time, type-safe dependency injection library without reflection.

The main benefit of this library is its compile-time safety without the need of reflection or annotation processing. This comes at the cost of a slightly more verbose setup.

Originally intended to be used by creating providers in Kotlin's companion objects, the library now can be used with more traditional styles as well.

The library provides three core parts:

- **Provider:** A provider is simply a function that can create a instance for a given type. It can use an Injector to do so.
- **Injector:** An injector can create an instance given a provider. It stores a profile and a list of mocks.
- **Context:** A context connects providers and an injector completing the system.

You can choose to use the complete abstraction with contexts or handle providers and injectors yourself either with companion objects or other global variables.

## Usage

The library can be used with [jitpack](https://jitpack.io). Follow the instructions there and then add the dependency:

```groovy
dependencies {
	implementation 'com.github.TimothyEarley.kompanionDI:core:0.3.2'
}
```

## Examples

We have two classes/interfaces `Foo` amd `Bar` where `Bar` is supposed to have a dependency on `Foo`.

### Basic usage
```kotlin

// these classes could/should be hidden behind interfaces
class Foo {
	fun getData() = 1
}

class Bar(private val foo: Foo) {
	fun printData() {
		println(foo.getData())
	}
}

// setup DI

// this could also be a class containing environment specific configuration
type Profile = Unit

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

### Using multiple modules (Recommended)

An example for splitting dependencies into seperate modules. When doing so we can reason about dependencies on a module level, e.g. for MVP: View needs Presenter, Presenter needs Model.

```kotlin
class Foo { ... }

class Bar(private val foo: Foo) { ... }

// setup DI
type Profile = Unit

interface ModuleA {
	val foo: Provider<Foo, Profile>
}

interface ModuleB {
	val bar: Provider<Bar, Profile>
}

// These are the available modules for manual injection, i.e. not everything
// managed by DI has to be publicly visible.
// In this case only module B can be manually injected
interface DI : ModuleB

class BaseModuleA() : ModuleA {
	override val foo: Provider<Foo, Profile> = { profile, inject -> Foo() }
}

class BaseModuleB(private val moduleA: ModuleA): ModuleB {
	// here we use foo from module A
	override val bar: Provider<Bar, Profile> = { profile, inject -> Bar(inject(moduleA.foo)) }
}

class BaseDI(
		moduleA: ModuleA = BaseModuleA(),
		moduleB: ModuleB = BaseModuleB(moduleA)
) : DI, ModuleB by moduleB

// create the context
val inject: Context<DI, Profile> = createContext(BaseDI())

// use it somewhere

fun test() {
	inject { bar }.printData()
}
```

### Using Companions / Injectors

Providers can also be declared directly on companion objects skipping the **Context** and using **Injector** directly. This approach is currently not recommended since it does not seperate definition of classes and their dependency management.

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

## Mocking
Mocking is **not** done on a per class basis, but on a per **providers** basis. This simplifies the reasoning about the process and should be sufficient in most cases.

Providers can be mocked with static values (`mock withValue`) or another provider (`mock with`).

Example (based on the basic usage example)

```kotlin
// open up Foo (in actual code this should be an interface)
open class Foo {
	open fun getData() = 1
}

// Setup DI stays the same
// ...

// create the context
val di = BaseDI()
val inject: Context<DI, Profile> = createContext(di, mocksOf(
	di.foo.mock withValue object : Foo() {
		override fun getData(): Int = 2
	}
))

// use it somewhere

fun test() {
	inject { bar }.printData() // now prints "2"
}

```

## Helpers
To help create providers there are wrappers to create **singleton** providers and providers that return eager or lazy **static** values.

## Original Idea

The use of companion providers is inspired by [this blog post](https://blog.kotlin-academy.com/effective-java-in-kotlin-item-1-consider-static-factory-methods-instead-of-constructors-8d0d7b5814b2).
