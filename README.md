# CoopCredit System - Credit Management Platform

A modern Spring Boot microservices system for managing credit applications in a cooperative environment.

## 📋 Project Overview

The **CoopCredit** system is designed to manage credit applications from cooperative members (affiliates). It consists of two Spring Boot services:

1. **credit-application-service** - Core service managing affiliates and credit applications
2. **risk-central-mock-service** - External risk evaluation service

### Key Features

✅ Hexagonal (Ports & Adapters) Architecture
✅ Domain-Driven Design principles
✅ JWT-based authentication and role-based access control
✅ PostgreSQL persistence with Flyway migrations
✅ RFC 7807 compliant error handling
✅ Comprehensive test coverage
✅ Spanish user-facing messages, English code

---

## 🏗️ Architecture

### Hexagonal Architecture Pattern

```
┌─────────────────────────────────────────┐
│  REST Controllers, DB, HTTP Clients    │ Infrastructure
├─────────────────────────────────────────┤
│  Services, DTOs, Use Case Implementation│ Application
├─────────────────────────────────────────┤
│  Entities, Exceptions, Ports (Interfaces)│ Domain (Pure Java)
└─────────────────────────────────────────┘
```

**Benefits:**

- Pure domain logic (testable without framework)
- Clear separation of concerns
- Easy to swap implementations
- Dependency injection flows from outer → inner layers
- Independent layer testing

### Layers

#### **Domain Layer** (`domain/`)

- Pure Java with no Spring dependencies
- Business logic and invariants
- Entities, exceptions, and ports (interfaces)
- Language: **English**

#### **Application Layer** (`application/`)

- Use case implementations (services)
- DTOs for request/response
- Orchestration logic
- Spring annotations for component management

#### **Infrastructure Layer** (`infrastructure/`)

- REST controllers, database adapters, external clients
- Spring Framework integration
- Configuration and beans
- Concrete implementations of domain ports

---

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Maven 3.8+
- Docker and Docker Compose
- PostgreSQL (via Docker recommended)

### Setup & Run

```bash
# Clone and navigate to project
cd /path/to/coop-credit-system

# Start services with Docker Compose
docker-compose up

# Services will be available at:
# - credit-application-service: http://localhost:8080
# - risk-central-mock-service: http://localhost:8081
# - PostgreSQL: localhost:5432 (user: coopcredit, pass: coopcredit)
```

### Build Without Docker

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package
mvn clean package

