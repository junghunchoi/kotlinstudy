package com.emoney

import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis

/**
 * 코틀린의 코루틴 (Java 개발자를 위한) - 기본 개념
 *
 * 이 파일은 Java 개발자가 Kotlin의 코루틴(비동기 프로그래밍)을 이해하는 데 필요한 내용을 다룹니다.
 * 코루틴은 복잡한 비동기, 논블로킹 코드를 간단하게 작성할 수 있게 해주는 Kotlin의 강력한 기능입니다.
 *
 * 주의: 이 코드를 실행하려면 build.gradle에 다음 의존성을 추가해야 합니다:
 * ```
 * implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1'
 * ```
 */

fun main() = runBlocking { // 코루틴의 시작점 (main 함수가 코루틴 블록이 됨)
    println("코루틴 예제 시작: ${currentTime()}")

    // 1. 첫 번째 코루틴 ----------------------
    launch { // 새 코루틴 시작
        delay(1000L) // 논블로킹 지연 (스레드를 차단하지 않음)
        println("1초 후 실행됨: ${currentTime()}")
    }

    println("코루틴 시작 후 즉시 실행됨: ${currentTime()}")

    // 2. 순차적 vs 병렬 실행 ----------------------
    val time = measureTimeMillis {
        // 순차적 실행
        val one = doSomethingUsefulOne()
        val two = doSomethingUsefulTwo()
        println("순차 실행 결과: ${one + two}")
    }
    println("순차 실행 완료 시간: ${time}ms")

    val time2 = measureTimeMillis {
        // 병렬 실행 (async 사용)
        val one = async { doSomethingUsefulOne() }
        val two = async { doSomethingUsefulTwo() }
        println("병렬 실행 결과: ${one.await() + two.await()}")
    }
    println("병렬 실행 완료 시간: ${time2}ms")

    // 3. 코루틴 컨텍스트와 디스패처 ----------------------
    launch { // 부모 컨텍스트 상속 (runBlocking의 컨텍스트)
        println("부모 컨텍스트: $coroutineContext")
    }

    // 다양한 디스패처 사용
    launch(Dispatchers.Default) { // CPU 집약적 작업용
        println("Default 디스패처에서 실행: 스레드 ${Thread.currentThread().name}")
    }

    launch(Dispatchers.IO) { // I/O 작업용
        println("IO 디스패처에서 실행: 스레드 ${Thread.currentThread().name}")
    }

    launch(Dispatchers.Unconfined) { // 제한 없는 디스패처
        println("Unconfined 디스패처 시작: 스레드 ${Thread.currentThread().name}")
        delay(100)
        println("Unconfined 디스패처 재개: 스레드 ${Thread.currentThread().name}")
    }

    // 4. 작업 취소 ----------------------
    val job = launch {
        try {
            repeat(1000) { i ->
                println("작업 $i 실행 중...")
                delay(500L)
            }
        } catch (e: CancellationException) {
            println("코루틴이 취소됨: ${e.message}")
        } finally {
            println("자원 정리 작업 수행")
        }
    }

    delay(1300L) // 실행 허용
    println("코루틴 취소...")
    job.cancel("명시적으로 취소됨") // 작업 취소
    println("코루틴 취소 요청됨")
    job.join() // 취소 완료 대기
    println("이제 코루틴이 취소됨")

    // 5. withTimeout - 시간 제한 ----------------------
    try {
        withTimeout(1000L) {
            repeat(10) { i ->
                println("시간 제한 내 작업 $i...")
                delay(200L)
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("시간 초과: ${e.message}")
    }

    // 시간 초과시 null 반환
    val result = withTimeoutOrNull(1000L) {
        repeat(10) {
            delay(200L)
        }
        "완료" // 시간 내 완료되면 반환
    }
    println("결과: $result") // null 출력

    println("메인 함수 종료: ${currentTime()}")
}

// 가상의 시간이 걸리는 작업들
suspend fun doSomethingUsefulOne(): Int {
    delay(1000L)
    return 13
}

suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L)
    return 29
}

// 현재 시간 포맷팅 유틸리티 함수
fun currentTime(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS")
    return LocalDateTime.now().format(formatter)
}

// 코루틴 스코프 예제
suspend fun exampleScope() = coroutineScope {
    println("\n--- 코루틴 스코프 예제 ---")

    // 새 코루틴 스코프 생성 (자식 코루틴이 모두 완료될 때까지 대기)
    launch {
        delay(200L)
        println("작업 1 완료")
    }

    launch {
        delay(100L)
        println("작업 2 완료")
    }

    println("coroutineScope: 모든 자식 작업이 완료될 때까지 대기")
}

// 비동기 함수 예제 (suspend 함수)
suspend fun fetchUserData(userId: Int): String {
    delay(1000L) // 네트워크 호출 시뮬레이션
    return "User $userId Data"
}

suspend fun fetchMultipleUsers(): List<String> = coroutineScope {
    // 여러 사용자 데이터를 병렬로 가져오기
    val userIds = listOf(1, 2, 3, 4, 5)

    userIds.map { userId ->
        async {
            fetchUserData(userId)
        }
    }.awaitAll() // 모든 비동기 작업 완료 대기
}

// 예외 처리 예제
suspend fun exampleExceptions() = coroutineScope {
    // 글로벌 예외 핸들러 (모든 코루틴에 적용)
    val handler = CoroutineExceptionHandler { _, exception ->
        println("예외 캐치: $exception")
    }

    val job = GlobalScope.launch(handler) {
        throw AssertionError("에러 발생!")
    }

    // 자식 코루틴에서 예외 전파
    val job2 = GlobalScope.launch {
        launch {
            try {
                delay(1000L)
                throw RuntimeException("자식 코루틴 에러!")
            } catch (e: Exception) {
                println("자식 에러 캐치: $e")
            }
        }
    }

    job.join()
    job2.join()
}
