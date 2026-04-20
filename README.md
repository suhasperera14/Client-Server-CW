# Ã°Å¸ÂÂ« Smart Campus Sensor & Room Management API

A RESTful API built with **JAX-RS (Jersey)** for managing campus rooms and IoT sensors.  
Module: **5COSC022W Ã¢â‚¬â€œ Client-Server Architectures** | University of Westminster

---

## Ã°Å¸â€œâ€¹ Table of Contents

1. [API Design Overview](#api-design-overview)
2. [Technology Stack](#technology-stack)
3. [Project Structure](#project-structure)
4. [Build & Run Instructions](#build--run-instructions)
5. [API Endpoints Reference](#api-endpoints-reference)
6. [Sample curl Commands](#sample-curl-commands)
7. [Conceptual Report Ã¢â‚¬â€œ Question Answers](#conceptual-report--question-answers)

---

## API Design Overview

The Smart Campus API follows **REST architectural principles** to expose three core resources:

| Resource | Base Path | Description |
|---|---|---|
| Discovery | `GET /api/v1` | API metadata and HATEOAS links |
| Rooms | `/api/v1/rooms` | Campus room management |
| Sensors | `/api/v1/sensors` | IoT sensor registration and management |
| Readings | `/api/v1/sensors/{id}/readings` | Historical sensor reading logs |

### Design Decisions
- **In-memory storage** using `ConcurrentHashMap` (thread-safe, no database required)
- **Per-request JAX-RS lifecycle** with a singleton `DataStore` for safe shared state
- **Nested sub-resources** via the Sub-Resource Locator pattern for sensor readings
- **Exception Mappers** for every error scenario Ã¢â‚¬â€ no raw stack traces ever returned
- **Cross-cutting logging** via JAX-RS filters applied globally to all endpoints

---

## Technology Stack

| Component | Technology |
|---|---|
| Language | Java 11 |
| Framework | JAX-RS 2.1 (Jersey 2.41) |
| JSON | Jackson Databind 2.15 |
| Build Tool | Maven 3.x |
| IDE | NetBeans (Web Application project) |
| Server | Apache Tomcat (bundled with NetBeans) |
| Storage | In-memory `ConcurrentHashMap` / `ArrayList` |

> Ã¢Å¡Â Ã¯Â¸Â **No Spring Boot. No SQL database. Pure JAX-RS only.**

---

## Project Structure

```
SmartCampusAPI/
Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ pom.xml
Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ src/
    Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ main/
        Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ java/com/smartcampus/
        Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SmartCampusApplication.java     Ã¢â€ Â @ApplicationPath("/api/v1")
        Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ DataStore.java                  Ã¢â€ Â Singleton in-memory data store
        Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ model/
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Room.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ Sensor.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ SensorReading.java
        Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ resource/
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ DiscoveryResource.java       Ã¢â€ Â GET /api/v1
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ RoomResource.java            Ã¢â€ Â /api/v1/rooms
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SensorResource.java          Ã¢â€ Â /api/v1/sensors
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ SensorReadingResource.java   Ã¢â€ Â /api/v1/sensors/{id}/readings
        Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ exception/
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ RoomNotEmptyException.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ RoomNotEmptyExceptionMapper.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ LinkedResourceNotFoundException.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ LinkedResourceNotFoundExceptionMapper.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SensorUnavailableException.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€Å“Ã¢â€â‚¬Ã¢â€â‚¬ SensorUnavailableExceptionMapper.java
        Ã¢â€â€š   Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ GlobalExceptionMapper.java
        Ã¢â€â€š   Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ filter/
        Ã¢â€â€š       Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ LoggingFilter.java
        Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ webapp/
            Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ WEB-INF/
                Ã¢â€â€Ã¢â€â‚¬Ã¢â€â‚¬ web.xml
```

---

## Build & Run Instructions

### Prerequisites
- **Java JDK 11** or higher installed
- **Apache Maven 3.6+** installed
- **NetBeans IDE** (with Tomcat/GlassFish bundled)

### Option A: Run in NetBeans (Recommended)

1. **Open NetBeans** and go to `File Ã¢â€ â€™ Open Project`
2. Navigate to the `SmartCampusAPI` folder and open it
3. Right-click the project Ã¢â€ â€™ **Clean and Build**
4. Right-click the project Ã¢â€ â€™ **Run**
5. NetBeans will deploy the WAR to the bundled Tomcat server
6. The API will be available at: `http://localhost:8080/SmartCampusAPI/api/v1`

### Option B: Build with Maven CLI

```bash
# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/SmartCampusAPI.git
cd SmartCampusAPI

# 2. Build the WAR file
mvn clean package

# 3. Deploy to Tomcat
# Copy target/SmartCampusAPI.war to your Tomcat webapps/ directory
cp target/SmartCampusAPI.war /path/to/tomcat/webapps/

# 4. Start Tomcat
/path/to/tomcat/bin/startup.sh   # Linux/Mac
/path/to/tomcat/bin/startup.bat  # Windows

# 5. Access the API
curl http://localhost:8080/SmartCampusAPI/api/v1
```

### Verify the server is running

```bash
curl -s http://localhost:8080/SmartCampusAPI/api/v1 | python -m json.tool
```

You should see the discovery response with version info and resource links.