# Run application
java -jar credit-application-service/target/credit-application-service-0.0.1-SNAPSHOT.jar
```

---

## 📁 Project Structure

### credit-application-service

```
credit-application-service/
├── src/main/java/com/coopcredit/credit_application_service/
│   ├── domain/                          # Pure Java business logic
│   │   ├── entity/
│   │   │   ├── Affiliate.java          # Main entity: cooperative member
│   │   │   └── CreditApplication.java  # Credit application entity
│   │   ├── exception/                  # Domain-specific exceptions
│   │   │   ├── DomainException.java
│   │   │   ├── AffiliateNotFoundException.java
│   │   │   └── InvalidApplicationException.java
│   │   └── port/                       # Interfaces (contracts)
│   │       ├── input/                  # Use case contracts
│   │       │   ├── CreateAffiliateUseCase.java
│   │       │   ├── GetAffiliateUseCase.java
│   │       │   └── CreateApplicationUseCase.java
│   │       └── output/                 # Adapter contracts
│   │           ├── AffiliateRepository.java
│   │           ├── ApplicationRepository.java
│   │           └── RiskEvaluationPort.java
│   │
│   ├── application/                     # Use cases & DTOs
│   │   ├── service/
│   │   │   ├── CreateAffiliateService.java
│   │   │   ├── GetAffiliateService.java
│   │   │   ├── CreateApplicationService.java
│   │   │   └── GetApplicationService.java
│   │   └── dto/
│   │       ├── CreateAffiliateRequest.java
│   │       ├── AffiliateResponse.java
│   │       ├── CreateApplicationRequest.java
│   │       └── ApplicationResponse.java
│   │
│   ├── infrastructure/                  # Adapters & Spring config
│   │   ├── adapter/
│   │   │   ├── rest/                   # REST controllers
│   │   │   │   ├── AffiliateController.java
│   │   │   │   ├── ApplicationController.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   ├── persistence/            # Database adapters
│   │   │   │   ├── AffiliateJpaRepository.java (Spring Data)
│   │   │   │   ├── ApplicationJpaRepository.java
│   │   │   │   ├── AffiliateRepositoryAdapter.java
│   │   │   │   └── ApplicationRepositoryAdapter.java
│   │   │   └── external/               # External service clients
│   │   │       └── RiskCentralClient.java
│   │   ├── config/
│   │   │   ├── SecurityConfig.java     # JWT & Spring Security
│   │   │   ├── JwtTokenProvider.java   # Token generation/validation
│   │   │   ├── JwtAuthFilter.java      # Request filter
│   │   │   └── RestClientConfig.java   # HTTP client config
│   │   └── persistence/
│   │       └── entity/                 # JPA entities (DB models)
│   │           ├── AffiliateEntity.java
│   │           ├── ApplicationEntity.java
│   │           └── UserEntity.java
│   │
│   └── CreditApplicationServiceApplication.java
│
├── src/main/resources/
│   ├── application.properties           # Configuration
│   └── db/migration/                    # Flyway SQL migrations
│       ├── V001__create_affiliates_table.sql
│       ├── V002__create_applications_table.sql
│       └── V003__create_users_table.sql
│
├── src/test/java/                       # Unit & integration tests
│   └── com/coopcredit/credit_application_service/
│       ├── domain/entity/AffiliateTest.java
│       ├── application/service/CreateAffiliateServiceTest.java
│       └── infrastructure/adapter/rest/AffiliateControllerTest.java
│
└── pom.xml                              # Maven configuration
```

### risk-central-mock-service

```
risk-central-mock-service/
├── src/main/java/com/coopcredit/risk_central_mock_service/
│   ├── controller/
│   │   └── RiskEvaluationController.java
│   └── RiskCentralMockServiceApplication.java
│
└── pom.xml
```

---

## 🔌 API Endpoints

### Affiliates

```http
POST /api/affiliates
Content-Type: application/json

{
  "name": "Juan Pérez",
  "salary": 5000.00,
  "registrationDate": "2025-01-01"
}

Response: 201 Created
{
  "id": 1,
  "name": "Juan Pérez",
  "salary": 5000.00,
  "registrationDate": "2025-01-01",
  "status": "Activo"
}
```

```http
GET /api/affiliates/{id}

Response: 200 OK
{
  "id": 1,
  "name": "Juan Pérez",
  "salary": 5000.00,
  "registrationDate": "2025-01-01",
  "status": "Activo"
}
```

### Credit Applications

```http
POST /api/applications
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

{
  "affiliateId": 1,
  "amount": 10000.00,
  "months": 24
}

Response: 201 Created
{
  "id": 1,
  "affiliateId": 1,
  "amount": 10000.00,
  "months": 24,
  "status": "Pendiente",
  "riskScore": 650,
  "riskLevel": "MEDIO",
  "createdAt": "2025-01-09T15:30:00"
}
```

```http
GET /api/applications/{id}
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
{...}
```

### Risk Evaluation (Mock Service)

```http
POST /risk-evaluation
Content-Type: application/json

{
  "document": "123456789"
}

