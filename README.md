# SMS Backend

Spring Boot REST API for the Student Management System. Handles authentication, student and course management, enrollment logic, and serves the React frontend.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3, Spring MVC |
| Security | Spring Security, BCrypt |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8 |
| Mapping | ModelMapper |
| Build | Maven |
| Container | Docker (multi-stage build) |
| Runtime | Java 21 (Eclipse Temurin) |

---

## Project Structure

```
src/main/java/com/cwm/studentmanagement/
├── config/
│   ├── SpringConfig.java         # Security filter chain, CORS, BCrypt bean
│   ├── ModelMapperConfig.java    # ModelMapper bean
│   └── DataInitializer.java      # Seeds default Admin user on startup
├── controller/
│   ├── StudentController.java
│   ├── CourseController.java
│   └── EnrollmentController.java
├── service/
│   ├── StudentService.java       # Interface
│   ├── CourseService.java
│   ├── EnrollmentService.java
│   └── impl/
│       ├── StudentServiceImpl.java
│       ├── CourseServiceImpl.java
│       ├── EnrollmentServiceImpl.java
│       └── UserServiceImpl.java   # UserDetailsService for Spring Security
├── repository/
│   ├── StudentRepository.java
│   ├── CourseRepository.java
│   ├── EnrollmentRepository.java
│   └── UsersRepository.java
├── model/
│   ├── Students.java
│   ├── Courses.java
│   ├── Enrollment.java
│   └── Users.java
├── dto/
│   ├── StudentDTO.java
│   ├── CourseDTO.java
│   ├── EnrollmentDTO.java
│   └── EnrollmentSummaryDTO.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

## API Reference

### Auth

| Method | Path | Description |
|---|---|---|
| POST | `/login` | Form login (`application/x-www-form-urlencoded`). Returns `{"success":true}` on success, 401 on failure |
| POST | `/logout` | Invalidates the session. Returns `{"success":true}` |

All `/api/**` endpoints require an active session. Unauthenticated requests return `401 Unauthorized` (no redirect).

---

### Students — `/api/students`

| Method | Path | Description |
|---|---|---|
| GET | `/api/students?page=0&size=8` | Paginated list of active students, sorted by `id DESC` |
| GET | `/api/students/all` | Flat list of all active students (used for dropdowns) |
| GET | `/api/students/{id}` | Single student by ID |
| POST | `/api/students` | Create student. Returns `400` if email already exists |
| PUT | `/api/students/{id}` | Update student. Returns `400` if email conflicts with another record |
| DELETE | `/api/students/{id}` | Delete student and all their enrollment records (cascade) |

**Request / Response body (`StudentDTO`):**
```json
{
  "id": 1,
  "firstName": "Zhanel",
  "lastName": "Usonkulova",
  "email": "zhanel@example.com",
  "phoneNumber": "0502040883",
  "address": "Bishkek",
  "active": true
}
```

---

### Courses — `/api/courses`

| Method | Path | Description |
|---|---|---|
| GET | `/api/courses?page=0&size=8` | Paginated list of active courses, sorted by `id DESC` |
| GET | `/api/courses/all` | Flat list of all active courses, sorted by `courseName` |
| GET | `/api/courses/{id}` | Single course by ID |
| POST | `/api/courses` | Create course. Returns `400` if `courseCode` already exists (case-insensitive) |
| PUT | `/api/courses/{id}` | Update course. Returns `400` if `courseCode` conflicts with another record |

**Request / Response body (`CourseDTO`):**
```json
{
  "id": 1,
  "courseName": "Web Development",
  "courseCode": "WEB101",
  "duration": "6 Months",
  "fee": 1200.00,
  "description": "Full-stack web development course.",
  "active": true
}
```

---

### Enrollments — `/api/enrollments`

| Method | Path | Description |
|---|---|---|
| GET | `/api/enrollments?page=0&size=8` | Paginated list of students who have at least one enrollment |
| GET | `/api/enrollments/{studentId}/details` | Full enrollment detail for one student including course list |
| POST | `/api/enrollments` | Enroll a student in one or more courses. Already-enrolled courses are silently skipped |

**Enroll request (`EnrollmentDTO`):**
```json
{
  "studentId": 1,
  "courseIds": [2, 5, 8]
}
```

**Summary response (`EnrollmentSummaryDTO`):**
```json
{
  "studentId": 1,
  "studentName": "Zhanel Usonkulova",
  "email": "zhanel@example.com",
  "courseCount": 3,
  "totalFee": 3600.00,
  "courseList": [ ... ]
}
```

---

## Security

Configured in `SpringConfig.java`:

- **Form login** at `POST /login` — no server-side login page, custom JSON success/failure handlers.
- **Session cookies** — Spring's default `JSESSIONID` cookie is used. Sessions are tied to the browser tab on the frontend via `sessionStorage`.
- **CSRF** — disabled for `/api/**`, `/login`, and `/logout` (SPA pattern).
- **CORS** — all origins, methods, and headers are allowed; credentials are enabled. Restrict this in production as needed.
- **Password encoding** — BCrypt via `BCryptPasswordEncoder`.
- **Entry point** — unauthenticated requests to `/api/**` return `401` instead of a redirect.

---

## Data Models

### Students
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `firstName` | VARCHAR | Required |
| `lastName` | VARCHAR | Required |
| `email` | VARCHAR | Required, unique |
| `phoneNumber` | VARCHAR | Optional |
| `address` | VARCHAR(500) | Optional |
| `active` | BOOLEAN | Default `true`, set on `@PrePersist` |
| `createdAt` | DATETIME | Set on `@PrePersist`, immutable |

Cascade: deleting a student removes all their `Enrollment` rows via `CascadeType.ALL + orphanRemoval`.

### Courses
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `courseName` | VARCHAR(150) | Required |
| `courseCode` | VARCHAR | Required, unique |
| `duration` | VARCHAR | Required |
| `fee` | DECIMAL(12,2) | Required |
| `description` | VARCHAR(1000) | Optional |
| `active` | BOOLEAN | Default `true` |
| `createdAt` | DATETIME | Immutable |

### Enrollment
Join table between `Students` and `Courses`.

| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `student_id` | FK → students | Required |
| `course_id` | FK → courses | Required |
| `enrolledDate` | DATETIME | Set at construction |

### Users
| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `username` | VARCHAR | Required, unique |
| `password` | VARCHAR | BCrypt-hashed |
| `active` | BOOLEAN | Controls `UserDetails.isEnabled()` |

---

## Query Design

Two custom JPQL queries in `StudentRepository` handle enrollment data efficiently:

**`findEnrolledStudents`** — uses `@EntityGraph` to eagerly fetch `enrollments` and `enrollments.course` in a single query. Only returns students with at least one enrollment. Used for the paginated enrollments list.

**`findEnrolledStudentCourseDetails`** — `JOIN FETCH` query that loads a single student with all their enrolled courses in one round-trip. Used for the enrollment detail modal.

---

## Exception Handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) catches:

- `NoResourceFoundException` → `404` (suppressed from logs — not an application error)
- `RuntimeException` → `400` with `{"message": "..."}`
- `Exception` → `500` with a generic message

---

## Configuration Profiles

### Development (`application.properties`)
- MySQL on `localhost:3306`
- `ddl-auto=update`
- SQL logging off

### Production (`application-prod.properties`, activated via `-Dspring.profiles.active=prod`)
- PostgreSQL on Render
- Static files served from `classpath:/static/` (React build output)
- Port from `$PORT` env variable (Render injects this automatically)

---

## Default Admin Account

Created automatically on first startup by `DataInitializer` if no `Admin` user exists:

```
Username: Admin
Password: admin@123
```

---

## Running Locally

### Prerequisites
- Java 21
- Maven 3.9+
- MySQL 8 running on `localhost:3306`

### Steps

```bash
# 1. Create the database (or let Spring create it automatically)
mysql -u root -p -e "CREATE DATABASE student_mgmt_db;"

# 2. Update credentials in src/main/resources/application.properties if needed

# 3. Run
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`.

---

## Docker

The `Dockerfile` uses a two-stage build:

1. **Builder stage** — `maven:3.9.6-eclipse-temurin-21` downloads dependencies and packages the JAR.
2. **Runtime stage** — `eclipse-temurin:21-jre-alpine` runs the JAR with the `prod` profile active.

```bash
# Build image
docker build -t sms-backend .

# Run (provide DB connection via environment variables or mount a properties file)
docker run -p 8080:8080 sms-backend
```

---

## Logging

Configured via `logback-spring.xml`:

- **Console** — all environments
- **Rolling file** — daily rotation, max 10 MB per file, 30-day retention, written to `C:/logs/studentmanagement.log`
- Root level: `INFO`
