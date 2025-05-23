# 코틀린 학습 가이드 (Java 개발자를 위한)

이 가이드는 Java 스프링 개발자가 Kotlin을 배우는 데 필요한 핵심 개념과 문법을 다룹니다.

## 목차

1. [기본 문법](01_BasicSyntax.kt) - 변수, 함수, 조건문, 반복문 등
2. [클래스와 객체](02_ClassesAndObjects.kt) - 클래스, 생성자, 프로퍼티, data class 등
3. [함수형 프로그래밍](03_FunctionalProgramming.kt) - 람다, 고차 함수, 컬렉션 API 등
4. [Null 안전성](04_NullSafety.kt) - Null 처리, 안전 호출, Elvis 연산자 등
5. [코루틴](05_Coroutines.kt) - 기본 코루틴 사용법과 비동기 프로그래밍
6. [확장 함수](06_Extensions.kt) - 클래스 확장 및 유틸리티 함수
7. [스프링과 코틀린](07_KotlinWithSpring.md) - 스프링 프레임워크에서 코틀린 사용하기

## Java vs Kotlin 주요 차이점

| 기능 | Java | Kotlin |
|------|------|--------|
| 변수 선언 | `String name = "Java";` | `val name: String = "Kotlin"` 또는 `var name = "Kotlin"` |
| Null 처리 | `@Nullable`, `Optional` | nullable 타입 (`String?`)과 안전 호출 연산자(`?.`) |
| 세미콜론 | 필수 | 선택적 |
| 동등성 비교 | `equals()` 및 `==` | `==`는 구조적 동등성, `===`는 참조 동등성 |
| 타입 캐스팅 | 명시적 캐스팅 필요 | 스마트 캐스트 및 안전한 캐스팅 연산자 (`as?`) |
| Getter/Setter | 명시적으로 작성 | 프로퍼티로 자동 생성 |
| 함수형 특징 | Java 8+ 람다 | 일급 시민으로서의 함수, 확장 함수 |
| Switch | switch 문 | when 표현식 (더 강력) |
| Data 클래스 | 수동 구현 필요 | `data class` 키워드 |
| 상속 | 기본적으로 열려있음 | 기본적으로 닫혀있음 (`open` 키워드 필요) |
| 코루틴 | CompletableFuture | 내장 코루틴 지원 |

## 추천 학습 자료

- [Kotlin 공식 문서](https://kotlinlang.org/docs/home.html)
- [Kotlin Koans](https://play.kotlinlang.org/koans/overview) - 인터랙티브 연습
- [Spring 공식 Kotlin 가이드](https://spring.io/guides/tutorials/spring-boot-kotlin/)

이 저장소의 각 파일은 실제 실행 가능한 코드와 상세한 주석을 포함하고 있어 실습으로 학습할 수 있습니다.
