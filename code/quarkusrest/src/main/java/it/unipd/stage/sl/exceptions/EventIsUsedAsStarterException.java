package it.unipd.stage.sl.exceptions;

public class EventIsUsedAsStarterException extends RuntimeException {

    public EventIsUsedAsStarterException() {
        super("Event is used as starter in one or more chapters. Use force or cascade to delete it.");
    }
}