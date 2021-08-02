package it.unipd.stage.sl.services;

import io.smallrye.mutiny.Uni;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;
import it.unipd.stage.sl.reactive.RsaReactive;

import javax.enterprise.context.ApplicationScoped;

/**
 * Rsa service implementation.
 * This service calls the necessary functions from the library and returns the results to the controller.
 * Uses reactive methods.
 */
@ApplicationScoped
public class RsaServiceImpl implements RsaService {

    @Override
    public Uni<KeyHolder> generateKeys(int bitLength, long seed) {
        return RsaReactive.getReactiveRsaManager(bitLength, seed).onItem()
                .transform(it -> new KeyHolder(PublicKey.fromRsa(it.getPublicKey()), PrivateKey.fromRsa(it.getPrivateKey())));
    }

    @Override
    public Uni<KeyHolder> generateKeys(String bitLength, String seed) {
        return RsaReactive.getReactiveRsaManager(bitLength, seed).onItem()
                .transform(it -> new KeyHolder(PublicKey.fromRsa(it.getPublicKey()), PrivateKey.fromRsa(it.getPrivateKey())));
    }

    @Override
    public Uni<String> encryptMessage(String message, PublicKey pb) {
        return RsaReactive.encryptReactiveRsa(pb, message);
    }

    @Override
    public Uni<String> decryptMessage(String message, PublicKey pb, PrivateKey pr) throws UninitializedPrivateKeyException {
        return RsaReactive.decryptReactiveRsa(pb, pr, message);
    }

    @Override
    public Uni<PrivateKey> bruteKey(PublicKey pb) {
        return RsaReactive.bruteReactiveRsa(pb);
    }
}
