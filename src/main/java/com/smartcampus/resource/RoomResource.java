package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.RoomNotEmptyException;
import com.smartcampus.model.Room;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Resource class managing /api/v1/rooms endpoints.
 *
 * List vs Full Object Trade-off:
 * Returning only IDs in a list reduces payload size and is faster for large collections,
 * but forces clients to make N additional requests to fetch details â€” the "N+1 problem".
 * Returning full objects increases bandwidth per response but eliminates follow-up calls.
 * Best practice: return full objects with pagination for moderate collections, or use
 * field projection (e.g., ?fields=id,name) to let clients decide what they need.
 *
 * DELETE Idempotency:
 * A DELETE is idempotent when repeated requests produce the same server state.
 * In this implementation, the first DELETE on a room removes it and returns 204 No Content.
 * A second DELETE on the same (now non-existent) room returns 404 Not Found.
 * The server state is identical after both calls (room is gone), so the operation IS
 * idempotent by definition â€” the outcome is the same even if the status code differs.
 */
@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
public class RoomResource {

    private final DataStore store = DataStore.getInstance();

    /**
     * GET /api/v1/rooms â€” Return all rooms
     */
    @GET
    public Response getAllRooms() {
        List<Room> rooms = new ArrayList<>(store.getRooms().values());
        return Response.ok(rooms).build();
    }

    /**
     * POST /api/v1/rooms â€” Create a new room
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createRoom(Room room) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Room ID is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (store.roomExists(room.getId())) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 409);
            error.put("error", "Conflict");
            error.put("message", "A room with ID '" + room.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        store.putRoom(room);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Room created successfully.");
        response.put("room", room);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    /**
     * GET /api/v1/rooms/{roomId} â€” Get a specific room by ID
     */
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);
        if (room == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
        return Response.ok(room).build();
    }

    /**
     * DELETE /api/v1/rooms/{roomId} â€” Delete a room (blocked if sensors are assigned)
     */
    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Room room = store.getRoom(roomId);

        if (room == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Room with ID '" + roomId + "' does not exist.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Business Logic Constraint: cannot delete a room with active sensors
        if (room.getSensorIds() != null && !room.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException(
                "Room '" + roomId + "' cannot be deleted because it still has " +
                room.getSensorIds().size() + " sensor(s) assigned: " + room.getSensorIds()
            );
        }

        store.deleteRoom(roomId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Room '" + roomId + "' has been successfully decommissioned.");
        response.put("roomId", roomId);
        return Response.ok(response).build();
    }
}
