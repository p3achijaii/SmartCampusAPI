package exception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.HashMap;
import java.util.Map;

@Provider
public class LinkedResourceNotFoundExceptionMapper implements ExceptionMapper<LinkedResourceNotFoundException> {

    @Override
    public Response toResponse(LinkedResourceNotFoundException exception) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", "Unprocessable Entity");
        error.put("status", 422);
        error.put("message", exception.getMessage());
        error.put("detail", "The referenced resource (roomId) does not exist in the system. "
                + "Please ensure the linked resource is created before referencing it.");

        return Response.status(422)
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}