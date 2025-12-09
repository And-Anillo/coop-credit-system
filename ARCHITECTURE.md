# Cooperative Credit System - Architecture

## Overview

A hexagonal architecture-based microservices system for managing credit affiliates and evaluating credit risk. The system consists of two Spring Boot 4.0.0 services:

1. **Credit Application Service** (Port 8080) - Manages affiliates and credit applications
2. **Risk Central Mock Service** (Port 8081) - Evaluates credit risk deterministically

---

## Credit Application Service

### Architecture Layers

#### Domain Layer

Pure Java entities and business logic with no Spring dependencies.

**Entities:**

- `Affiliate` - Core domain entity representing a credit cooperative member
  - Fields: id, document (final, unique), name, salary, registrationDate, status, createdAt, updatedAt
  - Status Enum: ACTIVE, INACTIVE, SUSPENDED (with Spanish labels)
  - Factory methods: `create()`, `reconstruct()`
  - Domain behaviors: `deactivate()`, `reactivate()`, `updateSalary()`, `isEligibleForCredit()`
  - Validations: document non-null, salary positive, registration date not future

**Value Objects:**

- `RiskEvaluation` - Immutable result of risk assessment
  - Fields: score (Integer ≥ 0), riskLevel (String), detail (String)

**Exceptions:**

- `DomainException` - Domain rule violations
  - Properties: message (user-friendly), errorCode (machine-readable)

**Output Ports (Interfaces):**

- `AffiliateRepository` - Persistence operations
  - Methods: save, findById, findByName, findByDocument, findByStatus, exists, delete
- `RiskCentralPort` - External risk evaluation service
  - Method: `evaluateRisk(document: String, amount: Double, term: Integer) → RiskEvaluation`

**Input Ports (Use Cases):**

- `CreateAffiliateUseCase` - Create affiliate use case
  - Record: `CreateAffiliateCommand(name, document, salary, registrationDate)`
  - Method: `execute(command) → Affiliate`
- `GetAffiliateUseCase` - Retrieve affiliate use case
  - Methods: `getById(id)`, `getByName(name)`, `getByDocument(document)`

#### Application Layer

DTOs, mappers, and service implementations.

**DTOs:**

- `CreateAffiliateRequest` (Record) - Input validation
  - Fields: name, document, salary, registrationDate
  - Validation: @NotBlank, @NotNull, @DecimalMin, custom date validator
- `AffiliateResponse` (Record) - Output representation
  - Fields: id, name, document, salary, registrationDate, status (Spanish label), createdAt, updatedAt

**Mappers:**

- `AffiliateMapper` - MapStruct mapper (v1.5.5.Final)
  - Conversions: CreateAffiliateRequest → Affiliate domain, Affiliate domain ↔ AffiliateResponse
  - Special handling: Maps status enum to Spanish labels in response

**Services:**

- `AffiliateManagementService` - Implements CreateAffiliateUseCase input port
  - Business logic: Duplicate check (by name), risk evaluation call (non-blocking), persistence
  - Logging: Risk evaluation results at INFO level
  - Error handling: Catches InfrastructureException for risk evaluation failures (graceful degradation)
- `GetAffiliateService` - Implements GetAffiliateUseCase input port
  - Logic: Delegates to AffiliateRepository for retrieval

#### Persistence Layer

JPA/Hibernate with PostgreSQL (via Testcontainers in tests).

**Entities:**

- `AffiliateEntity` - JPA entity mapped to 'affiliates' table
  - Column mapping: id, name, document (UNIQUE), salary, registrationDate, status (with converter), createdAt, updatedAt
  - Status conversion: Java enum ↔ Spanish DB strings via JPA converter

**Converters:**

- `AffiliateStatusConverter` - Localizes status values
  - Database format: "ACTIVO" (Active), "INACTIVO" (Inactive), "SUSPENDIDO" (Suspended)
  - Java format: AffiliateStatus.ACTIVE, INACTIVE, SUSPENDED
  - Bidirectional: convertToDatabaseColumn, convertToEntityAttribute

**Repositories:**

- `AffiliateJpaRepository` - Spring Data JPA repository
  - Query methods: findByName, findByDocument, findByStatus, existsByDocument
  - Base interface: JpaRepository<AffiliateEntity, Long>

**Adapters:**

- `AffiliateRepositoryAdapter` - Implements domain AffiliateRepository output port

  - Converts between JPA entities and domain objects using mapper
  - Delegates CRUD operations to JPA repository

