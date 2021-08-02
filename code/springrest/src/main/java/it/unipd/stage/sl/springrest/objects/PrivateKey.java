package it.unipd.stage.sl.springrest.objects;

import it.unipd.stage.sl.lib.rsa.RsaPrivateKey;

import java.math.BigInteger;

/**
 * Local representation of a RsaPrivateKey
 */
public class PrivateKey {

    String n;
    String d;

    public PrivateKey(String n, String d) {
        this.n = n;
        this.d = d;
    }

    public PrivateKey() {}

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public RsaPrivateKey toRsa() {
        return new RsaPrivateKey(new BigInteger(getN()), new BigInteger(getD()));
    }

    public static PrivateKey fromRsa(RsaPrivateKey pr) {
        if (pr == null) return null;
        return new PrivateKey(pr.getN().toString(), pr.getD().toString());
    }
}
