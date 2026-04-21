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

---

## API Endpoints Reference

### Discovery
| Method | Path | Description |
|---|---|---|
| GET | `/api/v1` | API metadata and HATEOAS navigation links |

### Rooms
| Method | Path | Description | Success Code |
|---|---|---|---|
| GET | `/api/v1/rooms` | List all rooms | 200 |
| POST | `/api/v1/rooms` | Create a new room | 201 |
| GET | `/api/v1/rooms/{roomId}` | Get room by ID | 200 |
| DELETE | `/api/v1/rooms/{roomId}` | Delete room (blocked if sensors exist) | 200 |

### Sensors
| Method | Path | Description | Success Code |
|---|---|---|---|
| GET | `/api/v1/sensors` | List all sensors | 200 |
| GET | `/api/v1/sensors?type=CO2` | Filter sensors by type | 200 |
| POST | `/api/v1/sensors` | Register a new sensor | 201 |
| GET | `/api/v1/sensors/{sensorId}` | Get sensor by ID | 200 |
| DELETE | `/api/v1/sensors/{sensorId}` | Remove a sensor | 200 |

### Sensor Readings (Sub-Resource)
| Method | Path | Description | Success Code |
|---|---|---|---|
| GET | `/api/v1/sensors/{sensorId}/readings` | Get all readings for a sensor | 200 |
| POST | `/api/v1/sensors/{sensorId}/readings` | Append a new reading | 201 |
| GET | `/api/v1/sensors/{sensorId}/readings/{readingId}` | Get a specific reading | 200 |

### Error Responses

| Scenario | Status Code |
|---|---|
| Room deleted with sensors assigned | 409 Conflict |
| Sensor created with non-existent roomId | 422 Unprocessable Entity |
| Reading posted to MAINTENANCE sensor | 403 Forbidden |
| Resource not found | 404 Not Found |
| Duplicate resource ID | 409 Conflict |
| Any unexpected server error | 500 Internal Server Error |

---

## Sample curl Commands

> Base URL: `http://localhost:8080/SmartCampusAPI/api/v1`  
> Pre-loaded sample data includes rooms: `LIB-301`, `LAB-101`, `HALL-A` and sensors: `TEMP-001`, `CO2-001`, `OCC-001`, `TEMP-002`

---

### 1. Discover the API (GET /api/v1)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1 \
  -H "Accept: application/json"
```
**Expected:** 200 OK with API metadata and resource links.

---

### 2. List all Rooms (GET /api/v1/rooms)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Accept: application/json"
```
**Expected:** 200 OK with array of all room objects including sensor IDs.

---

### 3. Create a new Room (POST /api/v1/rooms)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "SCI-205",
    "name": "Science Lab 205",
    "capacity": 40
  }'
```
**Expected:** 201 Created with the new room object.

---

### 4. Get a specific Room (GET /api/v1/rooms/{roomId})
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```
**Expected:** 200 OK with room details including assigned sensor IDs.

---

### 5. Attempt to Delete a Room with Sensors (409 Conflict)
```bash
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301 \
  -H "Accept: application/json"
```
**Expected:** 409 Conflict Ã¢â‚¬â€ room has sensors assigned, deletion blocked.

---

### 6. Delete an empty Room (DELETE /api/v1/rooms/{roomId})
```bash
# First create a room with no sensors
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{"id": "EMPTY-01", "name": "Empty Test Room", "capacity": 10}'

# Then delete it successfully
curl -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/EMPTY-01 \
  -H "Accept: application/json"
```
**Expected:** 200 OK Ã¢â‚¬â€ room deleted successfully.

---

### 7. List all Sensors (GET /api/v1/sensors)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Accept: application/json"
```
**Expected:** 200 OK with all sensors.

---

### 8. Filter Sensors by type (GET /api/v1/sensors?type=Temperature)
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=Temperature" \
  -H "Accept: application/json"
```
**Expected:** 200 OK with only Temperature-type sensors.

---

### 9. Filter Sensors by CO2 type
```bash
curl -X GET "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2" \
  -H "Accept: application/json"
```
**Expected:** 200 OK with only CO2 sensors.

---

### 10. Register a new Sensor (POST /api/v1/sensors)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "id": "LIGHT-001",
    "type": "Light",
    "status": "ACTIVE",
    "currentValue": 350.0,
    "roomId": "LIB-301"
  }'
```
**Expected:** 201 Created with new sensor details.

---

### 11. Register Sensor with invalid roomId (422 Unprocessable Entity)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "BAD-001",
    "type": "Temperature",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "NONEXISTENT-ROOM"
  }'
```
**Expected:** 422 Unprocessable Entity Ã¢â‚¬â€ roomId does not exist.

---

### 12. Get readings history for a sensor (GET /api/v1/sensors/{sensorId}/readings)
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Accept: application/json"
```
**Expected:** 200 OK with reading history array.

---

### 13. Post a new reading to an ACTIVE sensor (POST /api/v1/sensors/{sensorId}/readings)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{
    "value": 24.7
  }'
```
**Expected:** 201 Created. Note that `currentValue` on the parent sensor is also updated.

---

### 14. Post a reading to a MAINTENANCE sensor (403 Forbidden)
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/OCC-001/readings \
  -H "Content-Type: application/json" \
  -d '{"value": 15.0}'
```
**Expected:** 403 Forbidden Ã¢â‚¬â€ sensor OCC-001 is under MAINTENANCE.

---

### 15. Trigger a 404 Not Found error
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms/FAKE-ROOM \
  -H "Accept: application/json"
```
**Expected:** 404 Not Found with descriptive JSON error body.
