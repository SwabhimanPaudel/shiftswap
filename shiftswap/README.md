# Shiftswap

**Shiftswap** is a comprehensive healthcare shift coordination platform designed to streamline the process of managing staff schedules, shift swaps, and manager approvals. It ensures compliance with healthcare regulations and internal policies through a robust business rule engine.

## 🚀 Features

*   **Staff Management**:
    *   Staff registration and profile management.
    *   Staff directory for easy contact lookup.
    *   Detailed staff views including certifications and roles.
*   **Shift Management**:
    *   Automated shift generation.
    *   Visual schedule views.
*   **Shift Swapping**:
    *   Staff can request to swap shifts with eligible colleagues.
    *   **Intelligent Validation**: The system automatically validates swaps against a set of business rules (see below).
    *   **State Machine**: Tracks swap requests through their lifecycle (Requested -> Approved/Denied).
*   **Manager Approval**:
    *   Managers can review and approve/deny swap requests.
    *   Dashboard for overseeing staff schedules and pending actions.
*   **Audit Logging**:
    *   Comprehensive audit trails for all critical actions (swaps, approvals, etc.).

## 🛡️ Business Rules Engine

Shiftswap enforces strict rules to ensure compliance and fairness:

*   **Certification Check**: Staff must have the required certifications for the shift.
*   **Role Compatibility**: Swaps are only allowed between staff with the same role.
*   **Facility Consistency**: Swaps must occur within the same facility.
*   **One Shift Per Day**: Prevents staff from being scheduled for multiple shifts on the same day.
*   **Max Weekly Hours**: Ensures staff do not exceed maximum working hours to prevent burnout.
*   **Rest Period**: Enforces mandatory rest periods between shifts.
*   **Schedule Conflict**: Prevents overlapping shifts.

## 🛠️ Tech Stack

*   **Language**: Java 17
*   **Framework**: Spring Boot 3.5.x
*   **Build Tool**: Maven
*   **Database**:
    *   **Production**: PostgreSQL
    *   **Test**: H2 In-Memory Database
*   **Frontend**: Thymeleaf (Server-side templating) with HTML/CSS
*   **Security**: Spring Security (Authentication & Authorization)
*   **Utilities**: Lombok, Spring Boot Actuator

## 📋 Prerequisites

*   **Java 17** or higher installed.
*   **Maven** installed (or use the included `mvnw` wrapper).
*   **PostgreSQL** installed and running (for production profile).

## ⚙️ Installation & Setup

1.  **Clone the repository**:
    ```bash
    git clone <repository-url>
    cd shiftswap
    ```

2.  **Configure Database**:
    Update `src/main/resources/application.yml` with your PostgreSQL credentials if running in production mode. By default, it may use H2 for development.

3.  **Build the project**:
    ```bash
    ./mvnw clean install
    ```

4.  **Run the application**:
    ```bash
    ./mvnw spring-boot:run
    ```

5.  **Access the application**:
    Open your browser and navigate to `http://localhost:8080`.

## 📂 Project Structure

*   `src/main/java/com/swabhiman/shiftswap`:
    *   `config`: Security and application configuration.
    *   `controller`: Web controllers handling HTTP requests.
    *   `domain`: JPA Entities and Repositories.
    *   `dto`: Data Transfer Objects.
    *   `rules`: Business logic rules for swap validation.
    *   `service`: Service layer containing business logic.
    *   `statemachine`: Logic for handling swap request states.
    *   `events`: Event listeners and publishers.

## 🤝 Contributing

1.  Fork the repository.
2.  Create a feature branch (`git checkout -b feature/amazing-feature`).
3.  Commit your changes (`git commit -m 'Add some amazing feature'`).
4.  Push to the branch (`git push origin feature/amazing-feature`).
5.  Open a Pull Request.

## 📄 License

[License Information]
