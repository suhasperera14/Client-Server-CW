package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps SensorUnavailableException to HTTP 403 Forbidden.
 *
 * 403 is appropriate here because the server understands the request,
 * the endpoint exists, but it refuses to process the reading because the
 * sensor's current operational state (MAINTENANCE/OFFLINE) forbids it.
 */
@Provider
public class SensorUnavailableExceptionMapper implements ExceptionMapper<SensorUnavailableException> {

    @Override
    public Response toResponse(SensorUnavailableException exception) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 403);
        error.put("error", "Forbidden");
        error.put("message", exception.getMessage());
        error.put("hint", "Update the sensor status to ACTIVE before posting new readings.");

        return Response
            .status(Response.Status.FORBIDDEN)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
