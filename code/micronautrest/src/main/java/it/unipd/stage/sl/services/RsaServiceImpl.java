package it.unipd.stage.sl.services;

import it.unipd.stage.sl.lib.rsa.RsaManager;
import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;

import javax.inject.Singleton;

/**
 * Rsa service implementation.
 * This service calls the necessary functions from the library and returns the results to the controller.
 */
@Singleton
public class RsaServiceImpl implements RsaService {

    @Override
    public KeyHolder generateKeys(int bitLength, long seed) {
        RsaManager m = RsaManager.fromGenerator(bitLength, seed, false);
        return new KeyHolder(PublicKey.fromRsa(m.getPublicKey()), PrivateKey.fromRsa(m.getPrivateKey()));
    }

    @Override
    public String encryptMessage(String message, PublicKey pb) throws MessageTooLongException {
        RsaManager m = new RsaManager(pb.toRsa());
        return m.encryptText(message);
    }

    @Override
    public String decryptMessage(String message, PublicKey pb, PrivateKey pr) throws UninitializedPrivateKeyException {
        RsaManager m = new RsaManager(pr.toRsa(), pb.toRsa());
        return m.decryptText(message);
    }

    @Override
    public PrivateKey bruteKey(PublicKey pb) {
        return PrivateKey.fromRsa(RsaManager.findPrivateKey(pb.toRsa()));
    }
}
