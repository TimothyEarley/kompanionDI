/*
The MIT license (MIT)

Copyright (c) 2018 REWE Digital GmbH

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the "Software"), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.rewedigital.katana.comparison

import org.nield.kotlinstatistics.median

const val ITERATIONS = 1_000_000

data class Timings(val setup: Long,
                   val execution: Long)

data class Results(val setupAverage: Double,
                   val setupMedian: Double,
                   val executionAverage: Double,
                   val executionMedian: Double)

val katanaTimings = mutableListOf<Timings>()
val koinTimings = mutableListOf<Timings>()
val kodeinTimings = mutableListOf<Timings>()
val kompanionTimings = mutableListOf<Timings>()

fun main(args: Array<String>) {
    (0 until ITERATIONS).forEach {
        println("Iteration: $it")

        val katana = KatanaSubject()
        katanaTimings.add(measure(katana))

        val koin = KoinSubject()
        koinTimings.add(measure(koin))

        val kodein = KodeinSubject()
        kodeinTimings.add(measure(kodein))

        val kompanion = KompanionSubject()
        kompanionTimings.add(measure(kompanion))
    }

    println()

    println("=== Katana ===")
    katanaTimings.results().print()
    println()

    println("=== Koin ===")
    koinTimings.results().print()
    println()

    println("=== Kodein ===")
    kodeinTimings.results().print()
    println()

    println("=== Kompanion ===")
    kompanionTimings.results().print()
    println()
}

fun measure(subject: Subject): Timings {
    val setup = measureCall { subject.setup() }
    val execution = measureCall { subject.execute() }
    subject.shutdown()
    return Timings(setup, execution)
}

fun measureCall(body: () -> Unit): Long {
    val before = System.nanoTime()
    body()
    val after = System.nanoTime()
    return after - before
}

fun Iterable<Timings>.results() =
    Results(setupAverage = map { it.setup }.average(),
                                              setupMedian = map { it.setup }.median(),
                                              executionAverage = map { it.execution }.average(),
                                              executionMedian = map { it.execution }.median())

fun Results.print() {
    println("setup (average):     $setupAverage ns")
    println("setup (median):      $setupMedian ns")
    println("execution (average): $executionAverage ns")
    println("execution (median):  $executionMedian ns")
}

fun Double.toMillis() = this / 1_000_000
