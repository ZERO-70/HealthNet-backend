# HealthNet Backend

HealthNet is a comprehensive healthcare management backend built with Spring Boot. It provides a robust set of APIs to manage patients, doctors, medical records, appointments, and hospital inventory.

## Features

- **User Management**: Role-based authentication (Admin, Doctor, Patient, Staff) with secure login and subscription tiers.
- **Medical Records**: Comprehensive medical record tracking including vitals, diagnoses, notes, and lab results.
  - Support for attaching files to medical records.
  - Built-in audit trail for medical record actions.
- **Appointments**: Schedule and manage doctor appointments.
- **Hospital Management**: Track departments, treatments, and hospital inventory.
- **AI Chat & Feedback**: Integrated chat interface for users to interact with the system and a suggestion module for feedback.

## Tech Stack

- **Framework**: Spring Boot (Java)
- **Security**: Spring Security with JWT (JSON Web Tokens)
- **Database**: SQL Database with JDBC/Spring Data (Repositories)
- **Build Tool**: Maven

## Database Schema (ERD)

The application relies on a comprehensive relational database schema.

- **`db/01_schema.sql`** — the authoritative schema (16 tables). This is what the
  database is actually created from.
- **`erd.html`** — interactive diagram of that schema. Open it in any browser.

> **Note**: `erd_diagram.png` is a stale snapshot of an earlier, inaccurate version
> of the diagram (it showed Java field names such as `recordId` rather than the real
> `record_id` columns, and the wrong primary keys for `doctor`/`patient`/`staff`).
> Use `erd.html` instead; the PNG can be regenerated from it or deleted.

See [SETUP.md](SETUP.md) for how to bring the database and services up locally.

## Project Structure

- `com.server.HealthNet.Controller` - REST API endpoints
- `com.server.HealthNet.Service` - Business logic layer
- `com.server.HealthNet.Repository` - Database access and queries
- `com.server.HealthNet.Model` - Data models and entities
- `com.server.HealthNet.SecurityConfig` - Authentication and authorization logic
