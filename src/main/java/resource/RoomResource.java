package resource;

import data.DataStore;
import exception.ResourceNotFoundException;
import exception.RoomNotEmptyException;
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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Path("/v1/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class RoomResource {
    @GET
    public List<Room> getAllRooms() {
        return new ArrayList<>(DataStore.getRooms().values());
    }

    @GET
    @Path("/{roomId}")
    public Room getRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoom(roomId);
        if (room == null) {
            throw new ResourceNotFoundException("Room with ID '" + roomId + "' was not found.");
        }
        return room;
    }

    @POST
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Room ID is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (DataStore.roomExists(room.getId())) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("{\"error\":\"A room with ID '" + room.getId() + "' already exists.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        if (room.getSensorIds() == null) {
            room.setSensorIds(new ArrayList<>());
        }

        DataStore.addRoom(room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    @PUT
    @Path("/{roomId}")
    public Response updateRoom(@PathParam("roomId") String roomId, Room updatedRoom) {
        Room existing = DataStore.getRoom(roomId);
        if (existing == null) {
            throw new ResourceNotFoundException("Room with ID '" + roomId + "' was not found.");
        }

        if (updatedRoom == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"error\":\"Updated room payload is required.\"}")
                    .type(MediaType.APPLICATION_JSON)
                    .build();
        }

        existing.setName(updatedRoom.getName());
        existing.setCapacity(updatedRoom.getCapacity());

        return Response.ok(existing).build();
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = DataStore.getRoom(roomId);
        if (room == null) {
            return Response.noContent().build();
        }

        List<String> sensorIds = room.getSensorIds();
        if (sensorIds != null && !sensorIds.isEmpty()) {
            for (String sensorId : sensorIds) {
                Sensor sensor = DataStore.getSensor(sensorId);
                if (sensor != null) {
                    throw new RoomNotEmptyException(
                            "Cannot delete room '" + roomId + "' because it still has sensor(s) assigned."
                    );
                }
            }
        }
        DataStore.removeRoom(roomId);
        return Response.noContent().build();
    }
}

