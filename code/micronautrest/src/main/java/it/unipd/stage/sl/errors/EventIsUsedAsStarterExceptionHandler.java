package it.unipd.stage.sl.errors;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {EventIsUsedAsStarterException.class, ExceptionHandler.class})
public class EventIsUsedAsStarterExceptionHandler extends GenericBadRequestHandler<EventIsUsedAsStarterException> {
}
