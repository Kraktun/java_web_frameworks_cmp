package it.unipd.stage.sl.services;

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.objects.KeyHolder;
import it.unipd.stage.sl.objects.PrivateKey;
import it.unipd.stage.sl.objects.PublicKey;

/**
 * Interface that exposes the available methods that the controller can call.
 */
public interface BlockingRsaService {

    KeyHolder generateKeys(int bitLength, long seed);

    String encryptMessage(String message, PublicKey pb) throws MessageTooLongException;

    String decryptMessage(String message, PublicKey pb, PrivateKey pr);

    PrivateKey bruteKey(PublicKey pb);
}
