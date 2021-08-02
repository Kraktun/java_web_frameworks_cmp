package it.unipd.stage.sl.services;

import io.smallrye.mutiny.Uni;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;

/**
 * Interface that exposes the available methods that the controller can call.
 */
public interface RsaService {

    Uni<KeyHolder> generateKeys(int bitLength, long seed);

    Uni<KeyHolder> generateKeys(String bitLength, String seed);

    Uni<String> encryptMessage(String message, PublicKey pb);

    Uni<String> decryptMessage(String message, PublicKey pb, PrivateKey pr);

    Uni<PrivateKey> bruteKey(PublicKey pb);
}
