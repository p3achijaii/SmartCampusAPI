package resource;

import data.DataStore;
import exception.SensorUnavailableException;
import model.Sensor;
import model.SensorReading;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    @GET
    public List<SensorReading> getReadings() {
        return DataStore.getReadings(sensorId);
    }

    @POST
    public Response addReading(SensorReading reading) {
        Sensor sensor = DataStore.getSensor(sensorId);

        if (sensor == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Sensor not found.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (reading == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Reading payload is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                    "Sensor '" + sensorId + "' is currently in MAINTENANCE mode and cannot accept new readings."
            );
        }

        SensorReading newReading = new SensorReading(reading.getValue());
        DataStore.addReading(sensorId, newReading);

        sensor.setCurrentValue(reading.getValue());

        return Response.status(Response.Status.CREATED)
                .entity(newReading)
                .build();
    }
}