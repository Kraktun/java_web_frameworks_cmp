package it.unipd.stage.sl.objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

/**
 * Object returned as the result of an operation of encryption/decryption
 */
@RegisterForReflection
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

    @Override
    public String toString() {
        return "RsaResult{" +
                "message= '" + message + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RsaResult rsaResult = (RsaResult) o;
        return Objects.equals(message, rsaResult.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(message);
    }
}
