package kotlinstudy

/**
 * 코틀린의 Null 안전성 (Java 개발자를 위한)
 * 
 * 이 파일은 Java 개발자가 Kotlin의 Null 안전성 기능을 이해하는 데 필요한 내용을 다룹니다.
 * 코틀린은 컴파일 시간에 NullPointerException을 방지하기 위한 다양한 기능을 제공합니다.
 */

fun main() {
    // 1. Nullable vs Non-Nullable 타입 ----------------------
    
    // Non-nullable 타입 (기본)
    val nonNullable: String = "이 변수는 항상 String 값이어야 합니다"
    // nonNullable = null // 컴파일 오류
    
    // Nullable 타입 (? 사용)
    val nullable: String? = "이 변수는 null일 수 있습니다"
    val nullString: String? = null // OK
    
    // 2. 안전 호출 연산자 (?.) ----------------------
    
    // null이 아닐 때만 호출됨 (null일 경우 null 반환)
    val length: Int? = nullable?.length // String?의 length 호출: Int?
    println("nullable의 길이: $length")
    
    // 체이닝 가능
    val user: User? = findUser("alice@example.com")
    val city: String? = user?.address?.city
    println("사용자 도시: $city")
    
    // 3. 엘비스 연산자 (?:) ----------------------
    
    // null일 경우 기본값 사용
    val lengthOrDefault: Int = nullable?.length ?: 0
    println("nullable의 길이 (기본값 0): $lengthOrDefault")
    
    // 에러 처리와 함께 사용
    val name = findUser("unknown@example.com")?.name
        ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다")
    
    // 4. 안전한 캐스팅 (as?) ----------------------
    
    val anyValue: Any = "문자열입니다"
    
    // 일반 캐스팅 (실패 시 ClassCastException 발생)
    // val stringValue: String = anyValue as String
    
    // 안전한 캐스팅 (실패 시 null 반환)
    val safeString: String? = anyValue as? String
    val safeInt: Int? = anyValue as? Int // null 반환
    
    println("안전한 문자열 캐스팅: $safeString")
    println("안전한 Int 캐스팅: $safeInt")
    
    // 5. !! 연산자 (Non-null 단언) ----------------------
    
    // null이 아님을 확신할 때 사용 (null일 경우 NPE 발생)
    val definitelyNotNull: String = nullable!!
    println("절대 null이 아닌 값: $definitelyNotNull")
    
    try {
        val willThrowNPE: Int = nullString!!.length // NPE 발생
    } catch (e: NullPointerException) {
        println("예상대로 NPE 발생: ${e.message}")
    }
    
    // 6. null 체크와 스마트 캐스팅 ----------------------
    
    val nullableValue: String? = "스마트 캐스트 예제"
    
    // if로 null 체크 후 스마트 캐스팅 적용
    if (nullableValue != null) {
        // 이 블록 안에서는 nullableValue가 String으로 자동 캐스팅됨
        println("nullableValue 길이: ${nullableValue.length}") // Int? 아니고 Int!
    }
    
    // 7. let 함수와 null 처리 ----------------------
    
    // let과 안전 호출 연산자 조합
    nullable?.let {
        // null이 아닌 경우에만 실행됨
        // it: String (nullable이 아님)
        println("nullable은 null이 아니며 길이는 ${it.length}입니다.")
    }
    
    nullString?.let {
        println("이 코드는 실행되지 않습니다.")
    } ?: println("nullString은 null입니다.")
    
    // 8. null 허용 컬렉션 처리 ----------------------
    
    val mixedList: List<String?> = listOf("첫번째", null, "세번째", null, "다섯번째")
    
    // filterNotNull로 null 요소 제거
    val nonNullList: List<String> = mixedList.filterNotNull()
    println("null이 아닌 요소들: $nonNullList")
    
    // null 요소 개수 세기
    val nullCount = mixedList.count { it == null }
    println("null 요소 개수: $nullCount")
    
    // 9. 플랫폼 타입 (Java 상호운용성) ----------------------
    
    // Java API에서 반환된 String (null일 수도, 아닐 수도 있음)
    val javaString = JavaInterop.getString() // 플랫폼 타입: String!
    
    // 안전한 처리
    val javaStringLength = javaString?.length ?: 0
    
    // 10. 초기화 지연과 null 안전성 ----------------------
    
    val lateInit = LateInitExample()
    
    try {
        lateInit.accessName() // 초기화 전 접근 시 UninitializedPropertyAccessException
    } catch (e: UninitializedPropertyAccessException) {
        println("예상대로 예외 발생: ${e.message}")
    }
    
    lateInit.initializeName("이제 초기화됨")
    println("지연 초기화된 이름: ${lateInit.accessName()}")
}

// Null 안전성 예제를 위한 클래스들
data class Address(val street: String, val city: String, val zipCode: String)
data class User(val name: String, val email: String, val address: Address?)

// 사용자 찾기 (null 반환 가능)
fun findUser(email: String): User? {
    return if (email == "alice@example.com") {
        User("Alice", email, Address("123 Main St", "New York", "10001"))
    } else {
        null
    }
}

// Java와의 상호운용성 예제
object JavaInterop {
    // 이 메서드는 Java 코드를 시뮬레이션합니다 (실제로는 Java 코드에서 온다고 가정)
    fun getString(): String {
        return if (Math.random() > 0.5) "Java에서 온 문자열" else ""
    }
}

// lateinit 예제
class LateInitExample {
    // var만 가능하고, primitive 타입 불가능
    lateinit var name: String
    
    fun initializeName(newName: String) {
        name = newName
    }
    
    fun accessName(): String {
        // 초기화 여부 확인
        if (::name.isInitialized) {
            return name
        } else {
            throw UninitializedPropertyAccessException("name이 초기화되지 않았습니다.")
        }
    }
}

// lazy 초기화 예제
class LazyExample {
    // 처음 접근할 때 초기화
    val expensiveData: List<String> by lazy {
        println("expensiveData 초기화 중...")
        loadExpensiveData()
    }
    
    private fun loadExpensiveData(): List<String> {
        // 실제로는 비용이 많이 드는 작업
        Thread.sleep(1000)
        return listOf("데이터1", "데이터2", "데이터3")
    }
}

// Nullable 매개변수를 사용하는 함수
fun processInput(input: String?) {
    // 1. if-else로 처리
    if (input != null) {
        println("입력값: $input")
    } else {
        println("입력값이 없습니다.")
    }
    
    // 2. ?: 연산자 (더 간결)
    val processedInput = input ?: "기본값"
    println("처리된 입력값: $processedInput")
    
    // 3. when 표현식 사용
    when (input) {
        null -> println("입력값이 없습니다.")
        "" -> println("입력값이 비어있습니다.")
        else -> println("입력값: $input")
    }
}

// Nullable 반환 타입 함수
fun findUserByName(name: String): User? {
    val users = listOf(
        User("Alice", "alice@example.com", null),
        User("Bob", "bob@example.com", null)
    )
    
    return users.find { it.name == name }
}

// 확장 함수를 사용한 Nullable 타입 처리
fun String?.isNullOrShort(maxLength: Int): Boolean {
    // this는 String?이므로 null일 수 있음
    return this == null || this.length <= maxLength
}
