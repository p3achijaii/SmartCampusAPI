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
- Apache Tomcat (configured in NetBeans)
- IntelliJ IDEA or NetBeans IDE (Java EE 7 support)

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
4. Open as a Maven Web Application project (if needed)

## Step 2: Verify project settings
1. Right-click the project and choose **Properties**.
2. Check that the project is using **Java 11**.

## Step 3: Build the project
1. Right-click the project.
2. Select **Clean and Build**.

Netbeans will:
- Compile the project
- Resolve dependencies
- Generate WAR file

## Step 4: Run or deploy the application
1. Right-click project
2. Click Run

NetBeans will:
- Automatically deploy to Tomcat
- Start the server if not running
- Handle WAR deployement automatically

*(No manual WAR deployment or Tomcat setup required)*

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

---

# Report Questions

## Part 1: Service Architecture & Setup

### 1. Project & Application Configuration
In SmartCampusAPI, a JAX-RS resource class should not be considered as a singleton that contains mutable application state. The runtime could create resource instances in a way that does not ensure a single permanent object for the whole application, therefore the safer assumption is that resource classes are request handlers rather than state containers. The important shared state in this project is stored in the in-memory 'DataStore', not inside the resource object itself. Rooms, sensors, and sensor readings are stored in shared collections, allowing multiple requests to access or modify them simultaneously. To prevent race conditions, lost updates, or inconsistent relationships between entities, the application must protect those structures using synchronisation or thread-safe collections. If this is not handled correctly, one request may override another's changes, leaving the data in a partially updated state.

### 2. The "Discovery" Endpoint
Hypermedia is regarded as a defining feature of advanced REST design because the server not only returns data but includes links that define what the client should do next. This makes the API self-descriptive, allowing the client to browse the application without having to hard-code every possible route. In a project like SmartCampusAPI, this is useful since it allows client developers to find relevant actions straight from the response rather than relying only on documentation. Static documentation may become outdated if endpoints change, but hypermedia remains consistent with current server behaviour because it is created as part of the response. This is why HATEOAS is widely regarded as one of the more mature REST concepts. It also reduces uncertainty for client developers by communicating the available next steps via the API.

## Part 2: Room Management

### 3. Room Resource Implementation
Returning only room IDs decreases the response size and bandwidth usage, which is beneficial when the client only requires references. However, it forces the client to perform extra queries if it needs actual room details, increasing latency and client-side complexity. Returning full room objects is more practical because the client obtains all relevant information in one response. This is generally preferable for user interfaces that must provide room information immediately. The downside is that the payload increases therefore, the best approach is determined by whether efficiency or convenience of use is more important. Full objects are normally preferred in a SmartCampusAPI-style project when the client is creating a dashboard or detail page.

### 4. Room Deletion & Safety Logic
The DELETE action in this implementation is ineffective since repeating the same request results in the same final state. If a room or sensor has already been deleted, submitting the same DELETE request does not modify the system. The second request just detects that the resource is already absent, keeping the application in the same state. This is the intended REST behaviour for DELETE, since multiple identical delete requests should not result in unwanted side effects. In practice, this makes the API safer when clients retry requests accidentally. It also indicates that the server may handle several network requests without affecting the resource state.

## Part 3: Sensor Operations & Linking

### 5. Sensor Resource & Integrity
Because the POST method specifically consumes JSON, JAX-RS expects the request body to be formatted as 'application/json'. If a client submits an unsupported format such as 'text/plain' or 'application/xml', the runtime will be unable to properly match the payload to the method. In such cases, JAX-RS normally rejects the request with a '415 Unsupported Media Type' response. This is beneficial because it maintains the API contract and keeps improper data types from being processed wrongly. It also makes endpoint behaviour more predictable for clients. Because SmartCampusAPI is built around JSON communication, this annotation ensures that only compatible payloads can reach the resource method.

### 6. Filtered Retrieval & Search
Using '@QueryParam' for filtering is preferable since filtering is typically an optional refinement of a collection, not a part of the resource's identity. A URL like '/api/v1/sensors?type=CO2' clearly indicates that the customer is requesting sensor data with a filter applied. If the type was included in the path, the API would become less flexible and more difficult to develop with additional filters later. In addition, query parameters make it easier to combine several search conditions. As a result, the query parameter method is often less complicated and more REST-friendly for search and filtering. It maintains the main collection URL stable while still allowing for helpful filtering behaviour.

## Part 4: Deep Nesting with Sub - Resources

### 7. The Sub-Resource Locator Pattern
The Sub-Resource Locator approach is useful for organising big APIs by delegating nested behaviour to different classes. This allows SmartCampusAPI to keep sensor logic, reading logic, and room logic distinct rather than combining everything into a single huge controller. This separation improves readability, simplifies testing, and reduces the likelihood of one class becoming overly complex. It also makes the structure more scalable because nested features can expand without turning the main resource class unmanageable. Compared to a single large controller, the sub-resource method is significantly easier to maintain as the API grows. It also makes the codebase more modular, which is a major advantage as the project grows.

## Part 5: Advanced Error Handling, Exception Mapping & Logging

### 8. Dependency Valodation
HTTP 422 is more semantically correct when the JSON payload is acceptable, but one of its references refers to a resource that does not exist. In that case, the endpoint itself is valid, and the request body is syntactically correct, therefore the issue is not that the URL cannot be located. The problem is that the request cannot be processed since the information is logically flawed. That is precisely what 422 means: the server recognised the request, but the content refused business validation. This makes the issue more obvious than a generic 404. It also helps the client understand that the solution is in the payload, not the endpoint address.

### 9. The Global Safety Net (500)
Exposing internal Java stack traces to external API users is risky since it reveals information that should be kept secret. An attacker can discover class names, method names, package structure, file names, line numbers, and even library or framework versions. That information allows them to better understand the application's internal design and direct future attacks. It may also uncover implementation flaws or confirm the technologies being used. For safety issues, the client should receive a restricted error message, but the detailed trace should remain in server logs only. This limits the amount of information available to an attacker while still allowing developers to debug the issue internally.

### 10. API Request & Response Logging Filters
JAX-RS filters are better suited for logging because it is a cross-cutting problem that should be handled centrally rather than being repeated in each resource method. A filter can automatically capture all incoming requests and outgoing responses, allowing resource classes to focus on business logic. This cleans up the code and keeps manual logging statements out of each endpoint. It also ensures that logging is consistent across the API, as each request takes the same logging path. If logging needs to be changed later, it can be done in one place rather than throughout the application. That is significantly easier to manage and reduces the possibility of forgetting to log some endpoints.






