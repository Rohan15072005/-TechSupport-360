# Changelog

## TechSupport 360 upgrade

- Rebranded the application, frontend navigation, browser title, and project documentation.
- Replaced PostgreSQL configuration and driver with MySQL Connector/J and environment-based connection settings.
- Added Docker Compose for a local MySQL 8.4 instance and example environment files.
- Hardened registration so public sign-up always creates a standard TENANT account; administrative accounts are bootstrapped from environment variables.
- Improved JWT secret validation and invalid/expired-token handling.
- Replaced wildcard CORS annotations with a configurable frontend origin.
- Tightened API permissions for admin-only management endpoints, ticket assignment, and ticket status changes.
- Added server-side checks so only the ticket owner can submit feedback and only after resolution; duplicate feedback and invalid ratings are rejected.
- Added SLA-based due dates and overdue-ticket dashboard metrics.
- Added ticket activity history for creation, assignment, and status changes, with role/ownership checks on history access.
- Updated the React UI for the new brand, responsive layout, support-agent ticket status updates, SLA dates, and ticket activity history.
- Replaced conflicting `.gitignore` content and updated setup/API documentation.

## Known limitations

- Strict tenant row-level data isolation is not implemented because the supplied data model does not connect each user and ticket to a tenant ID.
- SMTP email delivery, knowledge-base article management, and scheduled automatic escalation are not implemented in this version.
- Full Maven and Vite builds require downloading dependencies; those downloads were unavailable in the editing environment, so run the documented build steps locally before deployment.
