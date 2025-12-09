# Hexagonal Architecture Diagrams

## 1. Overall System Architecture

```
┌──────────────────────────────────────────────────────────────────────────┐
│                         EXTERNAL WORLD                                    │
│  REST Clients  │  PostgreSQL  │  Risk Central Mock Service                │
└────────┬───────────────────────────────────────┬──────────────────────────┘
         │                                       │
         ↓                                       ↓
┌──────────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER (Adapters)                        │
│  ┌────────────────────┐  ┌──────────────────┐  ┌────────────────────┐   │
│  │  REST Controllers  │  │  JPA Repository  │  │  HTTP Clients      │   │
│  │  Endpoints         │  │  Adapters        │  │  (RiskCentral)     │   │
│  └────────┬───────────┘  └────────┬─────────┘  └────────┬───────────┘   │
│           │                       │                     │                │
│           └───────────────────────┼─────────────────────┘                │
│                                   ↓                                       │
│           ┌───────────────────────────────────────┐                      │
│           │  DI Container / Spring Configuration  │                      │
│           └───────────────┬───────────────────────┘                      │
│                           │                                               │
│                           ↓ injects ports                                 │
└───────────────────────────┼─────────────────────────────────────────────┘
                            │
                            ↓
┌──────────────────────────────────────────────────────────────────────────┐
│                     APPLICATION LAYER (Use Cases)                         │
│  ┌─────────────────────┐  ┌────────────────────────┐                    │
│  │  Services           │  │  DTOs                  │                    │
│  │  - CreateAffiliate  │  │  - Requests            │                    │
│  │  - GetAffiliate     │  │  - Responses           │                    │
│  │  - CreateApplication│  │                        │                    │
│  └──────────┬──────────┘  └────────────────────────┘                    │
│             │                                                             │
│             │ implements/uses                                             │
│             ↓                                                             │
│    ┌────────────────┐                                                    │
│    │ Input Ports    │                                                    │
│    │ (Use Cases)    │                                                    │
│    └────────┬───────┘                                                    │
│             │                                                             │
│             ↓ depends on                                                  │
└─────────────┼──────────────────────────────────────────────────────────┘
              │
              ↓
┌──────────────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER (Business Rules)                       │
│  ┌────────────────────┐  ┌─────────────────┐  ┌─────────────────────┐  │
│  │  Entities          │  │  Exceptions     │  │  Ports (Contracts) │  │
│  │  - Affiliate       │  │  - DomainEx     │  │  - Input Ports     │  │
│  │  - CreditApplication│ │  - AffiliateNotFound │ - Output Ports    │  │
│  │  - User            │  │  - InvalidApplication│                   │  │
│  └────────────────────┘  └─────────────────┘  └─────────────────────┘  │
│                                                                           │
│  ✓ Pure Java (no Spring)                                               │
│  ✓ Business logic & invariants                                          │
│  ✓ No database knowledge                                                │
│  ✓ Spanish error messages                                               │
│                                                                           │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Affiliate Entity - Internal Structure

```
┌─────────────────────────────────────────────┐
│         AFFILIATE (Domain Entity)            │
├─────────────────────────────────────────────┤
│  Private Fields:                            │
│  - id: Long                                 │
│  - name: String                             │
│  - salary: BigDecimal                       │
│  - registrationDate: LocalDate              │
│  - status: AffiliateStatus (enum)           │
│  - createdAt: LocalDateTime                 │
│  - updatedAt: LocalDateTime                 │
├─────────────────────────────────────────────┤
│  Factory Methods:                           │
│  + create(name, salary, date): Affiliate    │
│  + reconstruct(...): Affiliate              │
├─────────────────────────────────────────────┤
│  Domain Behaviors:                          │
│  + deactivate(): void                       │
│  + reactivate(): void                       │
│  + updateSalary(newSalary): void            │
│  + isEligibleForCredit(): boolean           │
├─────────────────────────────────────────────┤
│  Getters (immutable):                       │
│  + getId(): Long                            │
│  + getName(): String                        │
│  + getSalary(): BigDecimal                  │
│  + getStatus(): AffiliateStatus             │
│  + getRegistrationDate(): LocalDate         │
│  + getCreatedAt(): LocalDateTime            │
│  + getUpdatedAt(): LocalDateTime            │
├─────────────────────────────────────────────┤
│  Protected Setters (for adapters):          │
│  # setName(name): void                      │
│  # setSalary(salary): void                  │
│  # setRegistrationDate(date): void          │
├─────────────────────────────────────────────┤
│  Inner Enum:                                │
│  AffiliateStatus {                          │
│    ACTIVE("Activo"),                        │
│    INACTIVE("Inactivo"),                    │
│    SUSPENDED("Suspendido")                  │
│  }                                          │
└─────────────────────────────────────────────┘
```

---

## 3. Data Flow: Create Affiliate

```
┌──────────────┐
│ REST Client  │
│ POST /api/   │
│ affiliates   │
└──────┬───────┘
       │
       │ JSON request
       │ {name, salary, registrationDate}
       ↓
