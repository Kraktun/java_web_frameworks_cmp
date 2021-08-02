package it.unipd.stage.sl.errors;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.server.exceptions.ExceptionHandler;

public class GenericBadRequestHandler<T extends Exception> implements ExceptionHandler<T, HttpResponse<String>> {

    @Override
    public HttpResponse<String> handle(HttpRequest request, T exception) {
        return HttpResponse.badRequest(exception.getMessage());
    }
}
