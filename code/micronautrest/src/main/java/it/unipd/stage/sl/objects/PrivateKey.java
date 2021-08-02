package it.unipd.stage.sl.objects;

import io.micronaut.core.annotation.Introspected;
import it.unipd.stage.sl.lib.rsa.RsaPrivateKey;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Local representation of a RsaPrivateKey
 */
@Introspected
public class PrivateKey {

    @NotBlank // just to add some validation
    String n;

    @NotBlank
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

    // Map from/to library objects
    public RsaPrivateKey toRsa() {
        return new RsaPrivateKey(new BigInteger(getN()), new BigInteger(getD()));
    }

    public static PrivateKey fromRsa(RsaPrivateKey pr) {
        if (pr == null) return null;
        return new PrivateKey(pr.getN().toString(), pr.getD().toString());
    }

    @Override
    public String toString() {
        return "PrivateKey{" +
                "n= '" + n + '\'' +
                ", d= '" + d + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivateKey that = (PrivateKey) o;
        return Objects.equals(n, that.n) && Objects.equals(d, that.d); // objects.equals to manage nulls
    }

    @Override
    public int hashCode() {
        return Objects.hash(n, d);
    }
}
