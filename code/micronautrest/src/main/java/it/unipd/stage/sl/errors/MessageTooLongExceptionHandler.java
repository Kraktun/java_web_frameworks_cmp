package it.unipd.stage.sl.errors;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {MessageTooLongException.class, ExceptionHandler.class})
public class MessageTooLongExceptionHandler extends GenericBadRequestHandler<MessageTooLongException> {
}
