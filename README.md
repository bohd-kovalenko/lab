# JWT Authentication API

A Spring Boot application with JWT-based authentication and authorization using Spring Security, Spring Web, and Spring Data JPA.

## Features

- User registration with password encryption (BCrypt)
- JWT token generation and validation
- Stateless authentication
- Protected API endpoints
- H2 in-memory database (for development)
- Input validation

## Project Structure

```
src/main/kotlin/com/quaterion/
├── Application.kt                    # Main Spring Boot application
├── config/
│   └── SecurityConfig.kt            # Security configuration
├── controller/
│   ├── AuthController.kt            # Authentication endpoints
│   └── TestController.kt            # Protected test endpoints
├── dto/
│   ├── AuthRequest.kt               # Login/Register request DTO
│   └── AuthResponse.kt              # Authentication response DTO
├── entity/
│   └── User.kt                      # User entity
├── repository/
│   └── UserRepository.kt            # User repository
└── security/
    ├── CustomUserDetails.kt         # UserDetails implementation
    ├── CustomUserDetailsService.kt  # UserDetailsService implementation
    ├── JwtAuthenticationFilter.kt   # JWT filter for requests
    └── JwtUtil.kt                   # JWT utility for token operations
```

## How to Run

1. Build the project:
```bash
./gradlew build
```

2. Run the application:
```bash
./gradlew bootRun
```

The application will start on `http://localhost:8080`

## API Endpoints

### Public Endpoints (No Authentication Required)

#### Register a new user
```bash
POST /auth/register
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john",
  "message": "Registration successful"
}
```

#### Login
```bash
POST /auth/login
Content-Type: application/json

{
  "username": "john",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john",
  "message": "Authentication successful"
}
```

### Protected Endpoints (Require JWT Token)

#### Hello endpoint
```bash
GET /api/hello
Authorization: Bearer <your-jwt-token>
```

Response:
```json
{
  "message": "Hello, john!",
  "authorities": "ROLE_USER"
}
```

#### Protected endpoint
```bash
GET /api/protected
Authorization: Bearer <your-jwt-token>
```

Response:
```json
{
  "message": "This is a protected endpoint",
  "user": "john",
  "timestamp": "1234567890123"
}
```

## Testing with cURL

1. Register a new user:
```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

2. Login (save the token from response):
```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'
```

3. Access protected endpoint:
```bash
curl -X GET http://localhost:8080/api/hello \
  -H "Authorization: Bearer YOUR_JWT_TOKEN_HERE"
```

## Configuration

Key configurations in `application.properties`:

- **JWT Secret**: Change `jwt.secret` to a strong random string in production (minimum 256 bits)
- **JWT Expiration**: Default is 24 hours (86400000 milliseconds)
- **Database**: Currently using H2 in-memory database. Change to PostgreSQL/MySQL for production

## Security Features

1. **Password Encryption**: BCrypt hashing algorithm
2. **Stateless Sessions**: No server-side session storage
3. **CSRF Protection**: Disabled for stateless JWT authentication
4. **Token Validation**: Tokens are validated on every protected request
5. **Role-Based Authorization**: Ready for role-based access control

## Database

### H2 Console (Development Only)
Access the H2 console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: (empty)

**IMPORTANT**: Disable H2 console in production!

## Production Considerations

1. **Change JWT Secret**: Use a strong, random secret key (at least 256 bits)
2. **Use Production Database**: Replace H2 with PostgreSQL, MySQL, etc.
3. **Disable H2 Console**: Set `spring.h2.console.enabled=false`
4. **Enable HTTPS**: Use TLS/SSL for secure communication
5. **Add Rate Limiting**: Prevent brute force attacks
6. **Implement Token Refresh**: Add refresh token mechanism
7. **Add Logging**: Monitor authentication attempts
8. **Environment Variables**: Store secrets in environment variables

## Testing

The project includes comprehensive unit and integration tests covering all components.

### Running Tests

Run all tests:
```bash
./gradlew test
```

Run tests with coverage:
```bash
./gradlew test jacocoTestReport
```

### Test Coverage

The project includes:

#### Unit Tests
- **UserTest**: Tests for User entity data class functionality
- **UserRepositoryTest**: Tests for JPA repository operations
- **CustomUserDetailsTest**: Tests for Spring Security UserDetails implementation
- **CustomUserDetailsServiceTest**: Tests for loading users from database
- **JwtUtilTest**: Tests for JWT token generation and validation
- **JwtAuthenticationFilterTest**: Tests for JWT filter chain
- **SecurityConfigTest**: Tests for Spring Security configuration
- **AuthControllerTest**: Tests for authentication endpoints
- **TestControllerTest**: Tests for protected endpoints

#### Integration Tests
- **AuthenticationIntegrationTest**: End-to-end tests for complete authentication flows
  - Full registration flow
  - Full login flow
  - Register → Login → Access protected endpoint
  - Invalid credentials handling
  - Duplicate username handling
  - Input validation
  - Multiple user scenarios

### Test Structure

```
src/test/kotlin/com/quaterion/
├── config/
│   └── SecurityConfigTest.kt
├── controller/
│   ├── AuthControllerTest.kt
│   └── TestControllerTest.kt
├── entity/
│   └── UserTest.kt
├── integration/
│   └── AuthenticationIntegrationTest.kt
├── repository/
│   └── UserRepositoryTest.kt
└── security/
    ├── CustomUserDetailsServiceTest.kt
    ├── CustomUserDetailsTest.kt
    ├── JwtAuthenticationFilterTest.kt
    └── JwtUtilTest.kt
```

### Testing Technologies

- JUnit 5
- MockK (for Kotlin mocking)
- Spring Boot Test
- Spring Security Test
- MockMvc (for integration tests)
- H2 in-memory database (for repository tests)

## Technologies Used

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- JWT (io.jsonwebtoken 0.12.3)
- Kotlin 2.2.20
- H2 Database
- Gradle
- JUnit 5
- MockK 1.13.8
