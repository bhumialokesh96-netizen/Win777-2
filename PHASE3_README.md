# Phase-3 Implementation: API Layer and JWT Security

## Overview

This document describes the Phase-3 implementation of the Win777 SMS Task Earning Backend, which includes:
- REST API Controllers
- JWT-based authentication and authorization
- Request/Response DTOs
- Global exception handling

## Architecture

### Layers

1. **Controller Layer** (`controller/`): Thin controllers that validate input and delegate to services
2. **Service Layer** (`service/`): Business logic for user, SMS job, wallet, and withdrawal operations
3. **Repository Layer** (`repository/`): Data access with Spring Data JPA
4. **Security Layer** (`security/`): JWT authentication and Spring Security configuration
5. **DTO Layer** (`dto/`): Request/Response data transfer objects
6. **Entity Layer** (`entity/`): JPA entities representing database tables
7. **Exception Layer** (`exception/`): Global exception handler

## API Endpoints

### Public Endpoints (No Authentication Required)

#### POST `/auth/register`
Register a new user.

**Request Body:**
```json
{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123",
  "phoneNumber": "+1234567890",
  "referralCode": "REF12345678" // Optional
}
```

**Response (201 Created):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "referralCode": "REFABCD1234"
}
```

#### POST `/auth/login`
Authenticate a user.

**Request Body:**
```json
{
  "username": "john_doe",
  "password": "securePassword123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe",
  "email": "john@example.com",
  "referralCode": "REFABCD1234"
}
```

### Protected Endpoints (JWT Authentication Required)

All protected endpoints require the `Authorization` header:
```
Authorization: Bearer <JWT_TOKEN>
```

#### POST `/api/sms-tasks/claim`
Claim the next available SMS job.

**Response (200 OK):**
```json
{
  "jobId": "660e8400-e29b-41d4-a716-446655440000",
  "recipientNumber": "+1234567890",
  "messageContent": "Hello, this is a test message.",
  "claimedAt": "2024-01-21T10:30:00"
}
```

#### POST `/api/sms-tasks/complete`
Complete a claimed SMS job.

**Request Body:**
```json
{
  "jobId": "660e8400-e29b-41d4-a716-446655440000"
}
```

**Response (200 OK):**
```json
"Job completed successfully"
```

#### GET `/api/wallet/balance`
Get wallet balance for the authenticated user.

**Response (200 OK):**
```json
{
  "balance": 1250.50
}
```

#### GET `/api/wallet/transactions`
Get transaction history for the authenticated user.

**Query Parameters:**
- `page` (default: 0): Page number
- `size` (default: 20): Page size

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "770e8400-e29b-41d4-a716-446655440000",
      "amount": 10.00,
      "ledgerType": "EARNINGS",
      "description": "SMS job completion earnings",
      "referenceId": "660e8400-e29b-41d4-a716-446655440000",
      "createdAt": "2024-01-21T10:35:00"
    }
  ],
  "pageable": { ... },
  "totalElements": 50,
  "totalPages": 3
}
```

#### POST `/api/withdrawals`
Create a withdrawal request.

**Request Body:**
```json
{
  "amount": 100.00,
  "paymentMethod": "Bank Transfer",
  "paymentDetails": "Account: 1234567890"
}
```

