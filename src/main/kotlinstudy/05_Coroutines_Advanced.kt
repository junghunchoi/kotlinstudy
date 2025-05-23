package kotlinstudy

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.measureTimeMillis

/**
 * 코틀린의 코루틴 (Java 개발자를 위한) - 고급 기능
 * 
 * 이 파일은 코루틴의 고급 기능인 채널, 플로우 등을 다룹니다.
 * 05_Coroutines.kt 파일과 함께 학습하세요.
 */

suspend fun main() = coroutineScope {
    // 1. 채널 예제 ----------------------
    exampleChannels()
    
    // 2. Flow 예제 ----------------------
    exampleFlow()
    
    // 3. 구조화된 동시성 예제 ----------------------
    exampleStructuredConcurrency()
}

// 채널 예제 - 코루틴 간 통신
suspend fun exampleChannels() = coroutineScope {
    println("\n--- 채널 예제 ---")
    
    // 기본 채널 생성
    val channel = Channel<Int>()
    
    // 생산자 코루틴
    launch {
        for (i in 1..5) {
            println("값 $i 전송 중...")
            channel.send(i) // 값 전송
            delay(100)
        }
        channel.close() // 완료 후 채널 닫기
    }
    
    // 소비자 코루틴
    launch {
        // 채널로부터 값 수신
        for (value in channel) {
            println("값 $value 수신됨")
        }
        println("채널 닫힘")
    }
    
    // 버퍼 채널 예제
    val bufferedChannel = Channel<Int>(3) // 버퍼 크기 3
    
    launch {
        for (i in 1..5) {
            println("버퍼 채널에 $i 전송")
            bufferedChannel.send(i)
            println("$i 전송 완료")
        }
        bufferedChannel.close()
    }
    
    delay(500)
    
    launch {
        for (value in bufferedChannel) {
            println("버퍼 채널에서 $value 수신")
            delay(200) // 소비자가 느리게 처리
        }
    }
    
    delay(2000) // 모든 작업 완료 대기
}

// Flow 예제 - 비동기 데이터 스트림
suspend fun exampleFlow() = coroutineScope {
    println("\n--- Flow 예제 ---")
    
    // 간단한 Flow 생성
    val simpleFlow = flow {
        for (i in 1..3) {
            delay(100) // 비동기 작업 시뮬레이션
            emit(i) // 값 방출
        }
    }
    
    // Flow 수집
    simpleFlow.collect { value ->
        println("Flow에서 $value 수신")
    }
    
    // Flow 연산자 사용
    val transformedFlow = simpleFlow
        .map { it * it } // 제곱
        .filter { it > 1 } // 필터링
    
    transformedFlow.collect { value ->
        println("변환된 Flow에서 $value 수신")
    }
    
    // Flow 컨텍스트 예제
    flow {
        println("Flow 내부 컨텍스트: ${Thread.currentThread().name}")
        emit(1)
    }
    .flowOn(Dispatchers.IO) // 흐름 상류에 컨텍스트 변경
    .collect { value ->
        println("컬렉터 컨텍스트: ${Thread.currentThread().name}, 값: $value")
    }
    
    // Flow에서 예외 처리
    flow {
        emit(1)
        throw RuntimeException("Flow 에러")
    }
    .catch { e -> 
        println("예외 처리: $e")
        emit(-1) // 대체 값 방출
    }
    .collect { value ->
        println("예외 처리 후 값: $value")
    }
    
    // stateFlow 예제 (상태 홀더)
    val stateFlow = MutableStateFlow("초기 상태")
    
    // 관찰자 코루틴
    val job = launch {
        stateFlow.collect { value ->
            println("StateFlow 업데이트: $value")
        }
    }
    
    delay(100)
    stateFlow.value = "상태 변경 1"
    delay(100)
    stateFlow.value = "상태 변경 2"
    delay(100)
    
    job.cancelAndJoin() // 수집 취소
    
    // sharedFlow 예제 (이벤트 브로드캐스팅)
    val sharedFlow = MutableSharedFlow<String>()
    
    // 첫 번째 수집기
    val collector1 = launch {
        sharedFlow.collect { value ->
            println("수집기 1: $value")
        }
    }
    
    // 두 번째 수집기
    val collector2 = launch {
        sharedFlow.collect { value ->
            println("수집기 2: $value")
        }
    }
    
    delay(100)
    sharedFlow.emit("이벤트 1")
    delay(100)
    sharedFlow.emit("이벤트 2")
    delay(100)
    
    collector1.cancelAndJoin()
    collector2.cancelAndJoin()
}

