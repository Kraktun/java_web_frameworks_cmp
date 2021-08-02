import it.unipd.stage.sl.lib.rsa.RsaManager;
import it.unipd.stage.sl.lib.rsa.RsaPrivateKey;
import it.unipd.stage.sl.lib.rsa.RsaPublicKey;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import static it.unipd.stage.sl.lib.rsa.RsaUtilsKt.*;

public class TestKeyGen {

    @Test
    public void testKeyGenWiki() {
        // From https://en.wikipedia.org/wiki/RSA_(cryptosystem)
        BigInteger p = new BigInteger(String.valueOf(61));
        BigInteger q = new BigInteger(String.valueOf(53));
        BigInteger n = p.multiply(q);
        assert n.equals(new BigInteger(String.valueOf(3233)));
        BigInteger y = lcm(p.subtract(BigInteger.ONE), (q.subtract(BigInteger.ONE)));
        assert y.equals(new BigInteger(String.valueOf(780)));
        BigInteger e = new BigInteger(String.valueOf(17));
        BigInteger d = modInverse(e,y);
        assert d.equals(new BigInteger(String.valueOf(413)));
    }

    @Test
    public void testEncodeDecode() throws MessageTooLongException, UninitializedPrivateKeyException {
        RsaManager m = RsaManager.fromGenerator(80, 10, true);
        // System.out.println("E: " + m.getPublicKey().getE() + " N: " + m.getPublicKey().getN());
        String original = "Seaaafghj7"; // max length for keyLength = 80 is 10
        String enc = m.encryptText(original);
        System.out.println(enc);
        String dec = m.decryptText(enc);
        System.out.println(dec);
        assert original.equals(dec);
    }

    @Test
    public void testFactor() {
        // with key length = 48 it takes 1s e.g. 33389267 and 18337457
        // with 56 it takes 15-25s e.g. 989690579 and 721135057
        // with 64 it takes 71s e.g. 3070835791 and 2224455329
        // with 72 it takes too long
        BigInteger a = new BigInteger("989690579");
        BigInteger b = new BigInteger("721135057");
        long start = System.currentTimeMillis();
        Set<BigInteger> result = factorize(a.multiply(b));
        long end = System.currentTimeMillis();
        System.out.println("For: " + a.multiply(b));
        for (BigInteger i : result)
            System.out.println(i);
        System.out.println("Factorization took: " + (end - start)/1000. + " s");
        assert result.contains(a.min(b));
    }

    @Test
    public void testFactorFull() {
        BigInteger a = new BigInteger("989690579");
        BigInteger b = new BigInteger("721135057");
        long start = System.currentTimeMillis();
        List<BigInteger> result = factorizeFull(a.multiply(b));
        long end = System.currentTimeMillis();
        System.out.println("For: " + a.multiply(b));
        for (BigInteger i : result)
            System.out.println(i);
        System.out.println("Factorization took: " + (end - start)/1000. + " s");
        assert result.contains(a) && result.contains(b);
    }

    @Test
    public void testKeyBrute() {
        RsaManager m = RsaManager.fromGenerator(56, 15, true);
        RsaPublicKey pb = m.getPublicKey();
        RsaPrivateKey pr = RsaManager.findPrivateKey(pb);
        assert pr != null && pr.equals(m.getPrivateKey());
    }
}
