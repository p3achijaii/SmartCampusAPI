package resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/test")
public class DiscoveryResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String test() {
        return "{\"message\": \"API is working\"}";
    }
}