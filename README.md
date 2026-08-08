# HackSync - Online Hackathon Management System Backend

This repository contains the backend service for the **Online Hackathon Management System**, a comprehensive platform designed to facilitate and manage online hackathons. The backend is built using **Java** and **Spring Boot**, providing robust APIs for authentication, participant management, mentoring, judging, and administration.

## 🚀 Features & Modules

The platform is designed with role-based access and modular architecture to cater to various users involved in a hackathon:

*   **Auth Module:** Secure user registration, login, and JWT-based authentication.
*   **Admin Core:** Administrative controls to oversee the platform and hackathons.
*   **Host Core:** Features for hackathon organizers to create, configure, and manage events.
*   **Participants Core:** Functionalities for participants to register, form teams, and submit projects.
*   **Mentor Core:** Tools for mentors to guide teams and provide feedback.
*   **Judge Core:** Capabilities for judges to evaluate projects based on specific criteria.
*   **Real-time Communication:** Integrated WebSockets for live notifications or chat.
*   **External Integrations:**
    *   **Twilio:** For SMS and OTP verification.
    *   **ImageKit:** For efficient image uploading, storage, and delivery.
    *   **JaaS (Jitsi as a Service):** For embedded video conferencing and live meetings.
    *   **Email (SMTP):** For automated system emails and alerts.

## 🛠️ Technology Stack

*   **Java Version:** 21
*   **Framework:** Spring Boot 3.5.5
*   **Database:** MySQL
*   **ORM:** Spring Data JPA / Hibernate
*   **Security:** Spring Security + JWT (JSON Web Tokens)
*   **Documentation:** Swagger / OpenAPI (SpringDoc)
*   **Utilities:** Lombok, Spring Boot Actuator, Spring Boot DevTools

## ⚙️ Prerequisites

Before you begin, ensure you have the following installed on your local machine:
*   [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/downloads/#java21)
*   [Apache Maven](https://maven.apache.org/download.cgi)
*   [MySQL Server](https://dev.mysql.com/downloads/mysql/)

## 🔧 Environment Setup

The application relies on several environment variables for configuration, security, and third-party integrations. 
Create a `.env` file in the root directory of the project (same level as `pom.xml`) and configure the following variables:

```properties
# Database Configuration
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/your_database_name?useSSL=false
SPRING_DATASOURCE_USERNAME=your_db_username
SPRING_DATASOURCE_PASSWORD=your_db_password

# JWT Security
JWT_SECRET_KEY=your_super_secret_jwt_key_that_is_long_enough

# Email / SMTP Configuration
SUPPORT_EMAIL=your_email@gmail.com
APP_PASSWORD=your_gmail_app_password

# Twilio Integration
TWILIO_ACCOUNT_SID=your_twilio_account_sid
TWILIO_AUTH_TOKEN=your_twilio_auth_token
TWILIO_VERIFICATION_SSID=your_twilio_verification_service_sid

# ImageKit Integration
IMAGEKIT_PRIVATE_KEY=your_imagekit_private_key

# JaaS (Jitsi) Integration
JASS_PRIVATE_KEY=your_jaas_private_key_path_or_string
```

*Note: Ensure your MySQL server is running and the database specified in `SPRING_DATASOURCE_URL` is created.*

## 🏃‍♂️ Running the Application

1. **Clone the repository** (if you haven't already).
2. **Navigate to the backend directory:**
   ```bash
   cd "Backend/Hackathon"
   ```
3. **Build the project** using Maven:
   ```bash
   mvn clean install
   ```
   *(To skip tests during build, use `mvn clean install -DskipTests`)*
4. **Run the Spring Boot application:**
   ```bash
   mvn spring-boot:run
   ```
   Alternatively, you can run the application directly from your IDE by executing the main application class.

The application will start by default on port `8080` (unless specified otherwise in your environment/properties).

## 📚 API Documentation (Swagger)

The API is fully documented using OpenAPI/Swagger. Once the application is running, you can explore and test the endpoints via the Swagger UI:

*   **Swagger UI URL:** `http://localhost:8080/swagger-ui.html`
*   **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

## 🤝 Contributing

When contributing to this repository, please ensure that you:
*   Follow the existing code style and architecture (Modular structure based on roles).
*   Add appropriate documentation for new endpoints.
*   Update the `.env` requirements if new integrations are added.
