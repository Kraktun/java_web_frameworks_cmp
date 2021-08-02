package it.unipd.stage.sl.lib.rsa

import it.unipd.stage.sl.lib.rsa.exceptions.MessageTooLongException
import it.unipd.stage.sl.lib.rsa.exceptions.UninitializedPrivateKeyException
import java.math.BigInteger
import java.security.SecureRandom
import java.util.*
import kotlin.math.floor
import kotlin.math.min
import kotlin.text.Charsets.UTF_8

class RsaManager {

    private var privateKey: RsaPrivateKey? = null
    private val publicKey: RsaPublicKey

    constructor(pubKey: RsaPublicKey) {
        this.publicKey = pubKey
    }

    constructor(privKey: RsaPrivateKey, pubKey: RsaPublicKey) {
        this.publicKey = pubKey
        this.privateKey = privKey
    }

    fun getPrivateKey(): RsaPrivateKey? {
        return this.privateKey
    }

    fun getPublicKey(): RsaPublicKey {
        return this.publicKey
    }

    companion object {
        /**
         * Get a pair of private-public keys with passed length and random seed
         *
         * @param keyLength length of the key
         * @param seed seed for the random generator
         * @param useSecureRandom to use secureRandom generator instead of simple random
         * @return RSAManager with initialized random keys
         */
        @JvmStatic
        fun fromGenerator(keyLength: Int, seed: Long, useSecureRandom: Boolean = true) : RsaManager {
            val p = generateRandomKeys(keyLength, seed, useSecureRandom)
            return RsaManager(privKey = p.first, pubKey = p.second)
        }

        /**
         * Compute random private and public key, given length l (in bits)
         *
         * @param length length of the key (bits)
         * @param seed seed for the random generator
         * @param useSecureRandom true to use SecureRandom() rather than Random()
         * @return pair of RsaPrivateKey, RsaPublicKey from a random number
         */
        private fun generateRandomKeys(length: Int, seed : Long, useSecureRandom: Boolean): Pair<RsaPrivateKey, RsaPublicKey> {
            // key has l bits:
            // choose p,q < 2^(l/2)
            // p,q must be primes
            // n = p*q
            // y = (p-1)*(q-1) // in general y = lcm(p-1, q-1) = (p-1)*(q-1)/gcd(p-1, q-1)
            // d s.t. gcd(y,d)=1
            // e s.t. e*d = 1 mod y
            // i.e. (e*d -1) is multiple of y i.e. e*d mod y = 1
            // note that e and d are interchangeable i.e. e can be coprime with y and d is computed from (e*d mod y = 1)

            // val seedBytes = ByteBuffer.allocate(Long.SIZE_BYTES).putLong(seed).flip().array()
            // val rand = SecureRandom(seedBytes)
            val rand = if (useSecureRandom) SecureRandom() else Random()
            rand.setSeed(seed)
            var p = BigInteger.probablePrime(floor(length / 2.0).toInt(), rand)
            while (!p.isPrime()) p = BigInteger.probablePrime(floor(length / 2.0).toInt(), rand)
            var q = BigInteger.probablePrime(floor(length / 2.0).toInt(), rand)
            while (!q.isPrime()) q = BigInteger.probablePrime(floor(length / 2.0).toInt(), rand)
            val n = p * q
            val y = lcm(p - BigInteger.ONE, q - BigInteger.ONE)
            val eLength = min(y.bitLength(), 16) // max length for e is 16, more than that it's useless
            var e = BigInteger(eLength, rand)
            while (e > y || !areCoprime(e, y)) e = BigInteger(length, rand)
            val d = modInverse(e,y)
            // println("P: $p Q: $q Y: $y D: $d")
            return Pair(RsaPrivateKey(n, d), RsaPublicKey(n, e))
        }

        /**
         * Compute private and public key, given p, q, e
         *
         * @param p
         * @param q
         * @param e
         * @return pair of RsaPrivateKey, RsaPublicKey from the given values
         */
        private fun generateKeys(p: BigInteger, q: BigInteger, e: BigInteger): Pair<RsaPrivateKey, RsaPublicKey> {
            val n = p * q
            val y = lcm(p - BigInteger.ONE, q - BigInteger.ONE)
            val d = modInverse(e,y)
            return Pair(RsaPrivateKey(n, d), RsaPublicKey(n, e))
        }

        /**
         * Decode an encrypted text with the given private key
         * Input encrypted text must be in base64
         *
         * @param s encoded text in base64
         * @param privKey private key to use to decrypt
         * @return decoded text as plaintext
         */
        @JvmStatic
        fun decryptText(s: String, privKey: RsaPrivateKey) : String {
            val bytes: ByteArray = Base64.getDecoder().decode(s)
            val x = BigInteger(bytes) // number representation
            val dec = x.modPow(privKey.d, privKey.n)
            return String(dec.toByteArray(), UTF_8)
        }

        /**
         * Find private key from a public key with a brute approach
         *
         * @param pubKey public key
         * @return private key if found, null otherwise
         */
        @JvmStatic
        fun findPrivateKey(pubKey: RsaPublicKey) : RsaPrivateKey? {
            // the goal is to find d s.t. x^d mod n = u
            // or p and q s.t. p*q = n (then d is derived from p and q)
            // use the second
            val set = factorize(pubKey.n)
            if (set.isNotEmpty()) {
                val p = set.first()
                val q = pubKey.n / p
                return generateKeys(p, q, pubKey.e).first
            }
            return null
        }
    }

    /**
     * Encode a string with the public key.
     * Text size must be < modulus of the key.
     * Base64 is necessary otherwise byte[] -> BigInteger conversion gives weird results
     *
     * @param s = string to encode (plaintext)
     * @return encoded string in base 64
     */
    @Throws(MessageTooLongException::class)
    fun encryptText(s: String) : String {
        val bytes: ByteArray = s.toByteArray(UTF_8)
        val u = BigInteger(bytes) // number representation
        if (u.bitLength() > publicKey.n.bitLength()) throw MessageTooLongException()
        val enc = u.modPow(publicKey.e, publicKey.n)
        return Base64.getEncoder().encodeToString(enc.toByteArray())
    }

    /**
     * Decode an encrypted text provided that the private key has been set correctly
     * Input encrypted text must be in base64
     *
     * @param s encoded text in base64
     * @return decoded text as plaintext
     */
    @Throws(UninitializedPrivateKeyException::class)
    fun decryptText(s: String) : String {
        if (privateKey == null) throw UninitializedPrivateKeyException()
        val bytes: ByteArray = Base64.getDecoder().decode(s)
        val x = BigInteger(bytes) // number representation
        val dec = x.modPow(privateKey!!.d, privateKey!!.n)
        return String(dec.toByteArray(), UTF_8)
    }
}