package com.smartcampus.exception;

/**
 * Thrown when a resource references a linked entity that does not exist.
 * Example: POST /sensors with a roomId that doesn't exist in the system.
 * Maps to HTTP 422 Unprocessable Entity.
 *
 * Why 422 over 404:
 * HTTP 404 means the requested URL/resource was not found. HTTP 422 means
 * the server understands the request's Content-Type and the payload is
 * syntactically correct JSON, but the semantic content is invalid â€” specifically,
 * a reference inside the payload points to a non-existent resource. The client
 * sent a valid request to a valid endpoint, but the data itself is logically
 * inconsistent. 422 accurately communicates "your data is processable but
 * semantically wrong", whereas 404 would incorrectly imply the endpoint itself
 * was not found.
 */
public class LinkedResourceNotFoundException extends RuntimeException {

    public LinkedResourceNotFoundException(String message) {
        super(message);
    }
}
