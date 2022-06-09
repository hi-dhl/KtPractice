package com.hi.dhl.demo.reflect

import org.openjdk.jmh.annotations.*
import org.openjdk.jmh.infra.Blackhole
import org.openjdk.jmh.results.format.ResultFormatType
import org.openjdk.jmh.runner.Runner
import org.openjdk.jmh.runner.options.OptionsBuilder
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible

/**
 * <pre>
 *     author: dhl
 *     date  : 2022/5/6
 *     website: https://www.hi-dhl.com
 *     desc  :
 * </pre>
 */

@BenchmarkMode(Mode.AverageTime) // 平均时间，单位为 time/op
@Warmup(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS) // 预热，5次，每次 3s
@Measurement(iterations = 5, time = 3, timeUnit = TimeUnit.SECONDS) // 执行执行， 5次，3s
@Fork(1) // 1 个进程
@OutputTimeUnit(TimeUnit.MILLISECONDS) // 统计结果的时间单位
@State(Scope.Thread) // 默认的 State，每个测试线程分配一个实例
@Threads(1) // 1 个线程
open class ReflectBenchmark {

    // 创建单个对象
    @Benchmark
    fun createSingleInstance(bh: Blackhole) {
        bh.consume(Person())
    }

    // 单次反射创建对象
    @Benchmark
    fun createSingleReflectInstance(bh: Blackhole) {
        bh.consume(Person::class.primaryConstructor?.call())
    }

    // 正常创建对象
    @Benchmark
    fun createInstance(bh: Blackhole) {
        for (index in 0 until 100_000) {
            bh.consume(Person())
        }
    }

    // 通过反射创建对象
    @Benchmark
    fun createReflectInstance(bh: Blackhole) {
        for (index in 0 until 100_000) {
            bh.consume(Person::class.primaryConstructor?.call())
        }
    }

    // 通过反射创建对象+开启安全检查
    @Benchmark
    fun createReflectInstanceAccessibleFalse(bh: Blackhole) {
        val constructor = Person::class.primaryConstructor
        for (index in 0 until 100_000) {
            bh.consume(constructor?.call())
        }
    }

    // 通过反射创建对象+关掉安全检查
    @Benchmark
    fun createReflectInstanceAccessibleTrue(bh: Blackhole) {
        val constructor = Person::class.primaryConstructor
        constructor?.isAccessible = true
        for (index in 0 until 100_000) {
            bh.consume(constructor?.call())
        }
    }

    //  正常调用方法
    @Benchmark
    fun callMethod(bh: Blackhole) {
        val person = Person()
        for (index in 0 until 100_000) {
            bh.consume(person.getName())
        }
    }

    // 通过反射调用方法
    @Benchmark
    fun callReflectMethod(bh: Blackhole) {
        val person = Person()
        for (index in 0 until 100_000) {
            val method = Person::class.declaredFunctions.find { it.name == "getName" }
            bh.consume(method?.call(person))
        }
    }

    // 通过反射调用方法+开启安全检查
    @Benchmark
    fun callReflectMethodAccessiblFalse(bh: Blackhole) {
        val person = Person()
        val method = Person::class.declaredFunctions.find { it.name == "getName" }
        for (index in 0 until 100_000) {
            bh.consume(method?.call(person))
        }
    }

    // 通过反射调用方法+关掉安全检查
    @Benchmark
    fun callReflectMethodAccessiblTrue(bh: Blackhole) {
        val person = Person()
        val method = Person::class.declaredFunctions.find { it.name == "getName" }
        method?.isAccessible = true
        for (index in 0 until 100_000) {
            bh.consume(method?.call(person))
        }
    }

    //  正常调用属性
    @Benchmark
    fun callPropertie(bh: Blackhole) {
        val person = Person()
        for (index in 0 until 100_000) {
            bh.consume(person.age)
        }
    }

    // 通过反射调用属性
    @Benchmark
    fun callReflectPropertie(bh: Blackhole) {
        val person = Person()
        for (index in 0 until 100_000) {
            val propertie = Person::class.declaredMemberProperties.find { it.name == "age" }
            bh.consume(propertie?.call(person))
        }
    }

    // 通过反射调用属性+开启安全检查
    @Benchmark
    fun callReflectPropertieAccessibleFalse(bh: Blackhole) {
        val person = Person::class.createInstance()
        val propertie = Person::class.declaredMemberProperties.find { it.name == "age" }
        for (index in 0 until 100_000) {
            bh.consume(propertie?.call(person))
        }
    }

    // 通过反射调用属性+关掉安全检查
    @Benchmark
    fun callReflectPropertieAccessibleTrue(bh: Blackhole) {
        val person = Person::class.createInstance()
        val propertie = Person::class.declaredMemberProperties.find { it.name == "age" }
        propertie?.isAccessible = true
        for (index in 0 until 100_000) {
            bh.consume(propertie?.call(person))
        }
    }

    // 正常调用伴生对象
    @Benchmark
    fun callCompaion(bh: Blackhole) {
        for (index in 0 until 100_000) {
            bh.consume(Person.getAddress())
        }
    }

    // 通过反射调用伴生对象
    @Benchmark
    fun createReflectCompaion(bh: Blackhole) {
        val classes = Person::class
        val personInstance = classes.companionObjectInstance
        val personObject = classes.companionObject
        for (index in 0 until 100_000) {
            val compaion = personObject?.declaredFunctions?.find { it.name == "getAddress" }
            bh.consume(compaion?.call(personInstance))
        }
    }

    // 通过反射调用伴生对象 + 开启安全检查
    @Benchmark
    fun callReflectCompaionAccessibleFalse(bh: Blackhole) {
        val classes = Person::class
        val personInstance = classes.companionObjectInstance
        val personObject = classes.companionObject
        val compaion = personObject?.declaredFunctions?.find { it.name == "getAddress" }
        for (index in 0 until 100_000) {
            bh.consume(compaion?.call(personInstance))
        }
    }

    // 通过反射调用伴生对象 + 关掉安全检查
    @Benchmark
    fun callReflectCompaionAccessibleTrue(bh: Blackhole) {
        val classes = Person::class
        val personInstance = classes.companionObjectInstance
        val personObject = classes.companionObject
        val compaion = personObject?.declaredFunctions?.find { it.name == "getAddress" }
        compaion?.isAccessible = true
        for (index in 0 until 100_000) {
            bh.consume(compaion?.call(personInstance))
        }
    }

}

fun main() {
    val millis = System.currentTimeMillis()
    val file = File("report/reflect")
    file.mkdirs()
    val options = OptionsBuilder()
        .include(ReflectBenchmark::class.java.simpleName)
        .output("${file.path}/benchmark_${millis}.log") // 生成Log文件
        .result("${file.path}/benchmark_${millis}.json") // 生成 json ，用于图形分析
        .resultFormat(ResultFormatType.JSON)
        .build()
    Runner(options).run()
}