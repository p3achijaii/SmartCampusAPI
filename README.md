# SmartCampusAPI

SmartCampusAPI is a RESTful Java 11 API for managing campus rooms, sensors, and sensor readings. The project uses Maven, Jersey, and JSON over HTTP.

## API Overview

This API is organised around three main resource groups:

### 1. Rooms
- Create a room
- Get all rooms
- Get a single room by ID
- Update a room
- Delete a room

### 2. Sensors
- Create a sensor and assign it to a room
- Get all sensors
- Filter sensors by type
- Get a single sensor by ID
- Update a sensor
- Delete a sensor

### 3. Sensor Readings
- Get all readings for a sensor
- Add a new reading for a sensor

### Base Path
All endpoints are exposed under:

```text
/api/v1
```

### Example Resource Paths
- `GET /api/v1`
- `GET /api/v1/rooms`
- `GET /api/v1/sensors`
- `GET /api/v1/sensors/{sensorId}/readings`

## Prerequisites
- Java 11
- Maven
- A servlet container or application server compatible with the project configuration
- IntelliJ IDEA or NetBeans

---

## Build and Run Instructions

# Option 1: IntelliJ IDEA

## Step 1: Open the project
1. Launch **IntelliJ IDEA**.
2. Click **Open**.
3. Select the project root folder: `SmartCampusAPI`.
4. Wait for IntelliJ to import the Maven project.

## Step 2: Verify project settings
1. Go to **File > Project Structure**.
2. Make sure the **Project SDK** is set to **Java 11**.
3. Confirm Maven imported successfully and dependencies are resolved.

## Step 3: Build the project
1. Open the Maven tool window.
2. Run:
   - **Lifecycle > clean**
   - **Lifecycle > package**
3. Wait for the build to finish successfully.

## Step 4: Deploy or run the server
1. Configure your application server in IntelliJ if needed.
2. Deploy the generated WAR file from:
   ```text
   target/SmartCampusAPI.war
   ```
3. Start the server.
4. Confirm the application is available at the expected host and port.

## Step 5: Test the API
Use a browser, Postman, or the sample `curl` commands below.

---

# Option 2: NetBeans

## Step 1: Open the project
1. Launch **NetBeans**.
2. Select **File > Open Project**.
3. Choose the `SmartCampusAPI` project folder.
4. Allow NetBeans to recognize it as a Maven project.

## Step 2: Verify Java version
1. Right-click the project and choose **Properties**.
2. Check that the project is using **Java 11**.
3. Confirm Maven dependencies are resolved.

## Step 3: Build the project
1. Right-click the project.
2. Select **Clean and Build**.
3. Wait until NetBeans finishes building the WAR file.

## Step 4: Run or deploy the application
1. Deploy the generated WAR file from:
   ```text
   target/SmartCampusAPI.war
   ```
2. Start your application server from NetBeans or deploy manually.
3. Verify the API is running.

## Step 5: Test the API
Use the sample `curl` commands below to confirm the endpoints work correctly.

---

## Sample `curl` Commands

> Replace `localhost:8080` with your actual host and port if needed.

### 1. Get API discovery info
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1
```

### 2. Get all rooms
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

### 3. Create a new room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "R101",
    "name": "Main Lecture Hall",
    "capacity": 60,
    "sensorIds": []
  }'
```

### 4. Get all sensors
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors
```

### 5. Create a new sensor assigned to a room
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "S100",
    "type": "TEMPERATURE",
    "status": "ACTIVE",
    "roomId": "R101"
  }'
```

### 6. Add a sensor reading
```bash
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/S100/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 24.5
  }'
```

### 7. Get sensor readings
```bash
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/sensors/S100/readings
```




