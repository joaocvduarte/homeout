package app.homeout.data

import app.homeout.domain.PasswordHasher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64

/**
 * Salted PBKDF2-HMAC-SHA256, stored as `<iterations>:<salt>:<hash>` in base64, so the cost can change later.
 * The work runs off the main thread: 600,000 rounds would otherwise freeze the screen.
 */
class Pbkdf2PasswordHasher(private val iterations: Int = DEFAULT_ITERATIONS) : PasswordHasher {

    override suspend fun hash(password: String): String = withContext(Dispatchers.Default) {
        val salt = secureRandomBytes(SALT_BYTES)
        val key = pbkdf2Sha256(password, salt, iterations, KEY_BYTES)
        "$iterations:${Base64.encode(salt)}:${Base64.encode(key)}"
    }

    override suspend fun verify(password: String, stored: String): Boolean = withContext(Dispatchers.Default) {
        val parts = stored.split(':')
        val rounds = parts.getOrNull(0)?.toIntOrNull()
        if (parts.size != 3 || rounds == null || rounds < 1) return@withContext false
        val salt = runCatching { Base64.decode(parts[1]) }.getOrNull() ?: return@withContext false
        val expected = runCatching { Base64.decode(parts[2]) }.getOrNull() ?: return@withContext false
        constantTimeEquals(pbkdf2Sha256(password, salt, rounds, expected.size), expected)
    }

    companion object {
        const val DEFAULT_ITERATIONS = 600_000
        private const val SALT_BYTES = 16
        private const val KEY_BYTES = 32
    }
}

/** Compares without stopping at the first difference, so timing doesn't reveal how much matched. */
internal fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
    var difference = a.size xor b.size
    for (i in 0 until minOf(a.size, b.size)) difference = difference or (a[i].toInt() xor b[i].toInt())
    return difference == 0
}
