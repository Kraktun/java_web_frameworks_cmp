package it.unipd.stage.sl.reactive;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import it.unipd.stage.sl.lib.rsa.RsaManager;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;

/*
Abstract class with a bunch of static methods to wrap blocking calls to the RSA library in reactive events
 */
public abstract class RsaReactive {

    public static Uni<RsaManager> getReactiveRsaManager(int bitLength, long seed) {
        // I don't know why the seed is not working properly (the value is parsed correctly) in SecureRandom
        // The library has no problem in Spring or Micronaut, only here.
        // Also does not work also if we use SecureRandom in a blocking/reactive/async method.
        return Uni.createFrom().item(RsaManager.fromGenerator(bitLength, seed, false));
    }

    public static Uni<RsaManager> getReactiveRsaManager(String bitLength, String seed) {
        try {
            int b = Integer.parseInt(bitLength);
            long s = Long.parseLong(seed);
            return getReactiveRsaManager(b, s);
        } catch (NumberFormatException e) {
            return Uni.createFrom().failure(()->e);
        }
    }

    public static Uni<RsaManager> getReactiveRsaManager(RsaManager m) {
        return Uni.createFrom().item(m);
    }

    public static Uni<RsaManager> getReactiveRsaManager(PublicKey pb) {
        return Uni.createFrom().item(new RsaManager(pb.toRsa()));
    }

    public static Uni<RsaManager> getReactiveRsaManager(PublicKey pb, PrivateKey pr) {
        return Uni.createFrom().item(new RsaManager(pr.toRsa(), pb.toRsa()));
    }

    public static Uni<String> encryptReactiveRsa(Uni<RsaManager> m, String text) {
        // Unchecked.function wraps the original exception in a runtimeException compatible with Uni. Use getCause on the fail condition to access the original exception
        return m.onItem().transform(Unchecked.function(it -> it.encryptText(text)));
    }

    public static Uni<String> encryptReactiveRsa(PublicKey pb, String text) {
        return getReactiveRsaManager(pb).onItem().transform(Unchecked.function(it -> it.encryptText(text)));
    }

    public static Uni<String> decryptReactiveRsa(PublicKey pb, PrivateKey pr, String message) {
        // No need for Unchecked.function because we throw only unchecked exceptions here (aka Runtime)
        return getReactiveRsaManager(pb, pr).onItem().transform(it -> it.decryptText(message));
    }

    public static Uni<PrivateKey> bruteReactiveRsa(PublicKey pb) {
        return Uni.createFrom().item(pb).onItem().transform(pub -> RsaManager.findPrivateKey(pub.toRsa())).onItem().transform(PrivateKey::fromRsa);
    }
}
