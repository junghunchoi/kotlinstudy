package kotlinstudy

/**
 * 코틀린 기본 문법 (Java 개발자를 위한)
 * 
 * 이 파일은 Java 개발자가 Kotlin으로 전환할 때 알아야 할 기본 문법을 다룹니다.
 */

// 파일 수준 함수 (클래스 외부에 정의할 수 있음)
fun main() {
    // 1. 변수 선언 -----------------
    
    // 불변 변수 (final과 유사)
    val immutable: String = "이 값은 변경할 수 없습니다"
    
    // 가변 변수
    var mutable: String = "이 값은 변경할 수 있습니다"
    mutable = "변경된 값"
    
    // 타입 추론 (타입 선언 생략 가능)
    val inferredType = "타입이 자동으로 String으로 추론됩니다"
    val number = 42 // Int로 추론
    
    // 2. 기본 타입 -----------------
    
    // 코틀린의 모든 것은 객체 (원시 타입과 래퍼 타입 구분 없음)
    val anInt: Int = 10
    val aLong: Long = 100L
    val aFloat: Float = 10.1f
    val aDouble: Double = 10.1
    val aBoolean: Boolean = true
    val aChar: Char = 'a'
    
    // 문자열 템플릿
    val name = "Kotlin"
    println("Hello, $name!") // 변수 직접 사용
    println("변수 길이: ${name.length}") // 표현식 사용
    
    // 3. 조건문 -----------------
    
    // if-else는 표현식으로 사용 가능 (삼항 연산자 대체)
    val max = if (anInt > aLong) anInt else aLong
    
    // when 표현식 (향상된 switch문)
    val result = when {
        anInt < 0 -> "음수"
        anInt == 0 -> "0"
        anInt < 10 -> "한 자리 양수"
        else -> "두 자리 이상 양수"
    }
    
    // 값을 기준으로 하는 when
    when (anInt) {
        0, 1 -> println("0 또는 1")
        in 2..10 -> println("2부터 10 사이")
        !in 20..100 -> println("20에서 100 사이가 아님")
        else -> println("기타")
    }
    
    // 4. 반복문 -----------------
    
    // for 루프 (범위 사용)
    for (i in 1..5) {
        println(i) // 1, 2, 3, 4, 5
    }
    
    // 다양한 범위 표현
    for (i in 5 downTo 1) println(i) // 5, 4, 3, 2, 1
    for (i in 1 until 5) println(i)  // 1, 2, 3, 4 (5 제외)
    for (i in 1..10 step 2) println(i) // 1, 3, 5, 7, 9
    
    // 컬렉션 반복
    val items = listOf("apple", "banana", "kiwi")
    for (item in items) {
        println(item)
    }
    
    // 인덱스와 함께 반복
    for ((index, value) in items.withIndex()) {
        println("$index: $value")
    }
    
    // while과 do-while (Java와 동일)
    var x = 0
    while (x < 5) {
        println(x)
        x++
    }
    
    // 5. 함수 -----------------
    simpleFunction()
    val sum = add(5, 3)
    println("합계: $sum")
    
    // 명명된 인자 (순서 변경 가능)
    val result2 = add(b = 10, a = 5)
    
    // 기본 인자 값 사용
    greet() // 기본값 "Guest" 사용
    greet("Developer")
    
    // 단일 표현식 함수 사용
    val squared = square(5)
    println("5의 제곱: $squared")
    
    // 6. 컬렉션 -----------------
    
    // 불변 컬렉션 (Immutable)
    val immutableList = listOf(1, 2, 3, 4, 5)
    val immutableMap = mapOf("one" to 1, "two" to 2)
    val immutableSet = setOf("a", "b", "c")
    
    // 가변 컬렉션 (Mutable)
    val mutableList = mutableListOf(1, 2, 3)
    mutableList.add(4) // 추가 가능
    
    val mutableMap = mutableMapOf("one" to 1)
    mutableMap["two"] = 2 // 추가 가능
    
    // 컬렉션 생성 빌더 사용
    val list = buildList {
        add(1)
        add(2)
        add(3)
    }
}

// 반환 타입이 없는 함수 (Java의 void)
fun simpleFunction() {
    println("간단한 함수 실행")
}

// 두 매개변수를 받아 Int를 반환하는 함수
fun add(a: Int, b: Int): Int {
    return a + b
}

// 기본 매개변수 값
fun greet(name: String = "Guest") {
    println("안녕하세요, $name!")
}

// 단일 표현식 함수 (간결한 표현)
fun square(x: Int): Int = x * x

// 확장 함수 (문자열 클래스에 기능 추가)
fun String.addExclamation(): String {
    return this + "!"
}

// 중위 함수 (infix)
infix fun Int.multiplyBy(other: Int): Int = this * other

// 연산자 오버로딩 예시
operator fun Int.times(str: String) = str.repeat(this)
