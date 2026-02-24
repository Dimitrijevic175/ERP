# ERP Microservices System

This project is a comprehensive ERP (Enterprise Resource Planning) solution built using a microservices architecture. It consists of **5 independent services** developed with **Spring Boot 4.0.0 (Java 17)** and a centralized **PostgreSQL** database environment managed via **Docker**.

---

## Technologies & Tools

* **Language:** Java 17
* **Framework:** Spring Boot 4.0.0
* **Build Tool:** Maven
* **Containerization:** Docker & Docker Compose
* **Database:** PostgreSQL 16+

---

## System Architecture

The system is divided into five core functional areas, each running as a separate microservice.

| Service Name        | Port | Database Name          |
| ------------------- | ---- | ---------------------- |
| User Service        | 8080 | user_service_db        |
| Product Service     | 8081 | product_service_db     |
| Procurement Service | 8082 | procurement_service_db |
| Sales Service       | 8083 | sales_service_db       |
| Warehouse Service   | 8084 | warehouse_service_db   |

Each service follows the **"Database per Service"** pattern, ensuring loose coupling and independent scalability.

---

## Getting Started (Local Setup)

### Prerequisites

Before running the application, make sure you have the following installed:

1. **Git** – [https://git-scm.com/](https://git-scm.com/)
2. **Docker Desktop** – [https://www.docker.com/products/docker-desktop/](https://www.docker.com/products/docker-desktop/)

---

## Installation & Execution

### 1. Clone the Repository

```bash
git clone https://github.com/Dimitrijevic175/ERP.git
cd ERP
```

---

### 2. Set up the environment

Set up necessary data in the application.properties file inside the resources folder of each service. You can use an example file application-example.properties as a template. It should look like this:

```bash
server.port=8080

spring.datasource.url=jdbc:postgresql://localhost:5432/user_service_db
spring.datasource.username=postgres
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

oauth.jwt.secret=YOUR_SECRET_KEY_GOES_HERE
```
To set up authorization, run the JwtKeyGenerator class inside the user service to generate a secret key, then copy the output from the console into your application.properties file.  

**REPEAT THIS STEP FOR EACH SERVICE.**


### 3. Launch the System

The entire infrastructure is automated using Docker Compose.

Run the following command from the project root directory:

```bash
docker-compose up --build
```

This command will:

* Build all Spring Boot services
* Pull required Docker images
* Initialize PostgreSQL databases
* Connect all services via Docker network

---

### 4. Verify Running Containers

After startup logs stabilize, verify that all containers are running:

```bash
docker ps
```

You should see containers for all microservices and the PostgreSQL database.

---

## Database Configuration

The system uses a single PostgreSQL container that automatically creates **5 separate databases**.

### Default Credentials

| Property | Value                                  |
| -------- | -------------------------------------- |
| Host     | localhost (host) / db (Docker network) |
| Port     | 5432                                   |
| Username | postgres                               |
| Password | admin                                  |


---

## 🌐 Service Access (Localhost)

After successful startup, services are available at:

* User Service: [8080]
* Product Service: [8081]
* Procurement Service: [8082]
* Sales Service: [8083]
* Warehouse Service: [8084]

---

## License

This project is licensed under the **MIT License**.

You are free to use, modify, and distribute this software in accordance with the terms of the MIT License.

See the **LICENSE** file for more details.
