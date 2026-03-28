# 🎓 Student Management System — Backend

<div align="center">

**A production-ready REST API for managing students, courses, and enrollments.**
Built with Spring Boot · Secured with Spring Security · Deployed on Render

<br/>

![Java](https://img.shields.io/badge/Java_21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)
![Render](https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)

</div>

---

## 📌 About

This is the backend of a **Student Management System** — a fullstack web application designed to help administrators manage students, courses, and enrollments in one place.

The backend is a REST API built with **Spring Boot** that handles:
- 🔐 Session-based authentication via Spring Security
- 👨‍🎓 Full CRUD for students and courses
- 📋 Enrollment logic with duplicate prevention
- 📄 Paginated and sorted data for all list endpoints
- ⚠️ Centralized error handling with proper HTTP status codes
- 🐳 Docker multi-stage build for optimized production deployment

The frontend is built with **React + Vite** and communicates with this API via session cookies.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3, Spring MVC |
| Security | Spring Security, BCrypt |
| Persistence | Spring Data JPA, Hibernate |
| Database | MySQL 8 (dev) · PostgreSQL (prod) |
| Mapping | ModelMapper |
| Build | Maven |
| Container | Docker (multi-stage build) |
| Runtime | Java 21 (Eclipse Temurin) |

---

## 📁 Project Structure

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

## 🔌 API Reference

### Auth

| Method | Path | Description |
|---|---|---|
| POST | `/login` | Form login (`application/x-www-form-urlencoded`). Returns `{"success":true}` on success, 401 on failure |
| POST | `/logout` | Invalidates the session. Returns `{"success":true}` |

> ⚠️ All `/api/**` endpoints require an active session. Unauthenticated requests return `401 Unauthorized` (no redirect).

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

<details>
<summary>📦 Request / Response body (<code>StudentDTO</code>)</summary>

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
</details>

---

### Courses — `/api/courses`

| Method | Path | Description |
|---|---|---|
| GET | `/api/courses?page=0&size=8` | Paginated list of active courses, sorted by `id DESC` |
| GET | `/api/courses/all` | Flat list of all active courses, sorted by `courseName` |
| GET | `/api/courses/{id}` | Single course by ID |
| POST | `/api/courses` | Create course. Returns `400` if `courseCode` already exists (case-insensitive) |
| PUT | `/api/courses/{id}` | Update course. Returns `400` if `courseCode` conflicts with another record |

<details>
<summary>📦 Request / Response body (<code>CourseDTO</code>)</summary>

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
</details>

---

### Enrollments — `/api/enrollments`

| Method | Path | Description |
|---|---|---|
| GET | `/api/enrollments?page=0&size=8` | Paginated list of students who have at least one enrollment |
| GET | `/api/enrollments/{studentId}/details` | Full enrollment detail for one student including course list |
| POST | `/api/enrollments` | Enroll a student in one or more courses. Already-enrolled courses are silently skipped |

<details>
<summary>📦 Request / Response bodies</summary>

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
</details>

---

## 🔐 Security

Configured in `SpringConfig.java`:

- **Form login** at `POST /login` — no server-side login page, custom JSON success/failure handlers
- **Session cookies** — Spring's default `JSESSIONID` cookie is used. Sessions are tied to the browser tab on the frontend via `sessionStorage`
- **CSRF** — disabled for `/api/**`, `/login`, and `/logout` (SPA pattern)
- **CORS** — all origins, methods, and headers are allowed; credentials are enabled. Restrict this in production as needed
- **Password encoding** — BCrypt via `BCryptPasswordEncoder`
- **Entry point** — unauthenticated requests to `/api/**` return `401` instead of a redirect

---

## 🗄️ Data Models

<details>
<summary>👨‍🎓 Students</summary>

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

> Cascade: deleting a student removes all their `Enrollment` rows via `CascadeType.ALL + orphanRemoval`
</details>

<details>
<summary>📚 Courses</summary>

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
</details>

<details>
<summary>📋 Enrollment</summary>

Join table between `Students` and `Courses`.

| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `student_id` | FK → students | Required |
| `course_id` | FK → courses | Required |
| `enrolledDate` | DATETIME | Set at construction |
</details>

<details>
<summary>👤 Users</summary>

| Column | Type | Notes |
|---|---|---|
| `id` | BIGINT | Auto-generated PK |
| `username` | VARCHAR | Required, unique |
| `password` | VARCHAR | BCrypt-hashed |
| `active` | BOOLEAN | Controls `UserDetails.isEnabled()` |
</details>

---

## ⚡ Query Design

Two custom JPQL queries in `StudentRepository` handle enrollment data efficiently:

**`findEnrolledStudents`** — uses `@EntityGraph` to eagerly fetch `enrollments` and `enrollments.course` in a single query. Solves the **N+1 problem**. Only returns students with at least one enrollment. Used for the paginated enrollments list.

**`findEnrolledStudentCourseDetails`** — `JOIN FETCH` query that loads a single student with all their enrolled courses in one round-trip. Used for the enrollment detail modal.

---

## ⚠️ Exception Handling

`GlobalExceptionHandler` (`@RestControllerAdvice`) catches:

| Exception | Status | Notes |
|---|---|---|
| `NoResourceFoundException` | `404` | Suppressed from logs — not an application error |
| `RuntimeException` | `400` | Returns `{"message": "..."}` |
| `Exception` | `500` | Returns a generic message |

---

## ⚙️ Configuration Profiles

### 🧪 Development (`application.properties`)
- MySQL on `localhost:3306`
- `ddl-auto=update`
- SQL logging off

### 🚀 Production (`application-prod.properties`, activated via `-Dspring.profiles.active=prod`)
- PostgreSQL on Render
- Static files served from `classpath:/static/` (React build output)
- Port from `$PORT` env variable (Render injects this automatically)

---

## 🚀 Running Locally

### Prerequisites

![Java](https://img.shields.io/badge/Java_21+-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Maven](https://img.shields.io/badge/Maven_3.9+-C71A36?style=flat&logo=apachemaven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL_8-4479A1?style=flat&logo=mysql&logoColor=white)

```bash
# 1. Clone the repository
git clone https://github.com/your-username/sms-backend.git
cd sms-backend

# 2. Create the database (or let Spring create it automatically)
mysql -u root -p -e "CREATE DATABASE student_mgmt_db;"

# 3. Update credentials in src/main/resources/application.properties if needed

# 4. Run
./mvnw spring-boot:run
```

> API will be available at `http://localhost:8080`

### 🔑 Default Admin Account

Created automatically on first startup by `DataInitializer` if no `Admin` user exists:

```
Username: Admin
Password: admin@123
```

---

## 🐳 Docker

The `Dockerfile` uses a **two-stage build**:

1. **Builder stage** — `maven:3.9.6-eclipse-temurin-21` downloads dependencies and packages the JAR
2. **Runtime stage** — `eclipse-temurin:21-jre-alpine` runs the JAR with the `prod` profile active

```bash
# Build image
docker build -t sms-backend .

# Run (provide DB connection via environment variables or mount a properties file)
docker run -p 8080:8080 sms-backend
```

---

## 📝 Logging

Configured via `logback-spring.xml`:

- **Console** — all environments
- **Rolling file** — daily rotation, max 10 MB per file, 30-day retention, written to `C:/logs/studentmanagement.log`
- Root level: `INFO`

---

<div align="center">

Made with ❤️ by **Your Name**

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/your-username)

</div>