┌──────────────────────────────────┐
│ AffiliateController              │
│ @RestController                  │
│ @PostMapping("/api/affiliates")  │
└──────┬───────────────────────────┘
       │
       │ calls
       │ CreateAffiliateUseCase.execute(command)
       ↓
┌──────────────────────────────────┐
│ CreateAffiliateService           │
│ @Service                         │
│ implements CreateAffiliateUseCase│
└──────┬───────────────────────────┘
       │
       │ 1. Parse command
       │ 2. Create domain object
       │
       ↓
┌──────────────────────────────────┐
│ Affiliate (Domain Entity)         │
│ create(name, salary, date)       │
│ - Validates all inputs           │
│ - Sets status = ACTIVE           │
│ - Sets timestamps = now()        │
└──────┬───────────────────────────┘
       │
       │ 3. Persist using port
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateRepository (Port)       │
│ save(affiliate): Affiliate       │
└──────┬───────────────────────────┘
       │
       │ 4. Adapter converts domain → JPA
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateRepositoryAdapter       │
│ @Repository                      │
│ implements AffiliateRepository   │
└──────┬───────────────────────────┘
       │
       │ 5. Uses Spring Data JPA
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateJpaRepository           │
│ extends JpaRepository            │
│ save(affiliateEntity)            │
└──────┬───────────────────────────┘
       │
       │ 6. Hibernate ORM mapping
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateEntity (@Entity)        │
│ mapped to "affiliates" table     │
└──────┬───────────────────────────┘
       │
       │ 7. SQL INSERT
       │
       ↓
┌──────────────────────────────────┐
│ PostgreSQL Database              │
│ INSERT INTO affiliates (...)     │
└──────┬───────────────────────────┘
       │
       │ 8. Returns saved entity + ID
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateRepositoryAdapter       │
│ Converts JPA entity → Domain obj │
│ with assigned ID                 │
└──────┬───────────────────────────┘
       │
       │ 9. Return domain object
       │
       ↓
┌──────────────────────────────────┐
│ CreateAffiliateService           │
│ Return Affiliate with ID         │
└──────┬───────────────────────────┘
       │
       │ 10. Convert to DTO
       │
       ↓
┌──────────────────────────────────┐
│ AffiliateResponse (DTO)          │
│ {id, name, salary, status}       │
└──────┬───────────────────────────┘
       │
       │ 11. JSON response
       │
       ↓
