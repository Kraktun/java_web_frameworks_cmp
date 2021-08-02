package it.unipd.stage.sl.errors;

import io.micronaut.context.annotation.Requires;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;

import javax.inject.Singleton;

@Produces
@Singleton
@Requires(classes = {CreationErrorException.class, ExceptionHandler.class})
public class CreationErrorExceptionHandler implements ExceptionHandler<CreationErrorException, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, CreationErrorException exception) {
        return HttpResponse.badRequest(exception.getMessage()).status(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}