- `RiskCentralAdapter` - Implements RiskCentralPort output port
  - Uses Spring RestClient to POST to http://localhost:8081/risk-evaluation
  - Request/Response DTOs: RiskEvaluationRequestDto, RiskEvaluationResponseDto
  - Error handling: Throws InfrastructureException on HTTP errors or network failures

**Configuration:**

- `RestClientConfig` - Spring RestClient bean configuration
  - BaseUrl: http://localhost:8081
  - No custom timeout/error handlers (uses Spring defaults)

**Database Migrations:**

- `V1__init_schema.sql` - Flyway migration creating affiliates table
  - PK: id (BIGSERIAL)
  - UC: (document)
  - Audit: created_at, updated_at (TIMESTAMP)

#### Input/REST Layer

Spring WebMvc REST controllers with RFC 7807 error responses.

**Controllers:**

- `AffiliateController` - REST API for affiliate management
  - Endpoints:
    - `POST /affiliates` → CreateAffiliateUseCase.execute() → AffiliateResponse (201)
    - `GET /affiliates/{id}` → GetAffiliateUseCase.getById() → AffiliateResponse (200)
    - `GET /affiliates?document={doc}` → GetAffiliateUseCase.getByDocument() → AffiliateResponse (200)
  - Response type: AffiliateResponse DTO with HTTP status codes
  - Request validation: @Valid on DTO parameters

**Exception Handlers:**

- `GlobalExceptionHandler` - @RestControllerAdvice
  - Handles DomainException → 400 Bad Request with error message and code
  - Handles MethodArgumentNotValidException → 400 with field validation errors
  - Handles generic Exception → 500 Internal Server Error
  - Response format: RFC 7807 ProblemDetail
    - type: URI identifying error class
    - title: HTTP status text
    - detail: Spanish user-facing message
    - instance: request path
    - properties: additional fields (e.g., errorCode, violations)

### Data Flow

```
REST Request
    ↓
AffiliateController (input adapter)
    ↓
CreateAffiliateUseCase (input port)
    ↓
AffiliateManagementService (application service)
    ├─ AffiliateRepository.findByName() (output port)
    │   └─ AffiliateRepositoryAdapter
    │       └─ AffiliateJpaRepository (Spring Data)
    │
    ├─ AffiliateRepository.save() (output port)
    │   └─ AffiliateRepositoryAdapter
    │       └─ AffiliateJpaRepository (Spring Data)
    │
    └─ RiskCentralPort.evaluateRisk() (output port)
        └─ RiskCentralAdapter
            └─ RestClient → http://localhost:8081/risk-evaluation
```

### Technology Stack

- **Language**: Java 17
- **Framework**: Spring Boot 4.0.0
- **Build**: Maven
- **ORM**: JPA/Hibernate 7.1.8.Final
- **Database**: PostgreSQL (production), TestContainers (integration tests)
- **Mapping**: MapStruct 1.5.5.Final
- **HTTP**: Spring RestClient (Spring Boot 4.0.0 native)
- **Testing**: JUnit 5, Mockito, Testcontainers
- **Validation**: Jakarta Bean Validation with Spanish messages

---

## Risk Central Mock Service

### Purpose

Lightweight deterministic risk evaluation service. Returns same risk score for same document (idempotent).

### Endpoints

- `POST /risk-evaluation`
  - Request body: `RiskEvaluationRequest` (document, amount, term)
  - Response: `RiskEvaluationResponse` (document, score, riskLevel, detail, evaluatedAt)
  - Algorithm: Deterministic hash-based calculation on document
    - Score range: 300-950
    - Risk levels: "BAJO" (Low), "MEDIO" (Medium), "ALTO" (High) in Spanish

### Implementation

- **Controller**: `RiskCentralController`
- **DTOs**: `RiskEvaluationRequest`, `RiskEvaluationResponse` (records)
- **Port**: 8081
- **Database**: None (stateless)
- **Security**: None (public evaluation)

### Technology Stack

Same as Credit Application Service (Spring Boot 4.0.0, Java 17, Maven)

---

## Localization & Internationalization

### Spanish Status Values

Stored in database as Spanish strings; Java enum used for type safety in application.

| Java Enum | Database String | Label      |
| --------- | --------------- | ---------- |
| ACTIVE    | ACTIVO          | Activo     |
| INACTIVE  | INACTIVO        | Inactivo   |
| SUSPENDED | SUSPENDIDO      | Suspendido |