┌──────────────────────────────────┐
│ HTTP 201 Created                 │
│ {id: 1, name: "Juan", ...}       │
└──────────────────────────────────┘
```

---

## 4. Package Organization with Dependencies

```
domain/
│
├── entity/
│   └── Affiliate.java
│       ├── Enum: AffiliateStatus
│       └── Uses: (nothing)
│
├── exception/
│   ├── DomainException.java
│   ├── AffiliateNotFoundException.java
│   └── InvalidApplicationException.java
│       └── Uses: (nothing)
│
└── port/
    ├── input/
    │   ├── CreateAffiliateUseCase.java
    │   │   ├── record: CreateAffiliateCommand
    │   │   └── Uses: Affiliate
    │   └── GetAffiliateUseCase.java
    │       └── Uses: Affiliate
    │
    └── output/
        ├── AffiliateRepository.java
        │   └── Uses: Affiliate
        └── RiskEvaluationPort.java
            ├── Class: RiskEvaluationResult
            └── Uses: (nothing)

application/
│
├── dto/
│   ├── CreateAffiliateRequest.java
│   │   └── Uses: @NotBlank, @Positive (validation)
│   └── AffiliateResponse.java
│       └── Uses: Affiliate (from domain)
│
└── service/
    ├── CreateAffiliateService.java
    │   ├── Uses: CreateAffiliateUseCase (port)
    │   ├── Uses: AffiliateRepository (port)
    │   └── Implements: CreateAffiliateUseCase
    └── GetAffiliateService.java
        ├── Uses: GetAffiliateUseCase (port)
        ├── Uses: AffiliateRepository (port)
        └── Implements: GetAffiliateUseCase

infrastructure/
│
├── adapter/
│   │
│   ├── rest/
│   │   ├── AffiliateController.java
│   │   │   ├── Uses: CreateAffiliateUseCase (port)
│   │   │   ├── Uses: GetAffiliateUseCase (port)
│   │   │   ├── Uses: CreateAffiliateRequest (DTO)
│   │   │   └── Uses: AffiliateResponse (DTO)
│   │   │
│   │   └── GlobalExceptionHandler.java
│   │       ├── Uses: DomainException
│   │       └── Returns: ProblemDetail (RFC 7807)
│   │
│   ├── persistence/
│   │   ├── AffiliateJpaRepository.java
│   │   │   └── Extends: JpaRepository<AffiliateEntity, Long>
│   │   │
│   │   ├── AffiliateRepositoryAdapter.java
│   │   │   ├── Implements: AffiliateRepository (port)
│   │   │   ├── Uses: AffiliateJpaRepository
│   │   │   ├── Uses: Affiliate (domain entity)
│   │   │   └── Uses: AffiliateEntity (JPA entity)
│   │   │
│   │   └── ApplicationJpaRepository.java
│   │       └── Extends: JpaRepository<ApplicationEntity, Long>
│   │
│   └── external/
│       └── RiskCentralClient.java
│           ├── Implements: RiskEvaluationPort (port)
│           └── Uses: RestTemplate (HTTP)
│
├── config/
│   ├── SecurityConfig.java
│   ├── JwtTokenProvider.java
│   └── JwtAuthFilter.java
│
└── persistence/
    └── entity/
        ├── AffiliateEntity.java
        │   └── @Entity, mapped to "affiliates" table
        └── ApplicationEntity.java
            └── @Entity, mapped to "applications" table
```

---

## 5. Dependency Injection Flow

```
Spring Application Context (IoC Container)
│
├── Bean: AffiliateJpaRepository
│   @Repository
│   Registered by Spring Data JPA
│   Implements: JpaRepository<AffiliateEntity, Long>
│
├── Bean: AffiliateRepositoryAdapter
│   @Repository
│   Depends on: AffiliateJpaRepository (injected via constructor)
│   Implements: AffiliateRepository (domain port)
│
├── Bean: CreateAffiliateService
│   @Service
│   Depends on: AffiliateRepository (injected via constructor)
│   Implements: CreateAffiliateUseCase
│
├── Bean: GetAffiliateService
│   @Service
│   Depends on: AffiliateRepository (injected via constructor)
│   Implements: GetAffiliateUseCase
│
└── Bean: AffiliateController
    @RestController
    Depends on:
    ├─ CreateAffiliateUseCase (injected via constructor)
    └─ GetAffiliateUseCase (injected via constructor)
```

When controller calls use case:

```
Controller (AffiliateController)
  ↓
