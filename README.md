# Jobsy - REST API

> For the Spanish version, click here → [Versión en Español](./README-ES.md)

## Description

**Jobsy** is a platform that connects local service providers (such as plumbers, electricians, tutors, and others) with clients who need their services.  
This RESTful API enables the management of users, provider verification, services, bookings, reviews, and real-time communication.

## Objective

To provide a scalable and secure platform that efficiently connects clients and verified service providers through CRUD operations, JWT authentication, and marketplace-specific functionalities.

---

## User Roles

* **USER**: Default role after registration. Can search services, book providers, and leave reviews.  
  May apply to become a provider by submitting verification documents.
* **PROVIDER**: Verified user approved by an admin. Can publish services, manage availability, and receive bookings.  
  Must first submit a verification request (INE, photos, and profile details).
* **ADMIN**: Oversees users and provider requests, manages categories, and maintains system integrity.  
  Can approve or reject provider verification requests and manage platform content.
* **SUPER_ADMIN**: Highest privilege role. Can manage administrators, perform critical system configurations, access all logs and audits, and restore or delete sensitive data.  
  Responsible for global security and advanced platform maintenance.

---

## Main Features

### Authentication and Authorization

* Registration and login with JWT
* Role-based access control (RBAC)
* Token validation and refresh
* Secure password encryption (BCrypt)

---

### User and Provider Verification

* Users can submit a **Provider Request** including:
    - Bio
    - Address
    - RFC Homoclave
    - CURP
    - Identification documents (INE photos and selfies)
* Admins manually review and approve/reject requests.
* Upon approval, the user’s role changes from **USER** → **PROVIDER**.

---

### Provider and Service Management

* Full CRUD for verified providers’ offerings
* Customizable categories, descriptions, and hourly rates
* Optional certifications and work portfolio
* Geolocation support for service area radius

---

### Booking System

* Clients can book appointments with providers
* Booking statuses: `PENDING`, `CONFIRMED`, `COMPLETED`, `CANCELLED`
* Providers manage their availability slots
* Notifications for booking updates and confirmations

---

### Reviews and Ratings

* Clients can leave reviews after completed services
* Automatic average rating per provider
* Textual feedback for transparency

---

### Geolocation and Search

* Search for nearby providers by location
* Filters by category, distance, and rating
* Location-optimized results

---

### Real-Time Messaging and Notifications

* Chat between client and provider using WebSockets
* Linked to specific bookings
* Email and in-app notifications for system events

---

### Admin Dashboard

* View and approve/reject provider verification requests
* Manage service categories
* Monitor user activity and system metrics

---

## Technologies

* **Framework:** Spring Boot 4.0
* **Language:** Java 21
* **Database:** PostgreSQL 17.5+
* **Authentication:** JWT (JSON Web Tokens)
* **Security:** Spring Security
* **Persistence:** Spring Data JPA
* **Mapping:** MapStruct
* **Documentation:** SpringDoc OpenAPI (Swagger)
* **File management:** Cloudinary
* **Email:** SendGrid
* **Validation:** Jakarta Bean Validation
* **Caching:** Caffeine
* **Build:** Maven
* **Real-time communication:** WebSockets

---

## Installation and Setup

### Prerequisites

* Java 21+
* Maven 3.6+
* PostgreSQL 17.5+

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/FrankSkep/jobsy-rest-api.git
   cd jobsy-rest-api
   ```

2. **Configure environment variables**
   Create a `.env` file in the project root:

   ```properties
   DB_URL=jdbc:postgresql://localhost:5432/jobsy
   DB_USERNAME=your_username
   DB_PASSWORD=your_password
   JWT_SECRET=your_secret_key
   JWT_EXPIRATION_MS=your_expiration_time_in_ms
   CLOUDINARY_API_SECRET=your_cloudinary_api_secret
   CLOUDINARY_API_KEY=your_cloudinary_api_key
   CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
   MAIL_HOST=smtp.gmail.com
   MAIL_PORT=587
   MAIL_USERNAME=your_email
   MAIL_PASSWORD=your_password
   ```

3. **Install dependencies**

   ```bash
   ./mvnw clean install
   ```

4. **Run the application**

   ```bash
   ./mvnw spring-boot:run
   ```

   The API will be available at `http://localhost:8080`.

---

## API Documentation

Once the application is running, access the interactive documentation:

* **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`

---

## License

This project is licensed under the **MIT License**.

---

##  Author

**FrankSkep** — [GitHub](https://github.com/FrankSkep)
