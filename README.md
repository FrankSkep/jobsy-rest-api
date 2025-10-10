# Jobsy - REST API

## Description

**Jobsy** is a platform that connects local service providers (plumbers, electricians, tutors, etc.) with clients looking to hire them. This RESTful API enables the management of users, services, bookings, payments, reviews, and real-time communication.

## Objective

To provide a scalable and secure platform that efficiently connects clients and service providers through CRUD operations, JWT authentication, and marketplace-specific functionalities.

## User Roles

* **User**: Can act as both client and service provider. As a client, they can search for services, book appointments, make payments, and leave reviews. As a provider, they can offer services, manage their availability, and handle bookings.
* **Administrator**: Oversees users, manages system services, and has full access to platform administration.

## Main Features

### Authentication and Authorization

* Registration and login with JWT
* Role-based access control (RBAC)
* Endpoint-level permission management

### Provider and Service Management

* Full CRUD for provider profiles
* Creation and management of services
* Customizable categories, descriptions, and rates

### Booking System

* Creation and management of appointments
* Statuses: pending, confirmed, completed, canceled
* Configurable provider availability

### Payments (Simulation)

* Integration with payment gateways (Stripe/PayPal sandbox)
* Deposit system
* Complete transaction history

### Reviews and Ratings

* Customer rating system
* Comments and feedback
* Automatic average rating calculation per provider

### Geolocation and Search

* Search for nearby providers
* Filters by category, price, and rating
* Location-optimized results

### Real-Time Messaging

* Chat between client and provider using WebSockets
* Linked to specific bookings
* Instant notifications

### Dashboards

* **As Client**: Booking, payment, and review history
* **As Provider**: Schedule, completed services, and ratings
* **Admin**: Complete system overview and user management

## Technologies

* **Framework**: Spring Boot 3.2.8
* **Language**: Java 21
* **Database**: PostgreSQL
* **Authentication**: JWT (JSON Web Tokens)
* **Security**: Spring Security
* **Persistence**: Spring Data JPA
* **Documentation**: SpringDoc OpenAPI (Swagger)
* **Real-time communication**: WebSockets
* **File management**: Cloudinary
* **Email**: Spring Mail
* **Validation**: Spring Boot Validation
* **Build**: Maven

## Installation and Setup

### Prerequisites

* Java 21+
* Maven 3.6+
* PostgreSQL 16+

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

## API Documentation

Once the application is running, access the interactive documentation:

* **Swagger UI**: `http://localhost:8080/swagger-ui.html`
* **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

## Security

* JWT-based authentication
* Passwords encrypted with BCrypt
* Role-based access control (USER, ADMIN)
* Input data validation
* CSRF protection
* HTTP security headers

## License

This project is licensed under the MIT License.

## Author

**FrankSkep** - [GitHub](https://github.com/FrankSkep)