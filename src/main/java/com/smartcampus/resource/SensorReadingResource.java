package com.smartcampus.resource;

import com.smartcampus.DataStore;
import com.smartcampus.exception.SensorUnavailableException;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Sub-resource class handling sensor reading history.
 * Accessed via: /api/v1/sensors/{sensorId}/readings
 *
 * This class is instantiated by SensorResource's sub-resource locator method,
 * receiving the sensorId as a constructor argument. This demonstrates the
 * Sub-Resource Locator pattern â€” JAX-RS calls getReadingsResource() to obtain
 * this instance, then dispatches the remaining path segment to it.
 */
@Produces(MediaType.APPLICATION_JSON)
public class SensorReadingResource {

    private final String sensorId;
    private final DataStore store = DataStore.getInstance();

    public SensorReadingResource(String sensorId) {
        this.sensorId = sensorId;
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings â€” Get all readings for a sensor
     */
    @GET
    public Response getReadings() {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<SensorReading> readings = store.getReadings(sensorId);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("sensorId", sensorId);
        response.put("count", readings.size());
        response.put("readings", readings);
        return Response.ok(response).build();
    }

    /**
     * POST /api/v1/sensors/{sensorId}/readings â€” Append a new reading
     *
     * Side Effect: Updates the parent Sensor's currentValue to keep data consistent.
     *
     * Error Handling: A sensor in MAINTENANCE status is physically disconnected
     * and cannot accept new readings â€” throws SensorUnavailableException (HTTP 403).
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addReading(SensorReading reading) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        // State Constraint: MAINTENANCE sensors cannot accept readings
        if ("MAINTENANCE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is currently under MAINTENANCE and cannot record readings. " +
                "Please update sensor status to ACTIVE before posting readings."
            );
        }

        // Also reject OFFLINE sensors
        if ("OFFLINE".equalsIgnoreCase(sensor.getStatus())) {
            throw new SensorUnavailableException(
                "Sensor '" + sensorId + "' is OFFLINE and cannot record readings."
            );
        }

        if (reading == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 400);
            error.put("error", "Bad Request");
            error.put("message", "Reading body is required.");
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }

        // Auto-generate ID and timestamp if not provided
        if (reading.getId() == null || reading.getId().trim().isEmpty()) {
            reading = new SensorReading(reading.getValue());
        }
        if (reading.getTimestamp() == 0) {
            reading.setTimestamp(System.currentTimeMillis());
        }

        // Persist the reading
        store.addReading(sensorId, reading);

        // Side Effect: Update the parent sensor's currentValue for data consistency
        sensor.setCurrentValue(reading.getValue());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Reading recorded successfully.");
        response.put("sensorId", sensorId);
        response.put("updatedSensorValue", sensor.getCurrentValue());
        response.put("reading", reading);
        return Response.status(Response.Status.CREATED).entity(response).build();
    }

    /**
     * GET /api/v1/sensors/{sensorId}/readings/{readingId} â€” Get a specific reading
     */
    @GET
    @Path("/{readingId}")
    public Response getReading(@PathParam("readingId") String readingId) {
        Sensor sensor = store.getSensor(sensorId);
        if (sensor == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Sensor with ID '" + sensorId + "' not found.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        List<SensorReading> readings = store.getReadings(sensorId);
        SensorReading found = readings.stream()
            .filter(r -> r.getId().equals(readingId))
            .findFirst()
            .orElse(null);

        if (found == null) {
            Map<String, Object> error = new LinkedHashMap<>();
            error.put("status", 404);
            error.put("error", "Not Found");
            error.put("message", "Reading with ID '" + readingId + "' not found for sensor '" + sensorId + "'.");
            return Response.status(Response.Status.NOT_FOUND).entity(error).build();
        }

        return Response.ok(found).build();
    }
}
