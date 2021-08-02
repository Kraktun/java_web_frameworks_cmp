package it.unipd.stage.sl.springrest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class CreationErrorException extends RuntimeException {

    public CreationErrorException() {
        super("Resource could not be created");
    }

    public CreationErrorException(String add) {
        super("Resource could not be created with identifier: " + add);
    }
}