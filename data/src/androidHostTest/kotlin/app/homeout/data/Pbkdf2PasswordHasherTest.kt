package app.homeout.data

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/** Runs on the JVM, where `javax.crypto` exists. The Wasm actual throws, and iOS is built on macOS. */
class Pbkdf2PasswordHasherTest {
    private val hasher = Pbkdf2PasswordHasher(iterations = 10)

    @Test
    fun twoHashesOfOnePasswordDifferAndBothVerify() = runTest {
        val first = hasher.hash("12345678")
        val second = hasher.hash("12345678")
        assertNotEquals(first, second)
        assertTrue(hasher.verify("12345678", first))
        assertTrue(hasher.verify("12345678", second))
    }

    @Test
    fun aWrongPasswordDoesNotVerify() = runTest {
        assertFalse(hasher.verify("87654321", hasher.hash("12345678")))
    }

    @Test
    fun theStoredValueNeverHoldsThePassword() = runTest {
        val stored = hasher.hash("correct horse battery")
        assertFalse(stored.contains("correct"))
        assertEquals(3, stored.split(':').size)
        assertEquals("10", stored.substringBefore(':'))
    }

    @Test
    fun aHashKeepsItsOwnIterationCountWhenTheDefaultChanges() = runTest {
        val stored = Pbkdf2PasswordHasher(iterations = 5).hash("12345678")
        assertTrue(hasher.verify("12345678", stored))
    }

    @Test
    fun aDamagedStoredValueDoesNotVerify() = runTest {
        assertFalse(hasher.verify("12345678", "not-a-hash"))
        assertFalse(hasher.verify("12345678", "10:@@@:@@@"))
        assertFalse(hasher.verify("12345678", "0:AAAA:AAAA"))
    }

    @Test
    fun matchesTheRfc7914Vector() = runTest {
        // PBKDF2-HMAC-SHA256, P="passwd", S="salt", c=1, dkLen=64 (RFC 7914 section 11), first 32 bytes.
        val key = pbkdf2Sha256("passwd", "salt".encodeToByteArray(), 1, 32)
        assertEquals("55ac046e56e3089fec1691c22544b605f94185216dde0465e68b9d57c20dacbc", key.joinToString("") { (it.toInt() and 0xff).toString(16).padStart(2, '0') })
    }
}
