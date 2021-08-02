package it.unipd.stage.sl.lib.rsa

import java.math.BigInteger

data class RsaPublicKey(val n: BigInteger, val e: BigInteger)
