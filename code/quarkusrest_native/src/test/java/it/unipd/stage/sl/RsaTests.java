package it.unipd.stage.sl;

import io.smallrye.mutiny.Uni;
import it.unipd.stage.sl.lib.rsa.RsaManager;
import it.unipd.stage.sl.lib.rsa.RsaPrivateKey;
import it.unipd.stage.sl.lib.rsa.RsaPublicKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.reactive.RsaReactive;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

public class RsaTests {

    @Test
    public void testReactiveManager() {
        Uni<RsaManager> manager = RsaReactive.getReactiveRsaManager(54, 10);
        RsaPrivateKey pr = manager.onItem().transform(RsaManager::getPrivateKey).await().indefinitely();
        RsaPublicKey pb = manager.onItem().transform(RsaManager::getPublicKey).await().indefinitely();
        BigInteger d = new BigInteger("1987201354784969");
        BigInteger n = new BigInteger("10460274474002053");
        BigInteger e = new BigInteger("5223497001888909");
        assert d.equals(pr.getD()) && n.equals(pr.getN()) && e.equals(pb.getE());
    }

    @Test
    public void testReactiveBrute() {
        Uni<RsaManager> manager = RsaReactive.getReactiveRsaManager(54, 10);
        RsaPublicKey pb = manager.onItem().transform(RsaManager::getPublicKey).await().indefinitely();
        RsaPrivateKey testPr = RsaReactive.bruteReactiveRsa(PublicKey.fromRsa(pb)).await().indefinitely().toRsa();
        BigInteger d = new BigInteger("1987201354784969");
        BigInteger n = new BigInteger("10460274474002053");
        assert d.equals(testPr.getD()) && n.equals(testPr.getN());
    }
}
