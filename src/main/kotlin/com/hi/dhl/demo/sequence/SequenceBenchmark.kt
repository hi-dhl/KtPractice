package com.hi.dhl.demo.sequence

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * <pre>
 *     author: dhl
 *     date  : 2022/5/13
 *     desc  :
 * </pre>
 */

@BenchmarkMode(Mode.AverageTime) // 平均时间，单位为 time/op
@Warmup(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS) // 预热，5次，每次 3s
@Measurement(iterations = 10, time = 3, timeUnit = TimeUnit.SECONDS) // 执行执行， 5次，3s
@Fork(1) // 1 个进程
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 统计结果的时间单位
@State(Scope.Thread) // 默认的 State，每个测试线程分配一个实例
@Threads(10) // 1 个线程
open class SequenceBenchmark {

    @Benchmark
    fun runSequence(bh: Blackhole) {
        val result = (1..100_000).asSequence()
            .map { it * 2 }
            .filter { it % 2 == 1 }
            .count()
        bh.consume(result)
    }

    @Benchmark
    fun runIterable(bh: Blackhole) {
        val result = (1..100_000).asIterable()
            .map { it * 2 }
            .filter { it % 2 == 1 }
            .count()
        bh.consume(result)
    }

}

fun main() {
    val millis = System.currentTimeMillis()
    val file = File("report/sequence")
    file.mkdirs()
    val options = OptionsBuilder()
        .include(SequenceBenchmark::class.java.simpleName)
        .output("${file.path}/benchmark_${millis}.log") // 生成Log文件
        .result("${file.path}/benchmark_${millis}.json") // 生成 json ，用于图形分析
        .resultFormat(ResultFormatType.JSON)
        .build()
    Runner(options).run()
}