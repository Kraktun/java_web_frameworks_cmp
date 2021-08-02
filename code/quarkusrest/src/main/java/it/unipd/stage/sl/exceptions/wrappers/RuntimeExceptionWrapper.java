package it.unipd.stage.sl.exceptions.wrappers;

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class RuntimeExceptionWrapper implements ExceptionMapper<RuntimeException> {

    final String RESPONSE_TYPE = "text/plain";

    @Override
    public Response toResponse(RuntimeException exception) {
        // use getCause for underlying messageTooLongExceptions when thrown from a reactive method
        Exception report;
        if (exception.getCause() instanceof MessageTooLongException)
            report = (MessageTooLongException) exception.getCause();
        else
            report = exception;
        // Note: NullPointerExceptions are RuntimeExceptions, no need for a separate wrapper
        return Response.serverError()
                .entity(report.getMessage())
                .type(RESPONSE_TYPE)
                .status(Response.Status.BAD_REQUEST)
                .build();
    }
}
