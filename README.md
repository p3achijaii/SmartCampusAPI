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
