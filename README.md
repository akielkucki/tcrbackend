# TCRBackend

A lightweight Java backend built with [Javalin](https://javalin.io/) to manage projects, serve static assets, and handle file uploads. Supports templating with JTE, user authentication (JWT), and serves JSON APIs for frontend consumption.

---

## 🚀 Features

- 🔐 JWT-based authentication
- 📂 Image upload support with persistent storage
- 🖼 JTE template rendering (`.jte`)
- 📡 RESTful API endpoints for projects
- 💾 SQLite database powered by ORMLite
- 🪶 Lightweight and deployable as a single JAR

---

## 🛠 Tech Stack

- **Java 17**
- **Javalin 6**
- **JTE** (Java Template Engine)
- **SQLite** (via ORMLite)
- **Jackson** (for JSON parsing)
- **SLF4J** (for logging)
- **JWT** (via Auth0)

---

### ✅ Prerequisites

- Java 17+
- Maven 3.8+
- (Optional) Fly.io, Docker, or a Linux VPS for deployment

### 🔧 Build & Run

```bash
# Build the JAR
mvn clean package

# Run the JAR
java -jar target/TCRBackend-1.0.jar

