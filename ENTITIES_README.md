# Win777 SMS Task Earning Backend - JPA Entities

## Overview
This project contains the JPA entity definitions for the Win777 SMS Task Earning backend application. All entities follow Hibernate best practices and implement the specified functional requirements.

## Entities

### 1. User (`users` table)
Tracks user information, daily SMS limits, and referral tree.

**Key Features:**
- UUID primary key with auto-generation
- Daily SMS tracking (`dailySmsLimit`, `dailySmsSentCount`, `lastSmsResetDate` as `LocalDate`)
- Referral tree support with self-referencing relationship
- Automatic timestamp management with `@CreationTimestamp` and `@UpdateTimestamp`

### 2. SMSJob (`sms_jobs` table)
Manages SMS jobs with a clear status flow and nullable user assignment.

**Key Features:**
- UUID primary key with auto-generation
- **Nullable `user_id`** - allows `PENDING` status without user assignment
- Status flow: `PENDING` → `CLAIMED` (user assigned) → `COMPLETED`/`FAILED`
- Timestamp tracking for claimed, completed, and failed states
- Automatic auditing with creation and update timestamps

### 3. WalletLedger (`wallet_ledger` table)
Immutable ledger for all wallet transactions.

**Key Features:**
- UUID primary key with auto-generation
- **BigDecimal for `amount`** (precision 19, scale 2)
- **Constraint: amount must NOT equal zero** (validated via `@PrePersist` and `@PreUpdate`)
- Immutable design - all fields marked as `updatable = false`
- Only `@CreationTimestamp` (no update timestamp for immutable records)
- Transaction type support via enum
- Reference ID for linking to related entities

### 4. Withdrawal (`withdrawals` table)
Tracks withdrawal requests with status management.

**Key Features:**
- UUID primary key with auto-generation
- **BigDecimal for `amount`** (precision 19, scale 2)
- **Constraint: amount must be greater than zero** (validated via `@PrePersist` and `@PreUpdate`)
- Status tracking via enum (`PENDING`, `APPROVED`, `REJECTED`, `COMPLETED`)
- Payment method and details support
- Processing information (processed by, transaction reference)
- Automatic auditing with creation and update timestamps

### 5. SMSRateConfig (`sms_rate_config` table)
Backend configuration for SMS earning rates.

**Key Features:**
- UUID primary key with auto-generation
- **BigDecimal for `smsEarningRate`** (precision 19, scale 2)
- **`isActive` boolean flag** - backend controls single active configuration
- No user relationships (backend-managed configuration)
- Additional configuration fields (minimum payout, maximum daily earnings)
- Automatic auditing with creation and update timestamps

## Enums

### SMSJobStatus
- `PENDING` - Job created but not claimed by any user
- `CLAIMED` - User has claimed the job
- `COMPLETED` - Job successfully completed
- `FAILED` - Job execution failed

### WithdrawalStatus
- `PENDING` - Withdrawal request submitted
- `APPROVED` - Withdrawal approved by admin
- `REJECTED` - Withdrawal rejected
- `COMPLETED` - Withdrawal processed and completed

### TransactionType
- `SMS_EARNING` - Earnings from completing SMS jobs
- `REFERRAL_BONUS` - Bonus from referrals
- `WITHDRAWAL` - Withdrawal transaction
- `ADMIN_CREDIT` - Admin-initiated credit
- `ADMIN_DEBIT` - Admin-initiated debit

## Key Implementation Details

### UUID Generation
All entities use `@GeneratedValue(strategy = GenerationType.UUID)` for automatic UUID generation.

### Monetary Fields
All monetary fields use `BigDecimal` with precision 19 and scale 2 for accurate financial calculations:
- `WalletLedger.amount`
- `Withdrawal.amount`
- `SMSRateConfig.smsEarningRate`
- `SMSRateConfig.minimumPayout`
- `SMSRateConfig.maximumDailyEarnings`

### Timestamp Management
- **Creation timestamps**: `@CreationTimestamp` with `updatable = false`
- **Update timestamps**: `@UpdateTimestamp` (not used for immutable entities like WalletLedger)

### Constraints
- **WalletLedger**: Amount must not be equal to zero (can be positive or negative)
- **Withdrawal**: Amount must be greater than zero

### Table Naming
All entities have explicit `@Table` annotations to avoid default naming conventions:
- `users`
- `sms_jobs`
- `wallet_ledger`
- `withdrawals`
- `sms_rate_config`

### Multi-SIM Support
The backend is agnostic to SIM-specific data. No SIM-related fields are included in any entities.

### Nullable User in SMSJob
The `user_id` foreign key in SMSJob is nullable, supporting the workflow where:
1. Jobs start in `PENDING` status without a user
2. User claims the job, status changes to `CLAIMED`, and `user_id` is set
3. Job is then completed or fails

## Hibernate Best Practices Followed

1. ✅ Explicit table names using `@Table`
2. ✅ UUID generation using JPA standard `@GeneratedValue`
3. ✅ Proper use of `@CreationTimestamp` and `@UpdateTimestamp`
4. ✅ Immutable fields marked with `updatable = false`
5. ✅ BigDecimal for monetary values with appropriate precision and scale
6. ✅ Lazy loading for associations
7. ✅ Proper enum handling with `@Enumerated(EnumType.STRING)`
8. ✅ Validation constraints using `@PrePersist` and `@PreUpdate`
9. ✅ Proper column definitions with nullability constraints

## Dependencies

The project uses Spring Boot 3.2.1 with:
- Spring Data JPA
- Hibernate ORM
- PostgreSQL driver
- Jakarta Persistence API
- Bean Validation API

## Usage

These entities can be used with Spring Data JPA repositories for database operations. The wallet balance is dynamically computed by summing all entries in the WalletLedger for a user.

## Example: Wallet Balance Calculation

```java
// In a service or repository
@Query("SELECT COALESCE(SUM(wl.amount), 0) FROM WalletLedger wl WHERE wl.user.id = :userId")
BigDecimal calculateWalletBalance(@Param("userId") UUID userId);
```

## Notes

- No default values are set for `lastSmsResetDate` in User entity (it's nullable)
- WalletLedger is fully immutable after creation
- SMSRateConfig's `isActive` flag ensures only one configuration is active at a time (enforced at service layer)