Response: 200 OK
{
  "document": "123456789",
  "score": 650,
  "riskLevel": "MEDIO",
  "detail": "Riesgo evaluado: MEDIO"
}
```

---

## 🔐 Security

### JWT Authentication

1. **Token Generation** (usually from login endpoint)

   ```
   POST /api/auth/login
   {
     "username": "user@example.com",
     "password": "password"
   }

   Response:
   {
     "token": "eyJhbGciOiJIUzI1NiIs..."
   }
   ```

2. **Using Token** (in requests)
   ```
   GET /api/affiliates/1
   Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   ```

### Roles

- **ROLE_AFILIADO** - Cooperative member (can view own applications)
- **ROLE_ANALISTA** - Credit analyst (can view/approve applications)
- **ROLE_ADMIN** - System administrator (full access)

---

## 💾 Database

### Schema

**affiliates**

- `id` (BIGSERIAL PRIMARY KEY)
- `name` (VARCHAR 255, NOT NULL)
- `salary` (DECIMAL 19,2, NOT NULL)
- `registration_date` (DATE, NOT NULL)
- `status` (VARCHAR 20, DEFAULT 'ACTIVE')
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP, NOT NULL)

**applications**

- `id` (BIGSERIAL PRIMARY KEY)
- `affiliate_id` (BIGINT, FOREIGN KEY)
- `amount` (DECIMAL 19,2, NOT NULL)
- `months` (INTEGER, NOT NULL)
- `status` (VARCHAR 20, DEFAULT 'PENDING')
- `risk_score` (INTEGER)
- `risk_level` (VARCHAR 20)
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP, NOT NULL)

**users**

- `id` (BIGSERIAL PRIMARY KEY)
- `username` (VARCHAR 100, UNIQUE, NOT NULL)
- `password` (VARCHAR 255, NOT NULL)
- `email` (VARCHAR 255, UNIQUE, NOT NULL)
- `affiliate_id` (BIGINT, FOREIGN KEY, NULLABLE)
- `role` (VARCHAR 50, NOT NULL)
- `enabled` (BOOLEAN, DEFAULT TRUE)
- `created_at` (TIMESTAMP, NOT NULL)
- `updated_at` (TIMESTAMP, NOT NULL)

### Migrations

Flyway automatically runs migration scripts in `src/main/resources/db/migration/`:

- `V001__create_affiliates_table.sql`
- `V002__create_applications_table.sql`
- `V003__create_users_table.sql`

---

## 🧪 Testing

### Running Tests

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=AffiliateTest

# Run with coverage
mvn test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Structure

- **Domain Tests** - Pure Java, no Spring context, testing business logic
- **Service Tests** - With mocked repositories, testing use cases
- **Controller Tests** - Integration tests with TestRestTemplate
- **Integration Tests** - With Testcontainers for database

---

## 📊 Configuration

### application.properties

```properties
# Application
spring.application.name=credit-application-service
server.port=8080

# Database
spring.datasource.url=jdbc:postgresql://postgres:5432/coopcredit
spring.datasource.username=coopcredit
spring.datasource.password=coopcredit

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false

# Flyway
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration

# JWT
jwt.secret=your-secret-key-must-be-at-least-32-characters-long
jwt.expiration=86400000

# Logging
logging.level.root=INFO
logging.level.com.coopcredit=DEBUG

# Risk Central
risk-central.url=http://localhost:8081
```

---

## 🛠️ Development Guide

### Key Principles

1. **Pure Domain** - No Spring in `domain/` package
2. **Ports & Adapters** - Interfaces define contracts
3. **Dependency Injection** - Outer layers inject into inner layers
4. **Language Rules** - Code in English, user messages in Spanish
5. **Error Handling** - RFC 7807 ProblemDetail format

### Adding New Features

1. **Create Domain Entity** (e.g., `domain/entity/Loan.java`)

   - Pure Java, no annotations
   - Factory methods, validation
   - Business operations

2. **Create Ports** (e.g., `domain/port/input/CreateLoanUseCase.java`)

   - Input ports (use cases)
   - Output ports (repositories)

3. **Create Service** (e.g., `application/service/CreateLoanService.java`)

   - Implements input port
   - Injects output ports
   - Orchestrates domain logic

4. **Create DTO** (e.g., `application/dto/CreateLoanRequest.java`)

   - Validation annotations
   - Maps to/from domain

5. **Create Controller** (e.g., `infrastructure/adapter/rest/LoanController.java`)

   - Injects use cases
   - Maps DTOs to domain
   - Returns responses

6. **Create Repository Adapter** (e.g., `infrastructure/adapter/persistence/LoanRepositoryAdapter.java`)

   - Implements output port
   - Converts domain ↔ JPA

7. **Create JPA Entity** (e.g., `infrastructure/persistence/entity/LoanEntity.java`)

   - @Entity annotations
   - Database mappings

8. **Create Tests**
   - Domain tests (pure Java)
   - Service tests (with mocks)
   - Controller tests (integration)

---

## 📚 Documentation

See the documentation files in the root directory:

- **`HEXAGONAL_ARCHITECTURE.md`** - Detailed architecture explanation
- **`QUICK_REFERENCE.md`** - Quick guide for developers
- **`IMPLEMENTATION_CHECKLIST.md`** - Step-by-step implementation guide
- **`ARCHITECTURE_DIAGRAMS.md`** - Visual diagrams and flows
- **`IMPLEMENTATION_SUMMARY.md`** - Summary of completed work

---

## 🐳 Docker

### Build Images

```bash
# Build credit-application-service
cd credit-application-service
mvn clean package
docker build -t coopcredit/credit-application-service:latest .

