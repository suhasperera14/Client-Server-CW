package com.smartcampus.filter;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Cross-cutting logging filter for all API requests and responses.
 *
 * Implements both ContainerRequestFilter and ContainerResponseFilter to intercept
 * every incoming request and every outgoing response in a single class.
 *
 * Why filters over manual Logger.info() calls:
 * Filters implement the "cross-cutting concerns" principle from AOP (Aspect-Oriented
 * Programming). Logging is a concern that applies to EVERY endpoint, not just some.
 * Putting Logger.info() in every resource method violates DRY (Don't Repeat Yourself),
 * scatters logging logic across dozens of classes, and makes it easy to forget logging
 * in new methods. Filters apply logging declaratively to ALL requests automatically,
 * regardless of how many resource classes or methods are added in the future. They
 * also execute even when exceptions are thrown, ensuring complete observability that
 * per-method logging would miss. This makes the codebase cleaner, more maintainable,
 * and the logging behaviour more consistent and reliable.
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingFilter.class.getName());

    /**
     * Logs incoming HTTP request details before the resource method executes.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        String remoteAddr = "unknown";

        LOGGER.info(String.format(
            "[REQUEST]  Method=%-7s URI=%s  RemoteAddr=%s",
            method, uri, remoteAddr
        ));
    }

    /**
     * Logs outgoing HTTP response status after the resource method executes.
     */
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {
        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();
        int status = responseContext.getStatus();

        LOGGER.info(String.format(
            "[RESPONSE] Method=%-7s URI=%s  Status=%d",
            method, uri, status
        ));
    }
}
