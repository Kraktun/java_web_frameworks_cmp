package it.unipd.stage.sl.springrest.objects;

/**
 * Object that keeps a pair of public and private keys, returned when the API is asked to generate them
 */
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
}
