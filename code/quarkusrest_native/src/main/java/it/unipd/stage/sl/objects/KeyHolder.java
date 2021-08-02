package it.unipd.stage.sl.objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

import java.util.Objects;

/**
 * Object that keeps a pair of public and private keys, returned when the API is asked to generate them
 */
@RegisterForReflection
public class KeyHolder {

    PublicKey publicKey;
    PrivateKey privateKey;

    public KeyHolder() {
    }

    public KeyHolder(PublicKey publicKey, PrivateKey privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public String toString() {
        return "KeyHolder{" +
                "publicKey= " + publicKey +
                ", privateKey= " + privateKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KeyHolder keyHolder = (KeyHolder) o;
        return Objects.equals(publicKey, keyHolder.publicKey) && Objects.equals(privateKey, keyHolder.privateKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(publicKey, privateKey);
    }
}