Use Case Interface (CreateAffiliateUseCase)
  ↓
Service Implementation (CreateAffiliateService)
  ↓
Repository Port Interface (AffiliateRepository)
  ↓
Repository Adapter Implementation (AffiliateRepositoryAdapter)
  ↓
JPA Repository (AffiliateJpaRepository)
  ↓
Database (PostgreSQL)
```

---

## 6. Error Handling Flow

```
┌──────────────────────────────────────────┐
│ User calls POST /api/affiliates          │
│ {name: "", salary: -100}                 │
└──────────┬───────────────────────────────┘
           │
           ↓
┌──────────────────────────────────────────┐
│ AffiliateController                      │
│ validate @RequestBody                    │
└──────────┬───────────────────────────────┘
           │
           ├─ If validation fails:
           │  └─→ MethodArgumentNotValidException
           │
           └─ If passes, call use case
              │
              ↓
┌──────────────────────────────────────────┐
│ CreateAffiliateService.execute()         │
│ Calls: Affiliate.create(name, salary...) │
└──────────┬───────────────────────────────┘
           │
           ├─ If name.isBlank():
           │  └─→ IllegalArgumentException
           │      "El nombre del afiliado no puede estar vacío"
           │
           ├─ If salary <= 0:
           │  └─→ IllegalArgumentException
           │      "El salario debe ser mayor a cero"
           │
           └─ If registrationDate in future:
              └─→ IllegalArgumentException
                  "La fecha de registro no puede ser en el futuro"
              │
              ↓
┌──────────────────────────────────────────┐
│ GlobalExceptionHandler                   │
│ @ControllerAdvice                        │
│ Catches all exceptions                   │
└──────────┬───────────────────────────────┘
           │
           ├─ @ExceptionHandler(MethodArgumentNotValidException)
           │  ↓
           │  HTTP 400 Bad Request
           │  {
           │    "type": "about:blank/validation-error",
           │    "title": "Validation Failed",
           │    "status": 400,
           │    "detail": "Invalid input fields",
           │    "instance": "/api/affiliates"
           │  }
           │
           ├─ @ExceptionHandler(DomainException)
           │  ↓
           │  HTTP 409 Conflict / 422 Unprocessable Entity
           │  {
           │    "type": "about:blank/business-rule",
           │    "title": "Business Rule Violation",
           │    "status": 422,
           │    "detail": "El nombre del afiliado no puede estar vacío",
           │    "instance": "/api/affiliates",
           │    "code": "VALIDATION_ERROR"
           │  }
           │
           └─ @ExceptionHandler(Exception)
              ↓
              HTTP 500 Internal Server Error
              {
                "type": "about:blank/server-error",
                "title": "Internal Server Error",
                "status": 500,
                "detail": "An unexpected error occurred",
                "instance": "/api/affiliates"
              }
```

---

## 7. Testing Pyramid

```
                    ▲
                   ╱ ╲
                  ╱   ╲
                 ╱  E2E ╲         (1-5 tests)
                ╱────────╲        - Full integration
               ╱          ╲       - HTTP endpoints
              ╱────────────╲
             ╱              ╲
            ╱  Integration   ╲    (5-15 tests)
           ╱   Tests         ╲   - With database
          ╱──────────────────╲   - With containers
         ╱                    ╲  - Testing adapters
        ╱──────────────────────╲
       ╱                        ╲
      ╱      Unit Tests          ╲ (50-100 tests)
     ╱    (Domain, Service)       ╲ - Pure Java
    ╱        No Spring            ╲ - With mocks
   ╱──────────────────────────────╲ - Fast execution
```

Each layer's testing:

```
DOMAIN Tests (pure Java):
├─ AffiliateTest.testCreateActive()
├─ AffiliateTest.testNameValidation()
├─ AffiliateTest.testDeactivate()
└─ AffiliateTest.testSalaryUpdate()

APPLICATION Tests (with mocks):
├─ CreateAffiliateServiceTest.testCreateSuccessfully()
├─ CreateAffiliateServiceTest.testRepositoryCalled()
└─ GetAffiliateServiceTest.testFindById()

