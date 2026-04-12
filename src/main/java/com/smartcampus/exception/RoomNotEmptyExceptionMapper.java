package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Maps RoomNotEmptyException to HTTP 409 Conflict with a descriptive JSON body.
 *
 * This prevents raw Java exceptions from leaking to API consumers and provides
 * a semantically correct status code: 409 Conflict indicates the request cannot
 * be fulfilled due to a conflict with the current state of the resource.
 */
@Provider
public class RoomNotEmptyExceptionMapper implements ExceptionMapper<RoomNotEmptyException> {

    @Override
    public Response toResponse(RoomNotEmptyException exception) {
        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 409);
        error.put("error", "Conflict");
        error.put("message", exception.getMessage());
        error.put("hint", "Please reassign or delete all sensors in this room before attempting deletion.");

        return Response
            .status(Response.Status.CONFLICT)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
