package com.hi.dhl.demo.sequence

import java.lang.management.ManagementFactory


/**
 * <pre>
 *     author: dhl
 *     date  : 2022/5/14
 *     desc  :
 * </pre>
 */

fun testIterable() {
    (1..3).asIterable()
        .map { print("A$it, "); it * 2 }
        .filter { print("B$it, "); it > 2 }
        .forEach { print("C$it, ") }
}

fun testIterableMemory() {
    (1..100_000).asIterable()
        .map { Model(it) }
        .filter { it.no > 1000 }
        .count()
    val pid = getProcessID()
    val millis = System.currentTimeMillis()
    val process =
        Runtime.getRuntime().exec("jcmd ${pid} GC.heap_dump report/sequence/memory_iterables_${pid}_${millis}.hprof")
    println(process.waitFor())
}

fun testSequence() {
    (1..3).asSequence()
        .map { print("A$it, "); it * 2 }
        .filter { print("B$it, "); it > 2 }
        .forEach { print("C$it, ") }
}

fun testSequenceMemory() {
    (1..100_000).asSequence()
        .map { Model(it) }
        .filter { it.no > 1000 }
        .count()
    val pid = getProcessID()
    val millis = System.currentTimeMillis()
    val process =
        Runtime.getRuntime().exec("jcmd ${pid} GC.heap_dump report/sequence/memory_sequences_${pid}_${millis}.hprof")
    println(process.waitFor())
}

data class Model(val no: Int) {
    private var name: String? = null
    private var addres: String? = null
    private var age: Int = 0
}

fun getProcessID(): Int {
    val runtimeMXBean = ManagementFactory.getRuntimeMXBean()
    return Integer.valueOf(runtimeMXBean.name.split("@".toRegex()).toTypedArray()[0])
        .toInt()
}

fun main() {
    testIterable()
    println()
    testSequence()

    // 测试内存专用，在执行前，先删掉当前类中无关的方法，分别执行下面两个方法进行测试，不要同时执行
    testIterableMemory()
    testSequenceMemory()
}