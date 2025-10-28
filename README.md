# Multi-Tenant Resource Management System

The Multi-Tenant Resource Management System is a Spring Boot application designed to
handle multiple tenants (organizations) within a single codebase.
Each tenant’s data is securely isolated using Schema-Based Multi-Tenancy, ensuring scalability, 
maintainability, and enhanced data privacy.
This project demonstrates modern enterprise application architecture — combining Spring Boot, JPA/Hibernate, JWT Authentication, and Caching to deliver a flexible and secure backend solution.

## Tech Stack

| Category | Technology |
|----------|------------|
| Language | Java 17+ |
| Framework | Spring Boot 3.x |
| Database | H2 (in-memory) |
| ORM | Hibernate |
| Authentication | JWT |
| Build Tool | Maven |
| IDE | IntelliJ IDEA |
| Multi-Tenancy | Schema-based (per-tenant schemas) |

## Project Structure
```
src/
 ├── main/
 │   ├── java/com/edstruments/multitenantresourcemanagement/
 │   │   ├── config/multitenancy/      # Multi-tenancy configuration classes
 │   │   ├── controller/               # REST controllers
 │   │   ├── entity/                   # JPA entities (User, Tenant, Resource, AuditLog)
 │   │   ├── enums/                    # Enum classes (UserRole, AuditAction)
 │   │   ├── repository/               # JPA repositories
 │   │   ├── security/                 # Security and JWT configuration
 │   │   ├── service/                  # Business services
 │   │   └── MultiTenantResourceManagementApplication.java
 │   └── resources/
 │       ├── application.properties    # Application configuration
 │       └── data.sql                  # Initial data load
 └── test/java/                        # Test cases
```

 ## Features

| Feature | Description |
|---------|-------------|
| Schema-Based Multi-Tenancy | Isolates each tenant's data in its own schema |
| In-Memory H2 Database | Lightweight and perfect for local development or demos |
| JWT Authentication | Login and authorization per tenant |
| User & Tenant Management | Create and manage users under specific tenants |
| Resource Management | CRUD operations for tenant resources |
| Audit Logging | Tracks create/update/delete actions |
| Dynamic Tenant Resolution | Tenant auto-detected via HTTP header (X-Tenant-ID) |
| JPA Auditing | Automatically captures created and updated timestamps |

## Setup & Run
```
1. Clone Repository
git clone https://github.com/HimaVachhani/multi-tenant-resource-management.git
cd multi-tenant-resource-management

2. Run Application
Since H2 is in-memory, no external DB setup is required.
mvn spring-boot:run

3. Access Application
API Base URL: http://localhost:8080
H2 Console: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:multitenantdb
Username: sa
Password: (leave blank)
```

## Authentication & Tenant Handling
```
Login using /api/auth/login → receive a JWT token.
Include the token in request headers:

Authorization: Bearer <token>
X-Tenant-ID: tenant1

The app switches the database schema based on the X-Tenant-ID value dynamically.
```

## Authentication Flow
```
/auth/login → Authenticates user with username & password.
On success, returns a JWT token.
Include the token in future requests:

Authorization: Bearer <your_token_here>
Optionally, include:
X-Tenant-ID: tenant_1
```

## Testing via Postman

```
POST → http://localhost:8080/auth/login
Body (JSON):

{
  "username": "adminA",
  "password": "password123"
}


Response:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR..."
}


Then use this token for authorized API calls.
``` 
## Multi-Tenancy Implementation
```
SchemaBasedMultiTenantConnectionProvider — routes DB connections to the correct tenant schema.
CurrentTenantIdentifierResolverImpl — resolves tenant identifier dynamically.
TenantContext — holds the current tenant info in thread-local scope.
Each request is filtered by TenantFilter, which reads the tenant ID from headers.
```

## Useful Commands
Here's the markdown table for the commands:

| Command | Description |
|---------|-------------|
| `mvn clean install` | Build the project |
| `mvn spring-boot:run` | Run application |
| `mvn test` | Execute tests |

```

Step 1: Create a Tenant

POST → http://localhost:8080/api/tenants

Body (JSON):

{
  "name": "TenantA",
  "schemaName": "tenant_a"
}


Expected response:

{
  "id": 1,
  "name": "TenantA",
  "schemaName": "tenant_a",
  "isActive": true
}

Step 2: Create a User for that Tenant

POST → http://localhost:8080/api/users

Body (JSON):

{
  "username": "adminA",
  "password": "password123",
  "role": "ADMIN",
  "tenantId": 1
}

Expected response:

{
  "id": 1,
  "username": "adminA",
  "role": "ADMIN",
  "tenantId": 1
}

 Step 3: Login (JWT authentication)

POST → http://localhost:8080/api/auth/login

Body (JSON):

{
  "username": "adminA",
  "password": "password123"
}

Response:

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5..."
}

Copy this token — you’ll use it for the next requests.

Step 4: Create a Resource (with tenant context)

POST → http://localhost:8080/api/resources

Add this header:

Authorization: Bearer <your-JWT-token>

Body:

{
  "name": "Report1",
  "description": "Test resource",
  "tenantId": 1,
  "ownerId": 1
}
Expected response:

{
  "id": 1,
  "name": "Report1",
  "tenantId": 1
}
Step 5: List all resources for that tenant

GET →
http://localhost:8080/api/resources?tenantId=1

Response:

[
  {
    "id": 1,
    "name": "Report1",
    "description": "Test resource",
    "tenantId": 1
  }
]

Step 6: Check H2 Console

Visit: http://localhost:8080/h2-console

JDBC URL:
jdbc:h2:mem:tenantdb
You can manually verify tables like TENANTS, USERS, RESOURCES.
```

Here's the markdown table for the controllers:

| Controller | Path Prefix | Description |
|------------|-------------|-------------|
| TenantController | `/api/tenants` | Create, list, delete tenants |
| UserController | `/api/users` | Create, list, delete users |
| ResourceController | `/api/resources` | Create, update, delete, list resources |
| AuthController | `/api/auth` | Login, JWT generation |
