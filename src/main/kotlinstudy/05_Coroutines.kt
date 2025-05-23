package kotlinstudy

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import kotlin.system.measureTimeMillis

/**
 * 코루틴 예제 모음
 * 
 * 코루틴은 비동기 프로그래밍을 위한 강력한 도구로, 복잡한 비동기 코드를 마치 동기 코드처럼 간결하게 작성할 수 있습니다.
 * 이 파일에서는 코루틴의 주요 API와 그 사용법을 살펴봅니다.
 */

// 1. launch - 새 코루틴을 시작하고 결과를 기다리지 않음
fun launchExample() = runBlocking {
    println("메인 코루틴 시작")
    
    // launch는 결과를 반환하지 않는 새 코루틴을 시작합니다
    val job = launch {
        delay(1000L) // 논블로킹 지연
        println("launch 코루틴에서 작업 수행")
    }
    
    println("메인 코루틴은 계속 실행됩니다")
    job.join() // launch된 코루틴이 완료될 때까지 기다립니다
    println("모든 코루틴 완료")
}

// 2. async - 결과를 반환하는 코루틴 시작
fun asyncExample() = runBlocking {
    println("async 예제 시작")
    
    // async는 Deferred<T>를 반환하는 코루틴을 시작합니다
    val deferred1 = async {
        delay(1000L)
        println("첫 번째 비동기 작업 수행")
        10
    }
    
    val deferred2 = async {
        delay(500L)
        println("두 번째 비동기 작업 수행")
        20
    }
    
    // await()는 코루틴의 완료를 기다리고 결과를 반환합니다
    val sum = deferred1.await() + deferred2.await()
    println("결과: $sum")
}

// 3. withContext - 다른 컨텍스트로 코루틴 전환
fun withContextExample() = runBlocking {
    println("withContext 예제 시작")
    
    val result = withContext(Dispatchers.Default) {
        // CPU 집약적인 작업을 위한 기본 디스패처에서 실행
        println("Default 디스패처에서 실행 중 (스레드: ${Thread.currentThread().name})")
        delay(100)
        10
    }
    
    println("현재 스레드: ${Thread.currentThread().name}")
    println("withContext 결과: $result")
    
    val ioResult = withContext(Dispatchers.IO) {
        // I/O 작업을 위한 IO 디스패처에서 실행
        println("IO 디스패처에서 실행 중 (스레드: ${Thread.currentThread().name})")
        delay(100)
        "I/O 작업 결과"
    }
    
    println("IO 작업 결과: $ioResult")
}

// 4. coroutineScope - 새 코루틴 스코프 생성
fun coroutineScopeExample() = runBlocking {
    println("coroutineScope 예제 시작")
    
    // coroutineScope는 모든 자식이 완료될 때까지 완료되지 않습니다
    val time = measureTimeMillis {
        coroutineScope {
            launch {
                delay(1000L)
                println("Task 1 완료")
            }
            
            launch {
                delay(2000L)
                println("Task 2 완료")
            }
            
            println("coroutineScope 내에서 두 작업 시작됨")
        }
        println("coroutineScope 완료 - 모든 작업이 끝났습니다")
    }
    
    println("소요 시간: $time ms") // 약 2000ms가 출력됩니다 (병렬 실행)
}

// 5. supervisorScope - 자식 코루틴 오류가 부모에 전파되지 않는 스코프
fun supervisorScopeExample() = runBlocking {
    println("supervisorScope 예제 시작")
    
    try {
        supervisorScope {
            val job1 = launch {
                delay(500L)
                println("첫 번째 자식 작업 성공")
            }
            
            val job2 = launch {
                delay(100L)
                println("두 번째 자식 작업 실패 예정")
                throw IllegalStateException("오류 발생")
            }
            
            try {
                job2.join()
            } catch (e: Exception) {
                println("자식 작업 2에서 오류 처리: ${e.message}")
            }
            
            job1.join()
            println("supervisorScope는 계속 실행 중")
        }
        println("supervisorScope 완료")
    } catch (e: Exception) {
        println("이 코드는 실행되지 않습니다. supervisorScope가 오류를 전파하지 않음")
    }
}

// 6. delay - 일정 시간 지연 (스레드 차단 없음)
fun delayExample() = runBlocking {
    println("delay 예제 시작")
    
    println("1초 대기 시작...")
    delay(1000L) // 현재 코루틴만 일시 중단, 스레드는 다른 작업 수행 가능
    println("1초 대기 완료")
    
    // 병렬 지연 실행
    coroutineScope {
        launch { 
            delay(1000L)
            println("병렬 코루틴 1 완료") 
        }
        launch { 
            delay(1000L)
            println("병렬 코루틴 2 완료") 
        }
    }
    
    println("모든 지연 작업 완료")
}