**Response (201 Created):**
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440000",
  "amount": 100.00,
  "status": "PENDING",
  "paymentMethod": "Bank Transfer",
  "paymentDetails": "Account: 1234567890",
  "createdAt": "2024-01-21T11:00:00",
  "processedAt": null
}
```

#### GET `/api/withdrawals`
Get withdrawal history for the authenticated user.

**Query Parameters:**
- `page` (default: 0): Page number
- `size` (default: 20): Page size

**Response (200 OK):**
```json
{
  "content": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440000",
      "amount": 100.00,
      "status": "PENDING",
      "paymentMethod": "Bank Transfer",
      "paymentDetails": "Account: 1234567890",
      "createdAt": "2024-01-21T11:00:00",
      "processedAt": null
    }
  ],
  "pageable": { ... },
  "totalElements": 5,
  "totalPages": 1
}
```

## Error Responses

### 400 Bad Request
Invalid input or validation errors.

```json
{
  "status": 400,
  "errors": {
    "username": "Username is required",
    "email": "Email must be valid"
  },
  "timestamp": "2024-01-21T11:00:00"
}
```

### 409 Conflict
Business rule violation (e.g., daily SMS limit reached, insufficient balance).

```json
{
  "status": 409,
  "message": "Daily SMS limit reached",
  "timestamp": "2024-01-21T11:00:00"
}
```

### 401 Unauthorized
Missing or invalid JWT token.

### 500 Internal Server Error
Unexpected server error.

```json
{
  "status": 500,
  "message": "An unexpected error occurred. Please try again later.",
  "timestamp": "2024-01-21T11:00:00"
}
```

## Security

### JWT Authentication

- All endpoints except `/auth/*` require JWT authentication
- JWT token is passed in the `Authorization` header as `Bearer <token>`
- Token contains `userId` and `username` claims
- Token expiration: 24 hours (configurable via `jwt.expiration` property)

### Password Security

- Passwords are hashed using BCrypt before storage
- BCrypt strength: 10 rounds (Spring Security default)

### CSRF Protection

CSRF protection is disabled for this stateless REST API as:
1. JWT tokens are stored in headers, not cookies
2. API is stateless with no session management
3. Each request is authenticated independently

## Configuration

### application.properties

```properties
# JWT Configuration
jwt.secret=${JWT_SECRET:Win777SecretKeyForJWTTokenGenerationAndValidation2024}
jwt.expiration=86400000

# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://localhost:5432/win777db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=update
```

**Important:** In production, set a strong JWT secret via environment variable:
```bash
export JWT_SECRET=your_strong_secret_key_minimum_32_characters_long
```

## Business Logic

### SMS Job Workflow

1. User calls `/api/sms-tasks/claim` to claim a PENDING job
2. Job status changes to CLAIMED and is assigned to the user
3. User completes the SMS and calls `/api/sms-tasks/complete`
4. System validates job ownership and status
5. Job status changes to COMPLETED
6. User earns SMS completion amount (from active SMS rate config)
7. Referral rewards distributed (10% L1, 2% L2, 1% L3)
8. Daily SMS counter incremented

### Wallet Operations

- Wallet balance is calculated dynamically by summing all ledger entries
- Transaction types: EARNINGS, REFERRAL_LEVEL_1/2/3, WITHDRAWAL, ADMIN_CREDIT/DEBIT
- Read-only operations (balance and transaction history)
- Append-only ledger pattern ensures audit trail

### Withdrawal Workflow

1. User requests withdrawal via `/api/withdrawals`
2. System checks balance (must have sufficient funds)
3. Withdrawal request created with PENDING status
4. Funds immediately debited from wallet (prevents double-spending)
5. Admin processes withdrawal (separate admin workflow - not in Phase-3)
6. If rejected, admin must create credit entry to refund

## Testing

### Running Tests

```bash
mvn clean test
```

### Building the Application

```bash
mvn clean package
```

### Running the Application

```bash
mvn spring-boot:run
```

Or with custom properties:

```bash
export JWT_SECRET=your_secret_key
mvn spring-boot:run
```

## Implementation Notes

### Controllers

- All controllers are thin wrappers
- No business logic in controllers
- Extract `userId` from JWT via `Authentication.getPrincipal()`
- Delegate to service layer for all operations
- Use `@Valid` for automatic DTO validation

### Services

- All business logic resides in services
- Transactional boundaries defined with `@Transactional`
- Services throw `IllegalArgumentException` for invalid input
- Services throw `IllegalStateException` for business rule violations

### DTOs

- Separate request and response DTOs
- Validation annotations on request DTOs
- No entity objects exposed in API responses

### Exception Handling

- Global exception handler with `@RestControllerAdvice`
- Consistent error response format
- Appropriate HTTP status codes
- Secure error messages (no internal details leaked)

## Next Steps (Out of Scope for Phase-3)

The following features are explicitly NOT included in Phase-3:

1. Admin APIs (withdrawal approval, user management, etc.)
2. Schedulers or async operations
3. JWT refresh tokens
4. SMS sending integration
5. Payment gateway integration
6. User profile management
7. Password reset functionality

These features may be implemented in future phases.
