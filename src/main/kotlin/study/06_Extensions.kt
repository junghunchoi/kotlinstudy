//package kotlinstudy
//
///**
// * 코틀린의 확장 함수와 프로퍼티 (Java 개발자를 위한)
// *
// * 이 파일은 Java 개발자가 Kotlin의 확장 기능을 이해하는 데 필요한 내용을 다룹니다.
// * 확장 함수와 프로퍼티는 기존 클래스에 기능을 추가할 수 있는 강력한 도구입니다.
// */
//
//fun main() {
//    // 1. 기본 확장 함수 ----------------------
//    val message = "Hello, Kotlin"
////    println(message.addExclamation()) // "Hello, Kotlin!" 출력
//
//    // 2. 확장 프로퍼티 ----------------------
//    println("문자열 단어 수는 ${message.wordCount}입니다.") // "문자열 단어 수는 2입니다." 출력
//
//    // 3. 널 안전 확장 함수 ----------------------
//    val nullableString: String? = null
////    println(nullableString.isNullOrShort(10)) // true 출력 (null이므로)
//
//    // 4. 컬렉션 확장 함수 ----------------------
//    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
//    println("짝수: ${numbers.evenNumbers()}")
//    println("홀수: ${numbers.oddNumbers()}")
//
//    // 5. 중위 확장 함수 ----------------------
//    val result = 5 multiplyBy 3
//    println("중위 함수 결과: $result") // "중위 함수 결과: 15" 출력
//
//    // 6. 연산자 확장 함수 ----------------------
//    val repeated = 3 * "ABC" // 연산자 오버로딩
//    println("반복된 문자열: $repeated") // "반복된 문자열: ABCABCABC" 출력
//
//    // 7. 수신 객체의 타입별 확장 함수 ----------------------
//    val any: Any = "I am a String"
//    println(any.typeInfo()) // "This is a String: I am a String" 출력
//
//    val anyNumber: Any = 42
//    println(anyNumber.typeInfo()) // "This is an Int: 42" 출력
//
//    // 8. 제네릭 확장 함수 ----------------------
//    val intList = listOf(1, 2, 3)
//    val stringList = listOf("a", "b", "c")
//
//    println("첫번째 요소 또는 기본값: ${intList.firstOrDefault(0)}")
//    println("빈 리스트의 첫번째 요소 또는 기본값: ${emptyList<String>().firstOrDefault("기본값")}")
//
//    // 9. 확장 함수와 상속 ----------------------
//    val baseObj = Base()
//    val derivedObj = Derived()
//
//    // 정적 타입에 따라 호출되는 함수가 결정됨
//    baseObj.printFunctionInfo()    // "Base 클래스의 확장 함수" 출력
//    derivedObj.printFunctionInfo() // "Derived 클래스의 확장 함수" 출력
//
//    val baseRef: Base = derivedObj
//    baseRef.printFunctionInfo()    // "Base 클래스의 확장 함수" 출력 (동적 디스패치 아님)
//
//    // 10. 확장 함수를 활용한 DSL (도메인 특화 언어) ----------------------
//    val html = buildHtml {
//        head {
//            title("확장 함수 예제")
//        }
//        body {
//            h1("코틀린의 확장 함수")
//            p("확장 함수는 기존 클래스에 새로운 기능을 추가합니다.")
//            ul {
//                li("기본 확장 함수")
//                li("확장 프로퍼티")
//                li("널 안전 확장 함수")
//            }
//        }
//    }
//
//    println("\n=== HTML DSL 결과 ===\n$html")
//
//    // 11. 실제 스프링 애플리케이션에서의 확장 함수 활용 예제 ----------------------
//    val userService = mockUserService()
//    val user = User(1, "Alice", "alice@example.com")
//
//    // 기존 서비스 메서드를 확장으로 개선
//    userService.createUserAndSendWelcomeEmail(user)
//
//    // 12. 표준 라이브러리 확장 함수 활용 ----------------------
//    val standardLibraryExamples = StandardLibraryExtensionsExample()
//    standardLibraryExamples.showExamples()
//}
//
//// 1. 기본 확장 함수 ----------------------
//fun String.addExclamation(): String {
//    return this + "!"
//}
//
//// 2. 확장 프로퍼티 ----------------------
//val String.wordCount: Int
//    get() = this.split("\\s+".toRegex()).size
//
//// 3. 널 안전 확장 함수 ----------------------
//fun String?.isNullOrShort(maxLength: Int): Boolean {
//    // this는 String?이므로 null일 수 있음
//    return this == null || this.length <= maxLength
//}
//
//// 4. 컬렉션 확장 함수 ----------------------
//fun List<Int>.evenNumbers(): List<Int> {
//    return this.filter { it % 2 == 0 }
//}
//
//fun List<Int>.oddNumbers(): List<Int> {
//    return this.filter { it % 2 != 0 }
//}
//
//// 5. 중위 확장 함수 ----------------------
//infix fun Int.multiplyBy(other: Int): Int {
//    return this * other
//}
//
//// 6. 연산자 확장 함수 ----------------------
//operator fun Int.times(str: String): String {
//    return str.repeat(this)
//}
//
//// 7. 수신 객체의 타입별 확장 함수 ----------------------
//fun Any.typeInfo(): String {
//    return when (this) {
//        is String -> "This is a String: $this"
//        is Int -> "This is an Int: $this"
//        is Boolean -> "This is a Boolean: $this"
//        else -> "Unknown type: $this"
//    }
//}
//
//// 8. 제네릭 확장 함수 ----------------------
//fun <T> List<T>.firstOrDefault(defaultValue: T): T {
//    return if (this.isEmpty()) defaultValue else this.first()
//}
//
//// 9. 확장 함수와 상속 ----------------------
//open class Base
//class Derived : Base()
//
//fun Base.printFunctionInfo() {
//    println("Base 클래스의 확장 함수")
//}
//
//fun Derived.printFunctionInfo() {
//    println("Derived 클래스의 확장 함수")
//}
//
//// 10. DSL을 위한 확장 함수 ----------------------
//// HTML 빌더 DSL 클래스들
//class HTML {
//    private val content = StringBuilder()
//
//    fun head(init: Head.() -> Unit) {
//        val head = Head()
//        head.init()
//        content.append("<head>\n${head.content}\n</head>\n")
//    }
//
//    fun body(init: Body.() -> Unit) {
//        val body = Body()
//        body.init()
//        content.append("<body>\n${body.content}\n</body>\n")
//    }
//
//    override fun toString(): String = "<!DOCTYPE html>\n<html>\n$content</html>"
//}
//
//class Head {
//    val content = StringBuilder()
//
//    fun title(text: String) {
//        content.append("  <title>$text</title>\n")
//    }
//}
//
//class Body {
//    val content = StringBuilder()
//
//    fun h1(text: String) {
//        content.append("  <h1>$text</h1>\n")
//    }
//
//    fun p(text: String) {
//        content.append("  <p>$text</p>\n")
//    }
//
//    fun ul(init: UL.() -> Unit) {
//        val ul = UL()
//        ul.init()
//        content.append("  <ul>\n${ul.content}  </ul>\n")
//    }
//}
//
//class UL {
//    val content = StringBuilder()
//
//    fun li(text: String) {
//        content.append("    <li>$text</li>\n")
//    }
//}
//
//// DSL 시작 함수
//fun buildHtml(init: HTML.() -> Unit): HTML {
//    val html = HTML()
//    html.init()
//    return html
//}
//
//// 11. 스프링 애플리케이션 확장 함수 예제 ----------------------
//// 모의 서비스 클래스
//class UserService {
//    fun createUser(user: User): User {
//        println("사용자 생성: $user")
//        return user
//    }
//
//    fun sendEmail(email: String, subject: String, content: String) {
//        println("이메일 전송: 대상=$email, 제목=$subject")
//    }
//}
//
//// 모델 클래스
//data class User(val id: Int, val name: String, val email: String)
//
//// 서비스 확장 함수
//fun UserService.createUserAndSendWelcomeEmail(user: User): User {
//    val createdUser = this.createUser(user)
//    this.sendEmail(
//        user.email,
//        "환영합니다 ${user.name}님!",
//        "코틀린 애플리케이션에 가입해주셔서 감사합니다."
//    )
//    return createdUser
//}
//
//// 모의 서비스 생성 함수
//fun mockUserService(): UserService {
//    return UserService()
//}
//
//// 12. 코틀린 표준 라이브러리 확장 함수 예제 ----------------------
//class StandardLibraryExtensionsExample {
//    fun showExamples() {
//        println("\n=== 코틀린 표준 라이브러리 확장 함수 예제 ===")
//
//        // 문자열 확장 함수
//        val input = "  Hello, Kotlin!  "
//        println("trimmed: '${input.trim()}'")
//        println("uppercase: '${input.uppercase()}'")
//        println("lowercase: '${input.lowercase()}'")
//        println("with padding: '${input.padStart(25, '*')}'")
//
//        // 컬렉션 확장 함수
//        val items = listOf("apple", "banana", "orange", "kiwi", "grape")
//        println("first: ${items.first()}")
//        println("last: ${items.last()}")
//        println("filtered by length > 5: ${items.filter { it.length > 5 }}")
//        println("sorted: ${items.sorted()}")
//        println("contains 'kiwi': ${items.contains("kiwi")}")
//
//        // map, flatMap 확장 함수
//        val numbers = listOf(1, 2, 3, 4, 5)
//        println("map squares: ${numbers.map { it * it }}")
//
//        val nested = listOf(listOf(1, 2, 3), listOf(4, 5, 6), listOf(7, 8, 9))
//        println("flattened: ${nested.flatten()}")
//        println("flatMapped: ${nested.flatMap { it.map { n -> n * 2 } }}")
//
//        // 변환 함수
//        val parsedInt = "42".toIntOrNull() ?: 0
//        println("parsed int: $parsedInt")
//
//        // 파일 관련 확장 함수
//        val path = "example/path/to/file.txt"
//        println("file extension: ${path.substringAfterLast(".")}")
//        println("file name: ${path.substringAfterLast("/").substringBeforeLast(".")}")
//        println("directory path: ${path.substringBeforeLast("/")}")
//
//        // apply, also, let, run, with 함수
//        val sb = StringBuilder().apply {
//            append("Hello")
//            append(", ")
//            append("Kotlin")
//            append("!")
//        }
//        println("StringBuilder with apply: $sb")
//
//        val text = sb.toString().also {
//            println("String length is ${it.length}")
//        }
//
//        val uppercase = text.let {
//            "${it.uppercase()} (length: ${it.length})"
//        }
//        println("After let: $uppercase")
//
//        val result = with(text) {
//            "${this.uppercase()} (length: ${this.length})"
//        }
//        println("With result: $result")
//
//        val runResult = text.run {
//            "${this.uppercase()} (length: ${this.length})"
//        }
//        println("Run result: $runResult")
//    }
//}
//
//// 13. 확장 함수의 주의사항과 제한 ----------------------
//
//// 다른 파일에 확장 함수 정의하기 위한 예제
//// 실제로는 다른 파일에 작성해야 하나, 여기서는 주석으로 설명
///*
//// StringExtensions.kt 파일
//package com.example.extensions
//
//fun String.removeSpaces(): String {
//    return this.replace(" ", "")
//}
//
//// 사용할 때
//import com.example.extensions.removeSpaces
//
//val text = "Hello World"
//text.removeSpaces() // "HelloWorld"
//*/
//
//// 확장 함수는 정적 디스패치된다는 점 유의 (위의 9번 예제 참조)
//
//// 내부 멤버에 접근 제한 (private/protected 멤버에 접근 불가)
//class RestrictedAccess {
//    private val secret = "This is private"
//    protected val protectedValue = "This is protected"
//    val public = "This is public"
//}
//
//fun RestrictedAccess.accessMembers() {
//    // println(this.secret) // 컴파일 오류: private 멤버에 접근 불가
//    // println(this.protectedValue) // 컴파일 오류: protected 멤버에 접근 불가
//    println(this.public) // OK: public 멤버에만 접근 가능
//}
//
//// 클래스 내부에 정의된 동일한 이름의 멤버 함수가 항상 우선함
//class Conflict {
//    fun foo() = "멤버 함수"
//}
//
//fun Conflict.foo() = "확장 함수" // 멤버 함수와 이름이 같음
//
//// 확장 리시버와 디스패치 리시버 (확장 함수 내에서 다른 확장 함수 호출)
//class ExtensionReceiver {
//    fun originalMethod() = "원본 메서드"
//}
//
//fun ExtensionReceiver.firstExtension() = "첫 번째 확장: ${this.originalMethod()}"
//fun ExtensionReceiver.secondExtension() = "두 번째 확장: ${this.firstExtension()}"
