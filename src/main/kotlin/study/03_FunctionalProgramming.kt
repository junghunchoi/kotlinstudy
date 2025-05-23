package kotlinstudy

/**
 * 코틀린의 함수형 프로그래밍 (Java 개발자를 위한)
 * 
 * 이 파일은 Java 개발자가 Kotlin의 함수형 프로그래밍 기능을 이해하는 데 필요한 내용을 다룹니다.
 * 코틀린에서는 함수가 일급 시민(first-class citizens)으로 취급되며, 다양한 함수형 프로그래밍 패턴을 지원합니다.
 */

fun main() {
    // 1. 람다 표현식 ----------------------
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
    
    // 기본 람다 구문
    val squared = numbers.map { it * it }
    println("제곱한 숫자들: $squared")
    
    // 여러 매개변수를 가진 람다
    val sumOfPairs = numbers.zipWithNext { a, b -> a + b }
    println("인접 쌍의 합: $sumOfPairs")
    
    // 2. 고차 함수 ----------------------
    // 함수를 매개변수로 받는 함수
    val evenNumbers = numbers.filter { it % 2 == 0 }
    println("짝수: $evenNumbers")
    
    // 함수 참조 사용 (::함수명)
    val doubledNumbers = numbers.map(::double)
    println("두 배로 늘린 숫자들: $doubledNumbers")
    
    // 함수를 반환하는 함수
    val multiplyBy3 = getMultiplier(3)
    println("3을 곱한 결과: ${multiplyBy3(5)}") // 15
    
    // 3. 컬렉션 함수형 API ----------------------
    
    // map - 각 요소를 변환
    val numberStrings = numbers.map { it.toString() }
    
    // filter - 조건에 맞는 요소만 선택
    val largeNumbers = numbers.filter { it > 5 }
    
    // flatMap - 중첩 컬렉션을 평면화
    val nestedLists = listOf(listOf(1, 2, 3), listOf(4, 5, 6))
    val flattened = nestedLists.flatMap { it }
    println("평면화된 리스트: $flattened")
    
    // reduce - 컬렉션을 단일 값으로 축소
    val sum = numbers.reduce { acc, i -> acc + i }
    println("합계: $sum")
    
    // fold - 초기값을 지정하여 축소
    val sumWithInitial = numbers.fold(100) { acc, i -> acc + i }
    println("초기값 100으로 시작한 합계: $sumWithInitial")
    
    // groupBy - 그룹화
    val grouped = numbers.groupBy { if (it % 2 == 0) "짝수" else "홀수" }
    println("그룹화된 결과: $grouped")
    
    // 4. 시퀀스 ----------------------
    // 지연 평가(lazy evaluation)를 위한 시퀀스 사용
    
    // 무거운 연산 시 체이닝에서 중간 컬렉션 생성 방지
    val evenSquaresSum = numbers.asSequence()
        .filter { println("필터링: $it"); it % 2 == 0 }
        .map { println("매핑: $it"); it * it }
        .take(2) // 처음 2개만 처리
        .sum()
    println("첫 두 짝수의 제곱 합: $evenSquaresSum")
    
    // 5. 함수 타입 ----------------------
    
    // 함수 타입 변수 선언
    val isEven: (Int) -> Boolean = { it % 2 == 0 }
    val square: (Int) -> Int = { it * it }
    
    // 고차 함수에 함수 전달
    println("짝수인가? ${isEven(4)}")
    
    // 함수 타입 매개변수
    processNumbers(numbers, isEven, square)
    
    // 널이 될 수 있는 함수 타입
    val nullableFunc: ((Int) -> Int)? = if (numbers.size > 5) square else null
    nullableFunc?.let { func -> 
        println("함수 실행 결과: ${func(5)}")
    }
    
    // 6. 인라인 함수 ----------------------
    measureTime {
        // 이 코드 블록의 실행 시간 측정
        Thread.sleep(50)
    }
    
    // 7. 확장 함수와 함수형 프로그래밍 ----------------------
    val filteredAndMapped = numbers.myFilter { it > 5 }.myMap { it * 2 }
    println("커스텀 필터와 맵: $filteredAndMapped")
    
    // 8. 실전 예제: 데이터 처리 파이프라인 ----------------------
    val users = listOf(
        User("Alice", 25, listOf("Java", "Kotlin")),
        User("Bob", 30, listOf("Java", "Python")),
        User("Charlie", 22, listOf("Kotlin", "Swift")),
        User("David", 35, listOf("Java", "C++")),
        User("Eve", 28, listOf("Kotlin", "JavaScript"))
    )
    
    // 코틀린을 아는 사용자 평균 나이 계산
    val avgAgeOfKotlinUsers = users
        .filter { "Kotlin" in it.skills }
        .map { it.age }
        .average()
    
    println("코틀린 사용자의 평균 나이: $avgAgeOfKotlinUsers")
    
    // 나이별 사용자 그룹화
    val usersByAgeGroup = users.groupBy { 
        when {
            it.age < 25 -> "Junior"
            it.age < 35 -> "Mid-level"
            else -> "Senior"
        }
    }
    
    usersByAgeGroup.forEach { (ageGroup, usersInGroup) ->
        println("$ageGroup: ${usersInGroup.map { it.name }}")
    }
    
    // 9. let, run, with, apply, also 범위 함수 ----------------------
    
    // let - 객체를 it으로 참조, 마지막 표현식 반환
    val nameLengths = users.let { userList ->
        userList.map { it.name.length }
    }
    println("사용자 이름 길이: $nameLengths")
    
    // run - 객체를 this로 참조, 마지막 표현식 반환
    val allSkills = users.run {
        flatMap { it.skills }.distinct()
    }
    println("모든 기술: $allSkills")
    
    // with - 객체를 this로 참조, non-extension 함수
    val userSummary = with(users.first()) {
        "이름: $name, 나이: $age, 기술: $skills"
    }
    println("첫번째 사용자 요약: $userSummary")
    
    // apply - 객체를 this로 참조, 객체 자체 반환
    val newUser = User("Frank", 0, emptyList()).apply {
        age = 40
        skills = listOf("Kotlin", "Android")
    }
    println("새 사용자: $newUser")
    
    // also - 객체를 it으로 참조, 객체 자체 반환
    val checkedUser = newUser.also {
        println("사용자 생성됨: ${it.name}")
        require(it.age > 0) { "나이는 양수여야 합니다." }
    }
}

