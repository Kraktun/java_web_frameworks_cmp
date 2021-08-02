package it.unipd.stage.sl.springrest.services;

import it.unipd.stage.sl.springrest.objects.KeyHolder;
import it.unipd.stage.sl.springrest.objects.PrivateKey;
import it.unipd.stage.sl.springrest.objects.PublicKey;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
* Service implementation for rsa with test profile
 * Returns some fixed values to the controller.
 */
@Service
@Profile("test")
public class RsaTestServiceImpl implements RsaService {

    @Override
    public KeyHolder generateKeys(int bitLength, long seed) {
        PrivateKey pr = new PrivateKey("5", "8");
        PublicKey pb = new PublicKey("5", "7");
        return new KeyHolder(pb, pr);
    }

    @Override
    public String encryptMessage(String message, PublicKey pb) {
        return "encrypted";
    }

    @Override
    public String decryptMessage(String message, PublicKey pb, PrivateKey pr) {
        return "decrypted";
    }

    @Override
    public PrivateKey bruteKey(PublicKey pb) {
        return new PrivateKey("1", "2");
    }
}
