package it.unipd.stage.sl.errors;

public class CreationErrorException extends RuntimeException {

    public CreationErrorException() {
        super("Resource could not be created");
    }

    public CreationErrorException(String add) {
        super("Resource could not be created with identifier: " + add);
    }
}