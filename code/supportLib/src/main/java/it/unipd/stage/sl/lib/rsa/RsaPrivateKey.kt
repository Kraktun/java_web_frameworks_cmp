package it.unipd.stage.sl.lib.rsa

import java.math.BigInteger

data class RsaPrivateKey(val n: BigInteger, val d: BigInteger)