// 타입 별칭으로 함수 타입 간소화 
typealias Operation = (Int, Int) -> Int
typealias Predicate<T> = (T) -> Boolean

// 기본 함수
fun double(x: Int): Int = x * 2

// 함수를 반환하는 함수
fun getMultiplier(factor: Int): (Int) -> Int {
    return { it * factor }
}

// 함수 타입 매개변수를 받는 함수
fun processNumbers(
    numbers: List<Int>, 
    predicate: (Int) -> Boolean, 
    transform: (Int) -> Int
): List<Int> {
    return numbers.filter(predicate).map(transform)
}

// 인라인 함수 (람다 성능 향상)
inline fun measureTime(block: () -> Unit) {
    val start = System.currentTimeMillis()
    block()
    val end = System.currentTimeMillis()
    println("실행 시간: ${end - start} ms")
}

// 확장 함수를 사용한 함수형 API 구현
fun <T> List<T>.myFilter(predicate: (T) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (predicate(item)) {
            result.add(item)
        }
    }
    return result
}

fun <T, R> List<T>.myMap(transform: (T) -> R): List<R> {
    val result = mutableListOf<R>()
    for (item in this) {
        result.add(transform(item))
    }
    return result
}

// 스코프 함수 예제
fun scopeFunctionsExample() {
    val str = "Hello"
    
    // let - it 키워드로 참조
    str.let {
        println("let: $it length is ${it.length}")
    }
    
    // with - this로 참조 (암시적)
    with(str) {
        println("with: $this length is $length")
    }
    
    // run - this로 참조 (암시적)
    str.run {
        println("run: $this length is $length")
    }
    
    // apply - this로 참조, 객체 자체 반환
    val result = str.apply {
        println("apply: $this length is $length")
    }
    println("apply result: $result")
    
    // also - it으로 참조, 객체 자체 반환
    val result2 = str.also {
        println("also: $it length is ${it.length}")
    }
    println("also result: $result2")
}

// 사용자 데이터 클래스
data class User(
    val name: String,
    var age: Int,
    var skills: List<String>
)

// 커링 예제 - 함수의 부분 적용
fun add(a: Int, b: Int, c: Int) = a + b + c

fun curryAdd(a: Int) = { b: Int -> { c: Int -> add(a, b, c) } }

// 함수 합성 예제
fun <A, B, C> compose(f: (B) -> C, g: (A) -> B): (A) -> C {
    return { x -> f(g(x)) }
}
