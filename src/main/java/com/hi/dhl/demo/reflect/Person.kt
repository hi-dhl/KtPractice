package com.hi.dhl.demo.reflect

/**
 * <pre>
 *     author: dhl
 *     date  : 2022/5/6
 *     website: https://www.hi-dhl.com
 *     desc  :
 * </pre>
 */
class Person {
    var age = 10

    fun getName(): String {
        return "I am DHL"
    }

    companion object {
        fun getAddress(): String = "BJ"
    }
}