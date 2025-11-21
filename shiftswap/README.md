# Shiftswap

**Shiftswap** is a platform to simplify healthcare staff scheduling. It allows teams to manage shifts, request swaps, and get manager approvals while ensuring compliance with regulations and internal policies.

---

## Features

### Staff Management
- Register and manage staff profiles
- Staff directory for quick contact lookup
- View detailed info: roles, certifications

### Shift Management
- Automated shift generation
- Visual schedule views

### Shift Swapping
- Request swaps with eligible colleagues
- Automatic validation according to business rules
- Track swap status: Requested → Approved/Denied

### Manager Approval
- Approve or deny swap requests
- Dashboard to monitor schedules and pending actions

### Audit Logging
- Full history of critical actions like swaps and approvals

---

## Business Rules

- **Certification Check:** Staff must have required certifications
- **Role Compatibility:** Swaps only between staff with the same role
- **Facility Consistency:** Swaps limited to the same facility
- **One Shift Per Day:** No multiple shifts per day
- **Max Weekly Hours:** Prevents exceeding working hour limits
- **Rest Periods:** Mandatory rest between shifts
- **No Schedule Conflicts:** Overlapping shifts prevented

---

## Tech Stack

- **Language:** Java 17
- **Framework:** Spring Boot 3.5.x
- **Build Tool:** Maven
- **Database:** PostgreSQL (production), H2 (test)
- **Frontend:** Thymeleaf (HTML/CSS)
- **Security:** Spring Security
- **Utilities:** Lombok, Spring Boot Actuator

---

## Prerequisites

- Java 17+
- Maven (or use `mvnw`)
- PostgreSQL (for production)

---

## Installation & Setup

1. Clone the repository:
```bash
git clone <repository-url>
cd shiftswap
```

2. Configure database in `src/main/resources/application.yml` (H2 used by default).

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

5. Open your browser: `http://localhost:8080`

## Project Structure

```bash
src/main/java/com/swabhiman/shiftswap
├─ config        # Security and app settings
├─ controller    # Web controllers
├─ domain        # Entities & repositories
├─ dto           # Data transfer objects
├─ rules         # Business logic for swaps
├─ service       # Core logic
├─ statemachine  # Swap request states
└─ events        # Event listeners/publishers
```

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature-name`)
3. Commit your changes (`git commit -m "Description"`)
4. Push to your branch (`git push origin feature-name`)
5. Open a pull request