# Build risk-central-mock-service
cd risk-central-mock-service
mvn clean package
docker build -t coopcredit/risk-central-mock-service:latest .
```

### Run with Docker Compose

```bash
docker-compose up

# View logs
docker-compose logs -f

# Stop services
docker-compose down

# Remove volumes
docker-compose down -v
```

---

## 🔗 External Services

### Risk Central Mock Service

The `risk-central-mock-service` provides a deterministic risk evaluation:

- **Endpoint:** `POST /risk-evaluation`
- **Input:** `{ "document": "123456" }`
- **Output:** `{ "document": "123456", "score": 650, "riskLevel": "MEDIO", "detail": "..." }`
- **Logic:** Score = hash(document) % 651 + 300 (range 300-950)
- **Risk Levels:**
  - BAJO (Low): score >= 700
  - MEDIO (Medium): 500 <= score < 700
  - ALTO (High): score < 500

---

## 📝 Language Constraints

### English (Code)

- Class names: `AffiliateService`, `CreditApplication`
- Method names: `createAffiliate()`, `evaluateRisk()`
- Variables: `affiliateName`, `creditAmount`
- Comments: "Calculate affiliate eligibility"

### Spanish (User-Facing)

- Error messages: "El afiliado no fue encontrado"
- API responses: `"detail": "El salario debe ser mayor a cero"`
- Status labels: "Activo", "Inactivo", "Pendiente"

---

## 🚨 Error Handling

All errors return RFC 7807 ProblemDetail format:

```json
{
  "type": "about:blank/business-rule",
  "title": "Business Rule Violation",
  "status": 422,
  "detail": "El afiliado ya está inactivo",
  "instance": "/api/affiliates/1",
  "code": "INVALID_STATE_TRANSITION"
}
```

Error codes:

- `AFFILIATE_NOT_FOUND` - Affiliate doesn't exist
- `VALIDATION_ERROR` - Field validation failed
- `INVALID_APPLICATION` - Business rule violation
- `INVALID_STATE_TRANSITION` - Illegal state change

---

## 📞 Support & Troubleshooting

### Common Issues

**Port already in use**

```bash
# Kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or use different port
docker-compose up -e SERVER_PORT=8090
```

**Database connection error**

```bash
# Check PostgreSQL container
docker-compose logs postgres

# Verify credentials in application.properties
```

**JWT token expired**

```bash
# Get new token from login endpoint
# Update jwt.expiration in application.properties
```

---

## 📄 License

[Add your license here]

---

## 👥 Authors

- **Your Name** - Architecture & Implementation

---

## 🔄 Version History

- **v0.0.1** (Jan 2025) - Initial hexagonal architecture setup
  - Domain layer with Affiliate entity
  - Ports and adapters pattern
  - Foundation for application layer

---

**Last Updated:** January 9, 2025

For detailed information, see the documentation files in the project root.
