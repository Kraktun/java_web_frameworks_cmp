package it.unipd.stage.sl.springrest.objects;

import it.unipd.stage.sl.lib.rsa.RsaPublicKey;

import java.math.BigInteger;

/**
 * Local representation of a RsaPublicKey
 */
public class PublicKey {

    String n;
    String e;

    public PublicKey() { }

    public PublicKey(String n, String e) {
        this.n = n;
        this.e = e;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getE() {
        return e;
    }

    public void setE(String e) {
        this.e = e;
    }

    public RsaPublicKey toRsa() {
        return new RsaPublicKey(new BigInteger(getN()), new BigInteger(getE()));
    }

    public static PublicKey fromRsa(RsaPublicKey pb) {
        return new PublicKey(pb.getN().toString(), pb.getE().toString());
    }
}
