package kotlinstudy

/**
 * 코틀린의 클래스와 객체 (Java 개발자를 위한)
 * 
 * 이 파일은 Java 개발자가 Kotlin으로 전환할 때 알아야 할 클래스와 객체 관련 문법을 다룹니다.
 */

fun main() {
    // 1. 기본 클래스 인스턴스화
    val person = Person("John", 30)
    println(person.name)      // getter 자동 호출
    person.age = 31           // setter 자동 호출
    // 2. 데이터 클래스 사용
    val user1 = User("Alice", 25)
    val user2 = User("Alice", 25)
    
    // equals(), hashCode(), toString() 자동 구현
    println(user1 == user2)  // true (구조적 동등성)
    println(user1)           // User(name=Alice, age=25)
    
    // copy() 함수 사용
    val user3 = user1.copy(age = 26)
    println(user3)           // User(name=Alice, age=26)
    
    // 구조 분해 선언
    val (name, age) = user1
    println("이름: $name, 나이: $age")
    
    // 3. 싱글톤 객체 사용
    println(MySingleton.count)
    MySingleton.incrementCount()
    println(MySingleton.count)
    
    // 4. 컴패니언 객체 사용
    val newUser = User.createUser("Bob")
    println(newUser)
    
    // 5. 상속 예제
    val employee = Employee("James", 35, "Developer")
    employee.introduceYourself()
    
    // 6. 인터페이스 구현 클래스 사용
    val circle = Circle(5.0)
    println("원의 넓이: ${circle.area()}")
    println("원의 둘레: ${circle.perimeter()}")
}

// 1. 기본 클래스 ------------------------

// 주 생성자가 있는 클래스 (Java의 필드, getter, setter 자동 생성)
class Person(
    val name: String,      // val: 읽기 전용 프로퍼티 (final, getter만 생성)
    var age: Int           // var: 변경 가능 프로퍼티 (getter와 setter 생성)
) {
    // 초기화 블록
    init {
        println("Person 객체 생성: $name, $age")
    }
    
    // 보조 생성자 (다른 시그니처의 생성자)
    constructor(name: String) : this(name, 0) {
        println("보조 생성자 호출됨")
    }
    
    // 커스텀 getter와 setter
    var isAdult: Boolean
        get() = age >= 18
        set(value) {
            println("isAdult 속성은 직접 설정할 수 없습니다.")
        }
    
    // 메소드
    fun introduceYourself() {
        println("안녕하세요, 제 이름은 `$name`이고, 나이는 `$age`입니다.")
    }
}

// 2. 데이터 클래스 ------------------------
// equals(), hashCode(), toString(), copy() 등 자동 생성
data class User(
    val name: String,
    val age: Int
) {
    // 컴패니언 객체 (Java의 static 멤버와 유사)
    companion object {
        private const val DEFAULT_AGE = 18
        
        // 팩토리 메소드
        fun createUser(name: String): User {
            return User(name, DEFAULT_AGE)
        }
    }
}

// 3. 싱글톤 객체 ------------------------
// Java의 싱글톤 패턴을 간단하게 구현
object MySingleton {
    var count = 0
        private set  // 외부에서 setter 접근 제한
    
    fun incrementCount() {
        count++
    }
}

// 4. 상속 ------------------------
// 코틀린에서는 클래스가 기본적으로 final이므로, 상속 가능하게 하려면 open 키워드 필요
open class Animal(open val name: String) {
    open fun makeSound() {
        println("Some generic sound")
    }
}

// 상속 구현 (': 부모클래스' 형태)
class Dog(override val name: String) : Animal(name) {
    override fun makeSound() {
        println("Woof!")
    }
}

// 상속과 주 생성자 사용
open class Person2(val name: String, var age: Int) {
    open fun introduceYourself() {
        println("안녕하세요, 제 이름은 `$name`이고, 나이는 `$age`입니다.")
    }
}

class Employee(
    name: String, 
    age: Int, 
    val jobTitle: String
) : Person2(name, age) {
    
    override fun introduceYourself() {
        super.introduceYourself()
        println("직업은 `$jobTitle`입니다.")
    }
}

// 5. 인터페이스 ------------------------
interface Shape {
    fun area(): Double
    fun perimeter(): Double
    
    // 기본 구현이 있는 메소드
    fun description(): String {
        return "이것은 도형입니다."
    }
}

// 인터페이스 구현
class Circle(private val radius: Double) : Shape {
    override fun area(): Double = Math.PI * radius * radius
    
    override fun perimeter(): Double = 2 * Math.PI * radius
    
    // description()은 재정의하지 않아도 됨
}

// 6. 추상 클래스 ------------------------
abstract class AbstractShape {
    abstract fun area(): Double
    
    // 추상 클래스는 상태를 가질 수 있음
    var name: String = "도형"
    
    // 비추상 메소드
    fun display() {
        println("`$name`의 넓이: ${area()}")
    }
}

// 7. 중첩 및 내부 클래스 ------------------------
class Outer {
    private val outerValue = 10
    
    // 중첩 클래스 (Java의 static 중첩 클래스와 유사)
    class Nested {
        fun nestedFunc() {
            // outerValue에 접근 불가
        }
    }
    
    // 내부 클래스 (Java와 다르게 명시적으로 inner 키워드 필요)
    inner class Inner {
        fun innerFunc() {
            // 외부 클래스 멤버에 접근 가능
            println(outerValue)
            // this@Outer로 외부 클래스 인스턴스 참조 가능
        }
    }
}

// 8. Sealed 클래스 ------------------------
// 제한된 계층 구조를 위한 클래스 (같은 파일 내에서만 하위 클래스 정의 가능)
sealed class Result {
    data class Success(val data: String) : Result()
    data class Error(val message: String) : Result()
    object Loading : Result()
}

fun handleResult(result: Result) {
    when(result) {
        is Result.Success -> println("성공: ${result.data}")
        is Result.Error -> println("오류: ${result.message}")
        Result.Loading -> println("로딩 중...")
        // 모든 케이스를 다루므로 else 불필요
    }
}

// 9. Enum 클래스 ------------------------
enum class Direction {
    NORTH, EAST, SOUTH, WEST
}

// 프로퍼티와 메소드가 있는 Enum
enum class HttpStatus(val code: Int) {
    OK(200),
    NOT_FOUND(404),
    INTERNAL_SERVER_ERROR(500);
    
    fun isSuccess(): Boolean = code < 400
}
