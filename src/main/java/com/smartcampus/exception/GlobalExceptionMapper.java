package com.smartcampus.exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Global catch-all ExceptionMapper that intercepts any unhandled Throwable.
 *
 * This is the "safety net" â€” it ensures no raw Java stack trace or default
 * server error page ever reaches the API consumer.
 *
 * Security rationale for hiding stack traces:
 * Exposing stack traces is a serious security vulnerability because:
 * 1. CLASS PATHS: Reveal the internal package structure and framework versions,
 *    enabling attackers to look up known CVEs for those exact versions.
 * 2. TECHNOLOGY FINGERPRINTING: Library names (Jersey, Jackson, Tomcat) expose
 *    the complete tech stack, narrowing the attack surface to known exploits.
 * 3. DATA LEAKAGE: Stack traces may contain variable values, SQL query fragments,
 *    file system paths, or internal IP addresses captured at the exception point.
 * 4. LOGIC EXPOSURE: Line numbers and method names reveal business logic internals,
 *    helping attackers understand control flow for crafting targeted exploits.
 *
 * The secure approach is to log the full exception server-side (for debugging)
 * while returning only a generic, opaque error message to the client.
 */
@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {

    private static final Logger LOGGER = Logger.getLogger(GlobalExceptionMapper.class.getName());

    @Override
    public Response toResponse(Throwable exception) {
        // Log full details server-side for debugging â€” never expose to client
        LOGGER.log(Level.SEVERE, "Unhandled exception intercepted by GlobalExceptionMapper", exception);

        Map<String, Object> error = new LinkedHashMap<>();
        error.put("status", 500);
        error.put("error", "Internal Server Error");
        error.put("message", "An unexpected error occurred. Please contact the API administrator.");

        return Response
            .status(Response.Status.INTERNAL_SERVER_ERROR)
            .entity(error)
            .type(MediaType.APPLICATION_JSON)
            .build();
    }
}
