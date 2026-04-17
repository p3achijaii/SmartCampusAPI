package resource;

import data.DataStore;
import exception.LinkedResourceNotFoundException;
import exception.ResourceNotFoundException;
import model.Room;
import model.Sensor;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {

    @GET
    public List<Sensor> getAllSensors(@QueryParam("type") String type) {
        List<Sensor> allSensors = new ArrayList<>(DataStore.getSensors().values());
        if (type != null && !type.isBlank()) {
            return allSensors.stream()
                    .filter(s -> s.getType() != null && type.equalsIgnoreCase(s.getType()))
                    .collect(Collectors.toList());
        }
        return allSensors;
    }

    @GET
    @Path("/{sensorId}")
    public Sensor getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensor(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with ID '" + sensorId + "' was not found.");
        }
        return sensor;
    }

    @POST
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Sensor ID is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (sensor.getRoomId() == null || sensor.getRoomId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"roomId is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (DataStore.sensorExists(sensor.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A sensor with ID '" + sensor.getId() + "' already exists.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        Room room = DataStore.getRoom(sensor.getRoomId());
        if (room == null) {
            throw new LinkedResourceNotFoundException(
                    "Cannot create sensor: the referenced roomId '" + sensor.getRoomId() + "' does not exist."
            );
        }

        if (sensor.getStatus() == null || sensor.getStatus().isBlank()) {
            sensor.setStatus("ACTIVE");
        }

        DataStore.addSensor(sensor);
        room.addSensorId(sensor.getId());

        return Response.status(Response.Status.CREATED)
                .entity(sensor)
                .build();
    }

    @PUT
    @Path("/{sensorId}")
    public Response updateSensor(@PathParam("sensorId") String sensorId, Sensor updatedSensor) {
        Sensor existing = DataStore.getSensor(sensorId);
        if (existing == null) {
            throw new ResourceNotFoundException("Sensor with ID '" + sensorId + "' was not found.");
        }

        if (updatedSensor == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Updated sensor payload is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (updatedSensor.getType() != null) {
            existing.setType(updatedSensor.getType());
        }

        if (updatedSensor.getStatus() != null) {
            existing.setStatus(updatedSensor.getStatus());
        }

        if (updatedSensor.getRoomId() != null && !updatedSensor.getRoomId().isBlank()
                && !updatedSensor.getRoomId().equals(existing.getRoomId())) {
            Room newRoom = DataStore.getRoom(updatedSensor.getRoomId());
            if (newRoom == null) {
                throw new LinkedResourceNotFoundException(
                        "Cannot move sensor: the referenced roomId '" + updatedSensor.getRoomId() + "' does not exist."
                );
            }

            Room oldRoom = DataStore.getRoom(existing.getRoomId());
            if (oldRoom != null) {
                oldRoom.removeSensorId(sensorId);
            }

            newRoom.addSensorId(sensorId);
            existing.setRoomId(updatedSensor.getRoomId());
        }

        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensor(sensorId);
        if (sensor == null) {
            return Response.noContent().build();
        }

        Room room = DataStore.getRoom(sensor.getRoomId());
        if (room != null) {
            room.removeSensorId(sensorId);
        }

        DataStore.removeSensor(sensorId);
        return Response.noContent().build();
    }

    @Path("/{sensorId}/readings")
    public SensorReadingResource getSensorReadings(@PathParam("sensorId") String sensorId) {
        Sensor sensor = DataStore.getSensor(sensorId);
        if (sensor == null) {
            throw new ResourceNotFoundException("Sensor with ID '" + sensorId + "' was not found.");
        }
        return new SensorReadingResource(sensorId);
    }
}