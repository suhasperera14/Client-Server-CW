package com.smartcampus;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

import com.smartcampus.resource.DiscoveryResource;
import com.smartcampus.resource.RoomResource;
import com.smartcampus.resource.SensorResource;
import com.smartcampus.exception.RoomNotEmptyExceptionMapper;
import com.smartcampus.exception.LinkedResourceNotFoundExceptionMapper;
import com.smartcampus.exception.SensorUnavailableExceptionMapper;
import com.smartcampus.exception.GlobalExceptionMapper;
import com.smartcampus.filter.LoggingFilter;

/**
 * JAX-RS Application configuration class.
 *
 * Lifecycle Note: By default, JAX-RS resource classes follow a "per-request"
 * lifecycle â€” a new instance is created for every incoming HTTP request. This
 * means instance fields cannot be used to store shared state. To manage
 * in-memory data safely across requests, we use a singleton DataStore class
 * backed by ConcurrentHashMap, which provides thread-safe operations without
 * explicit synchronization on every access.
 */
@ApplicationPath("/api/v1")
public class SmartCampusApplication extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();

        // Resources
        classes.add(DiscoveryResource.class);
        classes.add(RoomResource.class);
        classes.add(SensorResource.class);

        // Exception Mappers
        classes.add(RoomNotEmptyExceptionMapper.class);
        classes.add(LinkedResourceNotFoundExceptionMapper.class);
        classes.add(SensorUnavailableExceptionMapper.class);
        classes.add(GlobalExceptionMapper.class);

        // Filters
        classes.add(LoggingFilter.class);

        return classes;
    }
}