// 7. yield - 다른 코루틴에 실행 양보
fun yieldExample() = runBlocking {
    println("yield 예제 시작")
    
    // 3개의 동시 코루틴 시작
    launch(Dispatchers.Default) {
        repeat(5) { i ->
            println("코루틴 A, 반복 $i 시작")
            yield() // 다른 코루틴에 실행 양보
            println("코루틴 A, 반복 $i 종료")
        }
    }
    
    launch(Dispatchers.Default) {
        repeat(5) { i ->
            println("코루틴 B, 반복 $i 시작")
            yield() // 다른 코루틴에 실행 양보
            println("코루틴 B, 반복 $i 종료")
        }
    }
    
    delay(100) // 두 코루틴이 모두 시작될 시간을 줍니다
}

// 8. awaitAll - 여러 비동기 작업 동시 대기
fun awaitAllExample() = runBlocking {
    println("awaitAll 예제 시작")
    
    val deferreds = List(3) { index ->
        async {
            delay(1000L - index * 300L) // 각기 다른 지연 시간
            "작업 $index 완료"
        }
    }
    
    // 모든 작업의 완료를 동시에 기다립니다
    val results = deferreds.awaitAll()
    
    results.forEach { println(it) }
    println("모든 작업 완료됨")
}

// 9. flow - 비동기 데이터 스트림 생성
fun flowExample() = runBlocking {
    println("flow 예제 시작")
    
    // 숫자 스트림을 생성하는 flow
    val numberFlow = flow {
        for (i in 1..5) {
            delay(300) // 각 값 사이에 지연 추가
            emit(i) // 값 방출
        }
    }
    
    // 값 변환 (map 연산)
    val squaredFlow = numberFlow.map { it * it }
    
    // 스트림 필터링
    val filteredFlow = squaredFlow.filter { it > 10 }
    
    // collect로 flow 수집 및 처리
    println("필터링된 제곱 값:")
    filteredFlow.collect { value ->
        println(value)
    }
    
    // 다른 flow 연산자 예제
    val sum = numberFlow
        .onEach { println("수집 값: $it") }
        .reduce { acc, value -> acc + value }
    
    println("모든 값의 합: $sum")
}

// 10. collect - 플로우에서 값 수집
fun collectExample() = runBlocking {
    println("collect 예제 시작")
    
    // 간단한 플로우 생성
    val flow = flow {
        for (i in 1..3) {
            delay(100)
            println("값 $i 방출")
            emit("값 $i")
        }
    }
    
    // 기본 수집
    println("기본 수집:")
    flow.collect { value ->
        println("수신: $value")
    }
    
    // collectLatest - 새 값이 방출되면 이전 처리를 취소
    println("\ncollectLatest 예제:")
    flow.collectLatest { value ->
        println("수집 시작: $value")
        delay(150) // 다음 값이 방출되기 전에 처리가 완료되지 않음
        println("수집 완료: $value") // 일부는 취소되어 출력되지 않음
    }
    
    // 첫 번째 값만 수집
    println("\n첫 번째 값만 수집:")
    val firstValue = flow.first()
    println("첫 번째 값: $firstValue")
    
    // 리스트로 수집
    println("\n리스트로 수집:")
    val list = flow.toList()
    println("수집된 리스트: $list")
}

// 여러 코루틴 API를 조합한 실용적인 예제
fun practicalExample() = runBlocking {
    println("실용적인 코루틴 예제 시작")
    
    // 비동기로 데이터 가져오기 (API 호출 시뮬레이션)
    suspend fun fetchUserData(): String {
        delay(1000) // 네트워크 지연 시뮬레이션
        return "사용자 데이터"
    }
    
    suspend fun fetchUserPosts(): List<String> {
        delay(1500) // 네트워크 지연 시뮬레이션
        return listOf("게시물 1", "게시물 2", "게시물 3")
    }
    
    val time = measureTimeMillis {
        // 두 API 호출을 병렬로 실행
        val userDataDeferred = async { fetchUserData() }
        val userPostsDeferred = async { fetchUserPosts() }
        
        // 두 결과 모두 기다림
        val userData = userDataDeferred.await()
        val userPosts = userPostsDeferred.await()
        
        // 결과 처리
        println("사용자 데이터: $userData")
        println("사용자 게시물: $userPosts")
    }
    
    println("두 API 호출 완료 시간: $time ms") // 약 1500ms (병렬 실행 덕분)
}

// 메인 함수
fun main() {
    println("코루틴 예제 실행")
    
    launchExample()
    println("\n-----------------------\n")
    
    asyncExample()
    println("\n-----------------------\n")
    
    withContextExample()
    println("\n-----------------------\n")
    
    coroutineScopeExample()
    println("\n-----------------------\n")
    
    supervisorScopeExample()
    println("\n-----------------------\n")
    
    delayExample()
    println("\n-----------------------\n")
    
    yieldExample()
    println("\n-----------------------\n")
    
    awaitAllExample()
    println("\n-----------------------\n")
    
    flowExample()
    println("\n-----------------------\n")
    
    collectExample()
    println("\n-----------------------\n")
    
    practicalExample()
    
    println("\n모든 예제 완료")
}
