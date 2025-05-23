package com.emoney

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis


fun main1() = runBlocking {
    println("코루틴 시작")

    launch {
        delay(1000L)
        println("1초 후 시작")
    }

    val time = measureTimeMillis {
        // 순차적 실행
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
    }

    val time2 = measureTimeMillis {
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("${one.await() + two.await()}")

    }
}