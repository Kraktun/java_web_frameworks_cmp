package it.unipd.stage.sl.exceptions.wrappers;

import it.unipd.stage.sl.exceptions.EventIsUsedAsStarterException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/*
Wrapper class to convert an exception to a response, the @Provider annotation takes care of injecting this class when any compatible exception is thrown
 */
@Provider
public class EventIsUsedAsStarterExceptionWrapper implements ExceptionMapper<EventIsUsedAsStarterException> {

    final String RESPONSE_TYPE = "text/plain";

    @Override
    public Response toResponse(EventIsUsedAsStarterException exception) {
        return Response.serverError()
                .entity(exception.getMessage())
                .type(RESPONSE_TYPE)
                .status(Response.Status.BAD_REQUEST)
                .build();
    }
}
