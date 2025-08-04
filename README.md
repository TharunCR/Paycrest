# 💰 Paycrest - Secure Banking Application

Paycrest is a robust and secure online banking application built with **Spring Boot**, following clean architecture principles and adhering to the **SOLID design principles**. It allows users to register, log in securely, view account balances, and view transaction histories.

---

## ✨ Features

- 🔐 User Authentication with Spring Security
- 🏦 Account management
- 💳 Transaction history view
- ⚖️ Proper transaction handling with `@Transactional`
- ❗ Graceful exception handling
- ✅ Clean code following **SOLID principles**
- 🗃️ Data persistence using Spring Data JPA
- 📄 MVC Architecture with Thymeleaf (if using frontend views)

---

## ⚙️ Technologies Used

| Layer | Tech |
|------|------|
| Backend | Spring Boot |
| Security | Spring Security |
| ORM | Spring Data JPA |
| Database | H2 / MySQL / PostgreSQL |
| View (Optional) | Thymeleaf |
| Build Tool | Maven / Gradle |
| Version Control | Git & GitHub |

---

## 🧱 Architecture

The project follows a layered architecture:


- **Controller** handles HTTP requests and responses.
- **Service** layer contains business logic and uses `@Transactional` to maintain consistency.
- **Repository** interacts with the database using Spring Data JPA.

---

## 🧪 SOLID Principles in Practice

- **S**ingle Responsibility: Each class has one responsibility (e.g., `TransactionService`, `UserController`)
- **O**pen/Closed: Classes are open for extension but closed for modification.
- **L**iskov Substitution: Subclasses correctly implement base class behaviors.
- **I**nterface Segregation: Interfaces are specific and focused.
- **D**ependency Inversion: Services are injected via interfaces using `@Autowired`.

---

🛡️ Security
Login secured using Spring Security

Passwords are stored using BCrypt hashing

Role-based access (ADMIN, USER)
