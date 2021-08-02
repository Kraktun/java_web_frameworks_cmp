package it.unipd.stage.sl.springrest.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class EventIsUsedAsStarterException extends RuntimeException {

    public EventIsUsedAsStarterException() {
        super("Event is used as starter in one or more chapters. Use force or cascade to delete it.");
    }
}