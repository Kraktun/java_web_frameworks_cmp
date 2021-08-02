package it.unipd.stage.sl.lib.rsa

import java.math.BigInteger

/*
The following functions come from
https://en.wikipedia.org/wiki/RSA_(cryptosystem)#Key_generation
 */

/**
 * @return true is a big integer is probably prime with certainty 15
 */
fun BigInteger.isPrime(): Boolean {
    // not 100% correct, but error is very low and for this type of application it's good enough
    return this.isProbablePrime(15)
}

/**
 * @param n1 a non null positive big integer
 * @param n2 a non null positive big integer
 * @return true if n1 and n2 are coprime
 */
fun areCoprime(n1: BigInteger, n2: BigInteger): Boolean {
    return gcd(n1, n2) == BigInteger.ONE
}

/**
 * from https://www.geeksforgeeks.org/euclidean-algorithms-basic-and-extended/
 * @param a a non null positive big integer
 * @param b a non null positive big integer
 * @return greatest common divisor between a and b
 */
fun gcd(a: BigInteger, b: BigInteger): BigInteger {
    return if (a == BigInteger.ZERO) b else gcd(b % a, a)
}

/**
 * @param n1 a non null positive big integer
 * @param n2 a non null positive big integer
 * @return least common multiple between n1 and n2
 */
fun lcm(n1: BigInteger, n2: BigInteger): BigInteger {
    return n1.multiply(n2).divide(gcd(n1, n2))
}

/**
 * Returns modulo inverse of 'a' with respect to 'm' using extended Euclid
 * Algorithm Assumption: 'a' and 'm' are coprimes
 * from https://www.geeksforgeeks.org/multiplicative-inverse-under-modulo-m/
 *
 * @param a non null positive big integer
 * @param m non null positive big integer
 */
fun modInverse(a: BigInteger, m: BigInteger): BigInteger {
    // note: mapping to rsa: a = e, m= y, result = d
    var a1 = a
    var m1 = m
    val m0 = m1
    var y = BigInteger.ZERO
    var x = BigInteger.ONE
    if (m1 == BigInteger.ONE) return BigInteger.ZERO
    while (a1 > BigInteger.ONE) {
        // q is the quotient
        val q = a1 / m1
        var t = m1
        // m is the remainder now, process same as Euclid's algo
        m1 = a1 % m1
        a1 = t
        t = y
        // Update x and y
        y = x - q * y
        x = t
    }
    // Make x positive
    if (x < BigInteger.ZERO) x += m0
    return x
}

/**
 * Naive approach to factorize a number.
 * Note that this does not always return all factors, but a set such that
 * each element of the set may be a factor multiple times
 * and/or the last factor is obtained from the set as n/prod(set)
 *
 * @param n non null positive big integer
 * @return set of factors
 */
fun factorize(n: BigInteger): Set<BigInteger> {
    val set = mutableSetOf<BigInteger>()
    // first test 2 and 3
    if (n % BigInteger("2") == BigInteger.ZERO) set.add(BigInteger("2")) // for some reason BigInteger.TWO does not work with shadowjar
    if (n % BigInteger("3") == BigInteger.ZERO) set.add(BigInteger("3"))
    // use 6k+1 rule
    var i = BigInteger("5")
    while (i*i <= n) {
        if (n%i == BigInteger.ZERO) {
            set.add(i)
        }
        if (n%(i+ BigInteger("2")) == BigInteger.ZERO) {
            set.add(i+ BigInteger("2"))
        }
        i += BigInteger("6")
    }
    return set
}

/**
 * Return a list of all the prime factors of n
 *
 * @param n non null positive big integer
 * @return list of prime factors (may be repated)
 */
fun factorizeFull(n: BigInteger): List<BigInteger> {
    val list = mutableListOf<BigInteger>()
    val set = factorize(n)
    list.addAll(set)
    val prod = set.reduce { acc, bigInteger -> acc*bigInteger }
    var residual = n / prod
    while (residual > BigInteger.ONE) {
        set.forEach{
            while (residual % it == BigInteger.ZERO) {
                list.add(it)
                residual /= it
            }
        }
        if (residual.isPrime()) {
            list.add(residual)
            break
        }
    }
    return list
}