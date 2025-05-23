# 스프링과 코틀린 통합 가이드

이 문서에서는 Java로 Spring 애플리케이션을 개발하던 개발자가 Kotlin으로 전환할 때 알아두면 좋은 내용을 다룹니다.

## 목차

1. [스프링 부트와 코틀린 설정](#1-스프링-부트와-코틀린-설정)
2. [코틀린 Spring Boot 프로젝트 구조](#2-코틀린-spring-boot-프로젝트-구조)
3. [Spring의 주요 기능과 코틀린](#3-spring의-주요-기능과-코틀린)
4. [Spring Data와 코틀린](#4-spring-data와-코틀린)
5. [Spring Security와 코틀린](#5-spring-security와-코틀린)
6. [코틀린 DSL과 스프링 통합](#6-코틀린-dsl과-스프링-통합)
7. [코루틴과 Spring WebFlux 통합](#7-코루틴과-spring-webflux-통합)
8. [테스트 작성](#8-테스트-작성)
9. [모범 사례](#9-모범-사례)
10. [마이그레이션 전략](#10-마이그레이션-전략)

## 1. 스프링 부트와 코틀린 설정

### 기본 의존성 설정 (Gradle)

```kotlin
plugins {
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.9.20"
    kotlin("plugin.jpa") version "1.9.20" // JPA를 사용하는 경우
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    
    // 코루틴 사용 시
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor") // WebFlux 사용 시
    
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

### 코틀린 컴파일러 옵션

```kotlin
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict") // JSR-305 어노테이션 활성화 (Null 안전성)
        jvmTarget = "17" // Java 버전
    }
}
```

### 주요 플러그인 설명

- `kotlin-spring`: Spring의 final 클래스를 자동으로 열어줘서 프록시 생성 가능하게 함
- `kotlin-jpa`: 기본 생성자가 없는 코틀린 클래스에 no-arg 생성자를 자동 생성 (JPA 엔티티용)
- `kotlin-allopen`: 특정 어노테이션이 붙은 클래스를 자동으로 open으로 만들어줌

## 2. 코틀린 Spring Boot 프로젝트 구조

일반적인 프로젝트 구조는 Java와 동일하게 유지할 수 있습니다:

```
src/
├── main/
│   ├── kotlin/
│   │   └── com/
│   │       └── example/
│   │           └── demo/
│   │               ├── DemoApplication.kt
│   │               ├── controller/
│   │               ├── service/
│   │               ├── repository/
│   │               └── model/
│   └── resources/
│       └── application.yml
└── test/
    └── kotlin/
        └── com/
            └── example/
                └── demo/
```

### 애플리케이션 진입점

```kotlin
@SpringBootApplication
class DemoApplication

fun main(args: Array<String>) {
    runApplication<DemoApplication>(*args)
}
```

## 3. Spring의 주요 기능과 코틀린

### 컨트롤러 작성

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    fun getAllUsers(): List<UserDto> = userService.getAllUsers()

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): UserDto = 
        userService.getUserById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createUser(@RequestBody @Valid userDto: UserDto): UserDto = 
        userService.createUser(userDto)

    @PutMapping("/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody @Valid userDto: UserDto): UserDto =
        userService.updateUser(id, userDto) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteUser(@PathVariable id: Long) {
        if (!userService.deleteUser(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND)
        }
    }
}
```

### 서비스 작성

```kotlin
@Service
class UserService(private val userRepository: UserRepository) {

    fun getAllUsers(): List<UserDto> =
        userRepository.findAll().map { it.toDto() }

    fun getUserById(id: Long): UserDto? =
        userRepository.findById(id).orElse(null)?.toDto()

    fun createUser(userDto: UserDto): UserDto {
        // 중복 검사 예시 
        if (userRepository.existsByEmail(userDto.email)) {
            throw IllegalArgumentException("Email already exists")
        }
        
        val savedUser = userRepository.save(userDto.toEntity())
        return savedUser.toDto()
    }

    fun updateUser(id: Long, userDto: UserDto): UserDto? {
        return userRepository.findById(id).orElse(null)?.let { user ->
            val updatedUser = user.copy(
                name = userDto.name,
                email = userDto.email
            )
            userRepository.save(updatedUser).toDto()
        }
    }

    fun deleteUser(id: Long): Boolean {
        return if (userRepository.existsById(id)) {
            userRepository.deleteById(id)
            true
        } else {
            false
        }
    }
}
```

### 의존성 주입

코틀린에서는 생성자 매개변수에 `val/var`를 붙이면 자동으로 프로퍼티가 됩니다:

```kotlin
// Java 스타일
@Service
class UserService {
    private final UserRepository userRepository;
    
    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}

// 코틀린 스타일 (간결함)
@Service
class UserService(private val userRepository: UserRepository)
```

## 4. Spring Data와 코틀린

### JPA 엔티티

```kotlin
@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    var name: String,
    
    @Column(unique = true)
    var email: String,
    
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL], orphanRemoval = true)
    val posts: MutableList<Post> = mutableListOf()
)
```

### Data Class를 활용한 DTO

```kotlin
data class UserDto(
    val id: Long? = null,
    val name: String,
    val email: String
)

// 확장 함수를 사용한 변환 유틸리티
fun User.toDto() = UserDto(
    id = this.id,
    name = this.name,
    email = this.email
)

fun UserDto.toEntity() = User(
    id = this.id,
    name = this.name,
    email = this.email
)
```

### 리포지토리

```kotlin
interface UserRepository : JpaRepository<User, Long> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}
```

## 5. Spring Security와 코틀린

### 보안 설정

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig(private val userDetailsService: UserDetailsService) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .authorizeHttpRequests { authorize ->
                authorize
                    .requestMatchers("/api/public/**").permitAll()
                    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                    .anyRequest().authenticated()
            }
            .httpBasic(Customizer.withDefaults())
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        
        return http.build()
    }
    
    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
```

### UserDetailsService 구현

```kotlin
@Service
class CustomUserDetailsService(private val userRepository: UserRepository) : UserDetailsService {

    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByEmail(username)
            ?: throw UsernameNotFoundException("User not found with email: $username")
        
        return User.builder()
            .username(user.email)
            .password(user.password)
            .roles(user.roles.split(",").toTypedArray())
            .build()
    }
}
```

## 6. 코틀린 DSL과 스프링 통합

### Spring Security DSL

Spring Security는 코틀린 DSL을 지원합니다:

```kotlin
@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http {
            csrf { disable() }
            authorizeRequests {
                authorize("/api/public/**", permitAll)
                authorize("/api/admin/**", hasRole("ADMIN"))
                authorize(anyRequest, authenticated)
            }
            httpBasic {}
            sessionManagement {
                sessionCreationPolicy = SessionCreationPolicy.STATELESS
            }
        }
        return http.build()
    }
}
```

### Bean 정의 DSL

Spring Fu, Spring Boot Kotlin DSL 등을 사용하면 더 함수형으로 빈을 정의할 수 있습니다:

```kotlin
@Configuration
class AppConfig {

    @Bean
    fun beans() = beans {
        bean<UserService>()
        bean<AuthService>()
        bean {
            RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .build()
        }
    }
}
```

## 7. 코루틴과 Spring WebFlux 통합

### 코루틴 컨트롤러

```kotlin
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {

    @GetMapping
    suspend fun getAllUsers(): Flow<UserDto> =
        userService.getAllUsersAsFlow()

    @GetMapping("/{id}")
    suspend fun getUserById(@PathVariable id: Long): UserDto =
        userService.getUserById(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @PostMapping
    suspend fun createUser(@RequestBody userDto: UserDto): UserDto =
        userService.createUser(userDto)
}
```

### 코루틴 서비스

```kotlin
@Service
class UserService(private val userRepository: ReactiveUserRepository) {

    suspend fun getUserById(id: Long): UserDto? =
        userRepository.findById(id).awaitFirstOrNull()?.toDto()

    fun getAllUsersAsFlow(): Flow<UserDto> =
        userRepository.findAll()
            .asFlow()
            .map { it.toDto() }

    suspend fun createUser(userDto: UserDto): UserDto {
        val exists = userRepository.existsByEmail(userDto.email).awaitSingle()
        if (exists) {
            throw IllegalArgumentException("Email already exists")
        }
        
        val user = userDto.toEntity()
        return userRepository.save(user).awaitSingle().toDto()
    }
}
```

### 코루틴 확장 함수

```kotlin
// Mono 확장 함수
suspend fun <T> Mono<T>.awaitSingle(): T = 
    suspendCancellableCoroutine { cont ->
        val disposable = subscribe(
            { cont.resume(it) },
            { cont.resumeWithException(it) }
        )
        cont.invokeOnCancellation { disposable.dispose() }
    }

// Mono 확장 함수
suspend fun <T> Mono<T>.awaitFirstOrNull(): T? = 
    suspendCancellableCoroutine { cont ->
        val disposable = subscribe(
            { cont.resume(it) },
            { cont.resumeWithException(it) },
            { cont.resume(null) }
        )
        cont.invokeOnCancellation { disposable.dispose() }
    }
```

## 8. 테스트 작성

### 단위 테스트

```kotlin
@ExtendWith(MockKExtension::class)
class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    fun `should return user when getUserById is called with valid id`() {
        // given
        val userId = 1L
        val user = User(id = userId, name = "John", email = "john@example.com")
        every { userRepository.findById(userId) } returns Optional.of(user)

        // when
        val result = userService.getUserById(userId)

        // then
        assertNotNull(result)
        assertEquals(userId, result?.id)
        assertEquals("John", result?.name)
        assertEquals("john@example.com", result?.email)
    }

    @Test
    fun `should return null when getUserById is called with invalid id`() {
        // given
        val userId = 999L
        every { userRepository.findById(userId) } returns Optional.empty()

        // when
        val result = userService.getUserById(userId)

        // then
        assertNull(result)
    }
}
```

### 통합 테스트

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `should create new user`() {
        // given
        val userDto = UserDto(name = "John Doe", email = "john.doe@example.com")
        val request = MockMvcRequestBuilders.post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto))

        // when & then
        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("John Doe"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("john.doe@example.com"))
    }
}
```

### 코루틴 테스트

```kotlin
@ExtendWith(MockKExtension::class)
class UserServiceCoroutineTest {

    @MockK
    lateinit var userRepository: ReactiveUserRepository

    @InjectMockKs
    lateinit var userService: UserService

    @Test
    fun `should return user when getUserById is called with valid id`() = runBlocking {
        // given
        val userId = 1L
        val user = User(id = userId, name = "John", email = "john@example.com")
        coEvery { userRepository.findById(userId) } returns Mono.just(user)

        // when
        val result = userService.getUserById(userId)

        // then
        assertNotNull(result)
        assertEquals(userId, result?.id)
        assertEquals("John", result?.name)
        
        coVerify { userRepository.findById(userId) }
    }
}
```

## 9. 모범 사례

### 불변성 최대화

```kotlin
// 가변 상태를 최소화하고 불변 객체 사용
data class UserDto(
    val id: Long? = null,
    val name: String,
    val email: String
)

// 상태 변경 시 복사를 통한 새 객체 생성
fun updateUser(user: UserDto, newName: String): UserDto {
    return user.copy(name = newName)
}
```

### 확장 함수 활용

```kotlin
// 유틸리티 메서드를 확장 함수로 구현
fun String.toSlug() = this.lowercase()
    .replace(Regex("[^a-z0-9\\s-]"), "")
    .replace(Regex("\\s+"), "-")

// 사용
val slug = "Hello World!".toSlug() // "hello-world"

// 스프링 클래스 확장
fun ResponseEntity<*>.withCorsHeaders(): ResponseEntity<*> {
    return ResponseEntity.status(this.statusCode)
        .headers { headers ->
            headers.add("Access-Control-Allow-Origin", "*")
            this.headers.forEach { name, values ->
                headers.addAll(name, values)
            }
        }
        .body(this.body)
}
```

### 함수형 프로그래밍 스타일

```kotlin
// 명령형 스타일
fun processUsers(users: List<User>): List<UserDto> {
    val result = mutableListOf<UserDto>()
    for (user in users) {
        if (user.isActive) {
            val dto = UserDto(
                id = user.id,
                name = user.name,
                email = user.email
            )
            result.add(dto)
        }
    }
    return result
}

// 함수형 스타일
fun processUsers(users: List<User>): List<UserDto> =
    users.filter { it.isActive }
        .map { user ->
            UserDto(
                id = user.id,
                name = user.name,
                email = user.email
            )
        }
```

### null 안전성

```kotlin
// null 변수는 항상 명시적으로 처리
fun getUser(id: Long?): User? {
    if (id == null) return null
    
    return userRepository.findById(id).orElse(null)
}

// 안전 호출 연산자와 엘비스 연산자 활용
fun getUserName(id: Long?): String {
    return userRepository.findById(id).orElse(null)?.name ?: "Unknown"
}
```

## 10. 마이그레이션 전략

### 점진적 마이그레이션

1. **인프라 설정**: Gradle 설정을 업데이트하고 코틀린 컴파일러 플러그인 추가
2. **새 코드는 코틀린으로 작성**: 새로운 기능이나 모듈은 코틀린으로 작성
3. **제일 쉬운 클래스부터 변환**: 모델, DTO, 유틸리티 클래스 등 상태가 거의 없는 클래스부터 시작
4. **테스트 커버리지 확보**: 마이그레이션 전에 충분한 테스트 커버리지 확보
5. **점진적 리팩토링**: 핵심 비즈니스 로직은 가장 마지막에 마이그레이션

### 자동 변환 도구 활용

- IntelliJ IDEA의 Java-to-Kotlin 변환기 (⌥⇧⌘K 또는 Alt+Shift+Ctrl+K)
- 자동 변환 후 코드 검토 및 코틀린 스타일로 리팩토링 필수

### 병행 실행

Java와 Kotlin은 완벽하게 상호 운용되므로 한 프로젝트 내에서 두 언어를 혼용할 수 있습니다:

```kotlin
// Java 클래스 사용
@Service
class KotlinService(private val javaRepository: JavaRepository) {
    fun processData(): List<KotlinDto> {
        return javaRepository.findAll()
            .map { javaEntity -> javaEntity.toKotlinDto() }
    }
}

// Kotlin 클래스 사용 (Java에서)
public class JavaService {
    private final KotlinRepository kotlinRepository;
    
    public JavaService(KotlinRepository kotlinRepository) {
        this.kotlinRepository = kotlinRepository;
    }
    
    public List<JavaDto> getData() {
        return kotlinRepository.findAll().stream()
            .map(this::convertToJavaDto)
            .collect(Collectors.toList());
    }
}
```

## 추가 자료

- [Spring 공식 Kotlin 가이드](https://spring.io/guides/tutorials/spring-boot-kotlin/)
- [Kotlin + Spring Boot 예제 프로젝트](https://github.com/spring-guides/tut-spring-boot-kotlin)
- [Spring Fu](https://github.com/spring-projects-experimental/spring-fu) - 함수형 DSL로 Spring 애플리케이션 구성 
- [코루틴 공식 문서](https://kotlinlang.org/docs/coroutines-overview.html)
- [MockK](https://mockk.io/) - 코틀린을 위한 모킹 라이브러리
