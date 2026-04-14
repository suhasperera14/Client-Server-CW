package com.smartcampus.exception;

/**
 * Thrown when a reading is posted to a sensor that is in MAINTENANCE or OFFLINE status.
 * Maps to HTTP 403 Forbidden.
 */
public class SensorUnavailableException extends RuntimeException {

    public SensorUnavailableException(String message) {
        super(message);
    }
}
