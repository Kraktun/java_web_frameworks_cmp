package it.unipd.stage.sl.springrest.services;

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException;
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException;
import it.unipd.stage.sl.springrest.objects.KeyHolder;
import it.unipd.stage.sl.springrest.objects.PrivateKey;
import it.unipd.stage.sl.springrest.objects.PublicKey;

/**
 * Interface that exposes the available methods that the controller can call.
 */
public interface RsaService {

    KeyHolder generateKeys(int bitLength, long seed);

    String encryptMessage(String message, PublicKey pb) throws MessageTooLongException;

    String decryptMessage(String message, PublicKey pb, PrivateKey pr) throws UninitializedPrivateKeyException;

    PrivateKey bruteKey(PublicKey pb);
}