// 구조화된 동시성 예제
suspend fun exampleStructuredConcurrency() = coroutineScope {
    println("\n--- 구조화된 동시성 예제 ---")
    
    try {
        coroutineScope {
            // 첫 번째 작업
            val job1 = launch {
                delay(100)
                println("작업 1 완료")
            }
            
            // 두 번째 작업 (예외 발생)
            val job2 = launch {
                delay(50)
                throw RuntimeException("작업 2 실패!")
            }
            
            // 세 번째 작업
            val job3 = launch {
                delay(200)
                println("작업 3 완료")
            }
            
            // job2의 예외로 인해 모든 코루틴이 취소됨
        }
    } catch (e: Exception) {
        println("스코프에서 예외 발생: $e")
    }
    
    // 부모-자식 관계
    val parent = launch {
        println("부모 코루틴 시작")
        
        launch {
            try {
                println("자식 1 시작")
                delay(1000)
                println("자식 1 완료") // 실행되지 않음
            } catch (e: CancellationException) {
                println("자식 1 취소됨")
                throw e // 취소는 재전파해야 함
            }
        }
        
        launch {
            try {
                println("자식 2 시작")
                delay(1000)
                println("자식 2 완료") // 실행되지 않음
            } catch (e: CancellationException) {
                println("자식 2 취소됨")
                throw e
            }
        }
        
        delay(100)
        throw RuntimeException("부모 실패!")
    }
    
    try {
        parent.join()
    } catch (e: Exception) {
        println("부모 작업 예외: $e")
    }
}

// 실전 예제: 동시 API 호출
suspend fun concurrentApiCalls() = coroutineScope {
    println("\n--- 실전 예제: 동시 API 호출 ---")
    
    // 3개의 API 호출을 모두 기다림
    val results = awaitAll(
        async { fetchUserProfile() },
        async { fetchUserPosts() },
        async { fetchUserFriends() }
    )
    
    println("모든 API 호출 완료: $results")
    
    // 가장 빠른 응답 사용
    val fastResult = select<String> {
        async { fetchFromServer1() }.onAwait { it + " (서버 1)" }
        async { fetchFromServer2() }.onAwait { it + " (서버 2)" }
    }
    
    println("가장 빠른 서버 응답: $fastResult")
}

// 가상의 API 호출 함수들
suspend fun fetchUserProfile(): String {
    delay(300) // 300ms 소요
    return "사용자 프로필"
}

suspend fun fetchUserPosts(): List<String> {
    delay(500) // 500ms 소요
    return listOf("게시물 1", "게시물 2")
}

suspend fun fetchUserFriends(): List<String> {
    delay(400) // 400ms 소요
    return listOf("친구 1", "친구 2", "친구 3")
}

suspend fun fetchFromServer1(): String {
    val time = (Math.random() * 500 + 100).toLong()
    delay(time)
    return "서버 1 데이터"
}

suspend fun fetchFromServer2(): String {
    val time = (Math.random() * 500 + 100).toLong()
    delay(time)
    return "서버 2 데이터"
}

// 실제 애플리케이션에서 코루틴 사용하는 ViewModel 예제
class UserViewModel {
    // 코루틴 스코프 정의 (viewModelScope는 원래 안드로이드 라이브러리에 있음)
    private val viewModelScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    fun loadUserData(userId: String) {
        viewModelScope.launch {
            try {
                // UI 상태 업데이트 (로딩 중)
                updateUiState(UiState.Loading)
                
                // 병렬로 여러 API 호출
                val userInfo = async(Dispatchers.IO) { fetchUserInfo(userId) }
                val userPosts = async(Dispatchers.IO) { fetchUserPosts(userId) }
                
                // 결과 처리
                val combinedData = CombinedUserData(
                    userInfo = userInfo.await(),
                    posts = userPosts.await()
                )
                
                // UI 상태 업데이트 (성공)
                updateUiState(UiState.Success(combinedData))
            } catch (e: Exception) {
                // UI 상태 업데이트 (오류)
                updateUiState(UiState.Error(e.message ?: "Unknown error"))
            }
        }
    }
    
    private suspend fun fetchUserInfo(userId: String): UserInfo {
        delay(1000) // 네트워크 호출 시뮬레이션
        return UserInfo(userId, "사용자 $userId", "user$userId@example.com")
    }
    
    private suspend fun fetchUserPosts(userId: String): List<Post> {
        delay(1500) // 네트워크 호출 시뮬레이션
        return listOf(
            Post("1", "첫 번째 게시물"),
            Post("2", "두 번째 게시물")
        )
    }
    
    private fun updateUiState(state: UiState) {
        println("UI 상태 업데이트: $state")
    }
    
    // 클래스들
    data class UserInfo(val id: String, val name: String, val email: String)
    data class Post(val id: String, val content: String)
    data class CombinedUserData(val userInfo: UserInfo, val posts: List<Post>)
    
    // UI 상태를 표현하는 sealed 클래스
    sealed class UiState {
        object Loading : UiState()
        data class Success(val data: CombinedUserData) : UiState()
        data class Error(val message: String) : UiState()
    }
    
    // ViewModel 정리
    fun onCleared() {
        viewModelScope.cancel()
    }
}
