package it.unipd.stage.sl.springrest.objects;

/**
 * Object returned as the result of an operation of encryption/decryption
 */
public class RsaResult {

    String message;

    public RsaResult() {
    }

    public RsaResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
