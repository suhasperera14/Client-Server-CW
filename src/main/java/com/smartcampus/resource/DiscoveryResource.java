package com.smartcampus.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Discovery endpoint providing API metadata and resource links.
 *
 * HATEOAS Note: Hypermedia As The Engine Of Application State (HATEOAS) is
 * considered a hallmark of mature RESTful design (Richardson Maturity Level 3)
 * because it decouples clients from hardcoded URLs. Instead of relying on static
 * documentation, clients discover available actions and endpoints dynamically
 * from the response links. This reduces tight coupling, makes APIs self-documenting,
 * and allows the server to evolve its URL structure without breaking clients â€” they
 * simply follow the links provided rather than constructing URLs manually.
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DiscoveryResource {

    @GET
    public Response discover() {
        Map<String, Object> response = new LinkedHashMap<>();

        response.put("api", "Smart Campus Sensor & Room Management API");
        response.put("version", "1.0.0");
        response.put("description", "RESTful API for managing campus rooms and IoT sensors");
        response.put("contact", "admin@smartcampus.university.ac.uk");
        response.put("status", "operational");

        Map<String, String> links = new LinkedHashMap<>();
        links.put("self", "/api/v1");
        links.put("rooms", "/api/v1/rooms");
        links.put("sensors", "/api/v1/sensors");
        links.put("documentation", "/api/v1/docs");
        response.put("links", links);

        Map<String, String> resources = new LinkedHashMap<>();
        resources.put("rooms", "/api/v1/rooms");
        resources.put("sensors", "/api/v1/sensors");
        response.put("resources", resources);

        return Response.ok(response).build();
    }
}
