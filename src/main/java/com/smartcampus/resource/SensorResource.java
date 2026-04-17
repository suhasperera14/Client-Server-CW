package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.LinkedResourceNotFoundException;
import com.smartcampus.model.Sensor;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Resource class managing /api/v1/sensors endpoints.
 *
 * @Consumes(APPLICATION_JSON) consequence:
 * If a client sends data with Content-Type: text/plain or application/xml,
 * JAX-RS returns HTTP 415 Unsupported Media Type automatically, before the
 * method body is even reached. The runtime inspects the Content-Type header
 * and matches it against registered @Consumes annotations; a mismatch means
 * no method is eligible to handle the request, resulting in the 415 response.
 *
 * QueryParam vs Path-based filtering:
 * Using ?type=CO2 is superior for filtering because query parameters are
 * optional by nature â€” omitting them returns the full collection. Path segments
 * like /sensors/type/CO2 imply a resource hierarchy that doesn't exist and
 * can conflict with other path patterns (e.g., /sensors/{id}). Query parameters
 * are also the conventional REST idiom for filtering, sorting, and pagination,
 * making APIs more intuitive and consistent for developers.
 */
@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
public class SensorResource {

    private final DataStore store = DataStore.getInstance();

    /**
     * GET /api/v1/sensors â€” Get all sensors, optionally filtered by type
     */
    @GET
    public Response getAllSensors(@QueryParam("type") String type) {
        List<Sensor> sensorList = new ArrayList<>(store.getSensors().values());

        if (type != null && !type.trim().isEmpty()) {
            sensorList = sensorList.stream()
                .filter(s -> s.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());
        }

        return Response.ok(sensorList).build();
    }

    /**
     * POST /api/v1/sensors â€” Register a new sensor
     * Validates that the referenced roomId exists before creation.
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createSensor(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getId().trim().isEmpty()) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Sensor ID is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        if (store.sensorExists(sensor.getId())) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 409);
            error.put("error", "Conflict");
            error.put("message", "A sensor with ID '" + sensor.getId() + "' already exists.");
            return Response.status(Response.Status.CONFLICT).entity(error).build();
        }

        // Validate that the referenced roomId actually exists
        if (sensor.getRoomId() == null || !store.roomExists(sensor.getRoomId())) {
            throw new LinkedResourceNotFoundException(
                "Cannot register sensor: Room with ID '" + sensor.getRoomId() + "' does not exist. " +
                "Please create the room first or use a valid roomId."
            );
        }

        // Validate status value
        if (sensor.getStatus() == null) {
            sensor.setStatus("ACTIVE");
        }

        store.putSensor(sensor);

        // Link sensor to the room
        store.getRoom(sensor.getRoomId()).addSensorId(sensor.getId());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Sensor registered successfully.");
        response.put("sensor", sensor);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    /**
     * GET /api/v1/sensors/{sensorId} â€” Get a specific sensor
     */
    @GET
    @Path("/{sensorId}")
    public Response getSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }
        return Response.ok(sensor).build();
    }

    /**
     * DELETE /api/v1/sensors/{sensorId} â€” Remove a sensor
     */
    @DELETE
    @Path("/{sensorId}")
    public Response deleteSensor(@PathParam("sensorId") String sensorId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // Unlink from its room
        if (sensor.getRoomId() != null && store.roomExists(sensor.getRoomId())) {
            store.getRoom(sensor.getRoomId()).removeSensorId(sensorId);
        }

        store.deleteSensor(sensorId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Sensor '" + sensorId + "' has been removed.");
        response.put("sensorId", sensorId);
        return Response.ok(response).build();
    }

    /**
     * Sub-Resource Locator for /api/v1/sensors/{sensorId}/readings
     *
     * Architecture note: The Sub-Resource Locator pattern delegates the handling
     * of nested paths to a dedicated class. This is superior to monolithic controllers
     * because: (1) it separates concerns cleanly, (2) each class can be independently
     * tested, (3) it scales to arbitrarily deep hierarchies without one class growing
     * unbounded, and (4) JAX-RS can lazily instantiate sub-resource classes only when
     * the path segment is actually requested.
     */
    @Path("/{sensorId}/readings")
    public SensorReadingResource getReadingsResource(@PathParam("sensorId") String sensorId) {
        return new SensorReadingResource(sensorId);
    }
}
