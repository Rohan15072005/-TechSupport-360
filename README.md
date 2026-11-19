# TechSupport 360

**Smart IT Service & Support Management System** — a Java full-stack portfolio project for managing employee support requests, assigning agents, tracking ticket progress, and monitoring service deadlines.

![Java 21](https://img.shields.io/badge/Java-21-orange) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-green) ![React](https://img.shields.io/badge/React-19-149eca) ![MySQL](https://img.shields.io/badge/MySQL-8-blue)

## Technology stack

- **Backend:** Java 21, Spring Boot, Spring Security, Spring Data JPA, Hibernate, Maven
- **Frontend:** React 19, Vite, React Router, Axios
- **Database:** MySQL 8+
- **Authentication:** BCrypt password hashing and JWT bearer tokens
- **API:** REST, JSON

## Core capabilities

- Public registration for standard employee/tenant accounts; administrative roles are not selectable during public registration.
- JWT login and protected routes.
- Role-aware access for administrators, support agents, and tenant users.
- Create and view support tickets, assign tickets to support agents, and update ticket status.
- Priority and category management.
- Ticket activity history for creation, assignment, and status changes, visible to ticket owners, assigned agents, and administrators.
- Category-based SLA resolution deadlines when an SLA is configured.
- Dashboard counts for total, open, closed, resolved, and overdue tickets.
- Tenant directory and feedback modules.
- Optional bootstrap administrator using environment variables.

## Project structure

```text
TechSupport-360/
├── helpdesk/                 # Spring Boot REST API
│   ├── src/main/java/com/helpdesk/
│   ├── src/main/resources/application.properties
│   └── pom.xml
├── helpdesk-ui/              # React + Vite frontend
│   ├── src/api/
│   ├── src/components/
│   ├── src/pages/
│   └── package.json
├── docker-compose.yml        # Local MySQL service
├── .env.example              # Configuration template
└── README.md
```

## Requirements

- JDK 21
- Node.js 20.19+ (or 22.12+ recommended for Vite 8)
- npm
- MySQL 8+ **or** Docker Desktop

## 1. Start MySQL

From the project root, copy `.env.example` to `.env` and set `DB_PASSWORD` to a local development password, then run:

```bash
docker compose up -d mysql
```

This starts MySQL on `localhost:3306`, creates database `techsupport360`, and persists data in a Docker volume. For an existing local MySQL installation, create the database manually:

```sql
CREATE DATABASE techsupport360;
```

## 2. Configure the backend

Open `helpdesk/src/main/resources/application.properties` or set environment variables in your terminal. At minimum, set:

- `DB_USERNAME` — your MySQL username
- `DB_PASSWORD` — your MySQL password
- `JWT_SECRET` — a unique random string with at least 32 characters

Optional first administrator: set all three `ADMIN_NAME`, `ADMIN_EMAIL`, and `ADMIN_PASSWORD` variables before starting the backend. The password must contain at least 12 characters. The application creates this admin only if that email does not already exist. Keep these values private and never commit them.

Windows PowerShell example:

```powershell
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_mysql_password"
$env:JWT_SECRET="replace-this-with-a-long-random-secret-value"
$env:ADMIN_NAME="System Administrator"
$env:ADMIN_EMAIL="admin@example.com"
$env:ADMIN_PASSWORD="Use-A-Strong-Password-123"
cd helpdesk
.\mvnw.cmd spring-boot:run
```

The backend runs at `http://localhost:8080`. Hibernate creates/updates tables in the configured database (`DDL_AUTO=update` by default for local development).

## 3. Start the frontend

Open a second terminal:

```powershell
cd helpdesk-ui
npm install
npm run dev
```

Open `http://localhost:5173`. To use a different backend URL, create `helpdesk-ui/.env` with:

```env
VITE_API_BASE_URL=http://localhost:8080
```

## 4. Main API endpoints

| Method | Endpoint | Purpose |
|---|---|---|
| POST | `/api/auth/register` | Register a standard account |
| POST | `/api/auth/login` | Authenticate and receive JWT |
| POST | `/api/tickets` | Create a ticket |
| GET | `/api/tickets` | Admin ticket queue |
| GET | `/api/tickets/my` | Tickets created by user / assigned to agent |
| GET | `/api/tickets/{id}/history` | Ticket activity history (owner, assigned agent, or admin) |
| PUT | `/api/tickets/{id}/assign` | Assign ticket to support agent (admin) |
| PUT | `/api/tickets/{id}/status` | Update status (admin or assigned agent) |
| GET | `/api/dashboard` | Ticket and SLA metrics (admin/support agent) |
| GET/POST | `/api/sla` | Create/list category SLA rules (admin) |
| GET/POST | `/api/escalations` | Manage escalation rules (admin; configuration only) |
| GET/POST | `/api/categories` | Category operations (admin) |
| GET/POST | `/api/feedback` | Feedback operations |

All endpoints except authentication require `Authorization: Bearer <token>` where applicable.

## Security notes

- Public self-registration always creates the `TENANT` role. It ignores any client-supplied role field.
- Passwords are stored using BCrypt.
- Admin-only routes are protected by Spring Security.
- Ticket status changes are limited to administrators and the agent assigned to that ticket.
- Ticket assignment is restricted to administrators and support-agent accounts.
- The included JWT fallback secret is for local development only. Set a unique secret before deployment.
- Configure a restrictive `FRONTEND_ORIGIN` in production and use HTTPS.

## Current scope / honest limitations

- The tenant directory is present, but the current data model does not yet associate each user and ticket with a tenant identifier. Therefore, strict multi-tenant row-level isolation is **not** implemented.
- SLA deadlines are assigned at ticket creation when the ticket category has an SLA resolution-time rule. The dashboard reports overdue unresolved tickets. Automatic background escalation and outbound email delivery are not included in this version.
- The project should be tested against your own MySQL credentials and local environment before deployment. Do not claim untested features in a resume or interview.

## Suggested verification checklist

1. Register a standard account and verify it cannot self-assign an ADMIN role.
2. Log in and create a ticket with a configured category.
3. Create a support-agent account through the admin user management screen.
4. Log in as admin and assign the ticket to the support agent.
5. Log in as that agent and update the ticket status.
6. Configure an SLA for a category and verify a new ticket receives a due date.
7. Verify that an unassigned agent cannot update another agent's ticket.

## Resume project entry (after completing and verifying the setup)

**TechSupport 360 – Smart IT Service & Support Management System**  
*Java 21, Spring Boot, React, MySQL, Spring Security, JWT, Spring Data JPA*

- Built a Java full-stack support portal for creating, assigning, and tracking IT service requests through REST APIs.
- Implemented JWT authentication, BCrypt password hashing, and role-based authorization for administrator, support-agent, and employee workflows.
- Integrated MySQL using Spring Data JPA and added category-based SLA due dates and overdue-ticket dashboard metrics.

Only list the features you have personally run, tested, and can explain.