### Spanish Risk Levels

Risk Central Mock Service returns Spanish risk level labels:

- "BAJO" - Low risk
- "MEDIO" - Medium risk
- "ALTO" - High risk

### Spanish Error Messages

All error responses include Spanish `detail` messages:

- "El afiliado con este documento ya existe" - Affiliate already exists
- "El afiliado no encontrado" - Affiliate not found
- "El servicio de riesgo no está disponible" - Risk service unavailable
- And others...

---

## Deployment

### Docker Compose

```yaml
version: "3.8"
services:
  credit-application-service:
    image: coop-credit/credit-application-service:1.0.0
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/coop_credit
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=password
    depends_on:
      - postgres

  risk-central-mock-service:
    image: coop-credit/risk-central-mock-service:1.0.0
    ports:
      - "8081:8081"

  postgres:
    image: postgres:18-alpine
    environment:
      - POSTGRES_DB=coop_credit
      - POSTGRES_PASSWORD=password
    volumes:
      - postgres_data:/var/lib/postgresql/data

volumes:
  postgres_data:
```

---

## Testing

### Unit Tests

- `AffiliateManagementServiceTest` - Service business logic
  - Test: Create affiliate success
  - Test: Create duplicate affiliate throws exception
  - Mocks: Repository, Mapper, RiskCentralPort

### Integration Tests

- `CreditApplicationServiceApplicationTests` - Spring context bootstrap
  - Verifies all beans load correctly
  - Uses Testcontainers PostgreSQL
  - Verifies Flyway migrations execute

### Running Tests

```bash
# Credit Application Service
mvn -f credit-application-service/pom.xml clean test

# Risk Central Mock Service
mvn -f risk-central-mock-service/pom.xml clean test
```

---

## Future Enhancements

1. **Pagination & Filtering** - Add query parameters for affiliate listing
2. **Update/Delete Endpoints** - Complete REST CRUD operations
3. **Async Risk Evaluation** - Use messaging queue instead of synchronous calls
4. **JWT Security** - Implement authentication/authorization
5. **Swagger/OpenAPI** - API documentation with Spring Doc OpenAPI
6. **Metrics & Monitoring** - Micrometer integration for Prometheus
7. **Resilience** - Circuit breaker (Resilience4j) for risk service calls
8. **Cache** - Redis caching for frequent queries
9. **Event Publishing** - Domain events for affiliate lifecycle
10. **Multi-tenancy** - Support for multiple credit cooperatives

---

## Key Design Decisions

### Hexagonal Architecture

- **Rationale**: Clear separation of concerns, testability, independence from frameworks
- **Benefits**: Easy to swap implementations (e.g., database, external services), minimal coupling

### MapStruct Mappers

- **Rationale**: Compile-time code generation, type-safe, zero-reflection overhead
- **Benefits**: Fast, minimal boilerplate, clear data transformations

### JPA AttributeConverter

- **Rationale**: Localize database schema without polluting domain layer
- **Benefits**: Database stores Spanish values, Java uses enums for type safety

### RFC 7807 ProblemDetail

- **Rationale**: Standardized error format for REST APIs
- **Benefits**: Clients can parse errors consistently, tooling support

### Deterministic Risk Algorithm

- **Rationale**: Same input always produces same output for testing/reproducibility
- **Benefits**: No side effects, can cache results, easy to unit test

### Non-blocking Risk Evaluation

- **Rationale**: Risk service is external and may fail or be slow
- **Benefits**: Affiliate creation doesn't depend on risk service availability, graceful degradation

---

## Build & Run

### Build Both Services

```bash
mvn clean package
```

### Run Services Locally

```bash
# Terminal 1: Risk Central Mock Service
cd risk-central-mock-service
mvn spring-boot:run

# Terminal 2: Credit Application Service (requires PostgreSQL)
cd credit-application-service
mvn spring-boot:run
```

### Using Docker Compose

```bash
docker-compose up
```

---

## Documentation

- **JavaDoc**: All public classes and methods documented
- **Log output**: INFO level logs for risk evaluations, WARN level for failures
- **Configuration**: Spring properties documented in application.properties files
- **DDL**: SQL migrations versioned with Flyway in db/migration/

---

**Last Updated**: 2025-12-09
**Status**: ✓ Ready for Development
