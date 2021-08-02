package it.unipd.stage.sl.objects;

import io.quarkus.runtime.annotations.RegisterForReflection;
import it.unipd.stage.sl.lib.rsa.RsaPrivateKey;
import it.unipd.stage.sl.validators.BigIntegerA;

import javax.validation.constraints.NotBlank;
import java.math.BigInteger;
import java.util.Objects;

/**
 * Local representation of a RsaPrivateKey
 */
@RegisterForReflection
public class PrivateKey {


    @NotBlank // use some more annotations
    @BigIntegerA
    String n;

    @NotBlank
    @BigIntegerA
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
