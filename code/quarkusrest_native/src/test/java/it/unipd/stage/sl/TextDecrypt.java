package it.unipd.stage.sl;

import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;

/*
Object used to simplify encrypt test requests
 */
public class TextDecrypt {

    public String text;
    public PublicKey publicKey;
    public PrivateKey privateKey;

    public TextDecrypt(String text, PublicKey publicKey, PrivateKey privateKey) {
        this.text = text;
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }
}
