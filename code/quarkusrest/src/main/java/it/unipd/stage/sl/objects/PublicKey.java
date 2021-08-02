package it.unipd.stage.sl.objects;

import it.unipd.stage.sl.lib.rsa.RsaPublicKey;
import it.unipd.stage.sl.validators.BigIntegerA;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Local representation of a RsaPublicKey
 */
public class PublicKey {

    @NotBlank
    @BigIntegerA
    String n;

    @NotBlank
    @BigIntegerA
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

    @Override
    public String toString() {
        return "PublicKey{" +
                "n= '" + n + '\'' +
                ", e= '" + e + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PublicKey publicKey = (PublicKey) o;
        return Objects.equals(n, publicKey.n) && Objects.equals(e, publicKey.e);
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, e);
    }
}
