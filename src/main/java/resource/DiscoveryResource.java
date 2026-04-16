package resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/v1")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String discovery() {
        return "{ " +
                "\"version\": \"v1\", " +
                "\"message\": \"Smart Campus API\", " +
                "\"resources\": { " +
                "\"rooms\": \"/api/v1/rooms\", " +
                "\"sensors\": \"/api/v1/sensors\" " +
                "} " +
                "}";
    }
}