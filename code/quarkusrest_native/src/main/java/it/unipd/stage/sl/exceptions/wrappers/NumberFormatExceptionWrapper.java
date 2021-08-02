package it.unipd.stage.sl.exceptions.wrappers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NumberFormatExceptionWrapper implements ExceptionMapper<NumberFormatException> {

    final String RESPONSE_TYPE = "text/plain";

    @Override
    public Response toResponse(NumberFormatException exception) {
        return Response.serverError()
                .entity(exception.getMessage())
                .type(RESPONSE_TYPE)
                .status(Response.Status.BAD_REQUEST)
                .build();
    }
}