INFRASTRUCTURE Tests (with containers):
├─ AffiliateJpaRepositoryTest.testSaveAndFind()
├─ AffiliateControllerTest.testCreateEndpoint()
├─ GlobalExceptionHandlerTest.testProblemDetail()
└─ RiskCentralClientTest.testAsyncCall()
```

---

## 8. Configuration & Beans

```
┌─────────────────────────────────────────┐
│   Spring Application Startup             │
└────────┬────────────────────────────────┘
         │
         ↓ @SpringBootApplication
┌─────────────────────────────────────────┐
│ CreditApplicationServiceApplication     │
│ - Main entry point                      │
│ - Enables component scanning            │
│ - Loads properties                      │
└────────┬────────────────────────────────┘
         │
         ↓ Scans packages
┌─────────────────────────────────────────┐
│ infrastructure/config/                  │
├─────────────────────────────────────────┤
│ @Configuration classes:                 │
│                                         │
│ - SecurityConfig.java                   │
│   ├─ JwtAuthFilter                      │
│   ├─ UserDetailsService                 │
│   └─ PasswordEncoder                    │
│                                         │
│ - PersistenceConfig.java (if needed)    │
│   ├─ DataSource                         │
│   └─ EntityManager                      │
│                                         │
│ - RestClientConfig.java (if needed)     │
│   └─ RestTemplate                       │
└────────┬────────────────────────────────┘
         │
         ↓ Scans repositories
┌─────────────────────────────────────────┐
│ infrastructure/adapter/persistence/     │
├─────────────────────────────────────────┤
│ @Repository beans:                      │
│                                         │
│ - AffiliateRepositoryAdapter            │
│ - ApplicationRepositoryAdapter          │
│ - Spring Data JPA Repositories          │
└────────┬────────────────────────────────┘
         │
         ↓ Scans services
┌─────────────────────────────────────────┐
│ application/service/                    │
├─────────────────────────────────────────┤
│ @Service beans:                         │
│                                         │
│ - CreateAffiliateService                │
│ - GetAffiliateService                   │
│ - CreateApplicationService              │
└────────┬────────────────────────────────┘
         │
         ↓ Scans external adapters
┌─────────────────────────────────────────┐
│ infrastructure/adapter/external/        │
├─────────────────────────────────────────┤
│ @Component beans:                       │
│                                         │
│ - RiskCentralClient                     │
└────────┬────────────────────────────────┘
         │
         ↓ Scans controllers
┌─────────────────────────────────────────┐
│ infrastructure/adapter/rest/            │
├─────────────────────────────────────────┤
│ @RestController beans:                  │
│                                         │
│ - AffiliateController                   │
│ - ApplicationController                 │
│ - GlobalExceptionHandler                │
└────────┬────────────────────────────────┘
         │
         ↓ IoC Container Ready
┌─────────────────────────────────────────┐
│ All beans wired with dependencies       │
│ Application starts listening on :8080   │
└─────────────────────────────────────────┘
```

---

## 9. Security Flow (JWT)

```
Client sends request:
GET /api/affiliates/1
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
│
↓
JwtAuthFilter (infrastructure/config/)
│
├─ Extract token from header
├─ Validate token signature
├─ Check expiration
├─ Extract user ID & roles
│
├─ If valid:
│  └─→ Create Authentication object
│      └─→ Set in SecurityContext
│
├─ If invalid:
│  └─→ Return HTTP 401 Unauthorized
│
└─→ Continue to controller

SecurityContext contains:
├─ Principal: UserId
├─ Authorities: [ROLE_AFILIADO, ROLE_ANALISTA, ROLE_ADMIN]
└─ Is Authenticated: true

Controller:
├─ Check @PreAuthorize("hasRole('ADMIN')")
├─ Access current user from SecurityContext
└─ Execute business logic
```

---

This visual reference shows how everything connects together in the hexagonal architecture!
