package it.unipd.stage.sl;

import it.unipd.stage.sl.objects.PublicKey;

/*
Object used to simplify encrypt test requests
 */
public class TextEncrypt {

    public String text;
    public PublicKey publicKey;

    public TextEncrypt(String text, PublicKey publicKey) {
        this.text = text;
        this.publicKey = publicKey;
    }
}
