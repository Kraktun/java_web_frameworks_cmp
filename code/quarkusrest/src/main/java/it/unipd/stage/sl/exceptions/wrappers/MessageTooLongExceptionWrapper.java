package it.unipd.stage.sl.exceptions.wrappers;

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MessageTooLongExceptionWrapper implements ExceptionMapper<MessageTooLongException> {

    final String RESPONSE_TYPE = "text/plain";

    @Override
    public Response toResponse(MessageTooLongException exception) {
        return Response.serverError()
                .entity(exception.getMessage())
                .type(RESPONSE_TYPE)
                .status(Response.Status.BAD_REQUEST)
                .build();
    }
}
