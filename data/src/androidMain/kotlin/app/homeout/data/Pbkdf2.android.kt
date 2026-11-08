package app.homeout.data

import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

internal actual fun pbkdf2Sha256(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
    val spec = PBEKeySpec(password.toCharArray(), salt, iterations, keyLength * 8)
    try {
        // PBKDF2WithHmacSHA256 exists from Android 8 (API 26), the app's minimum.
        return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).encoded
    } finally {
        spec.clearPassword()
    }
}

internal actual fun secureRandomBytes(size: Int): ByteArray = ByteArray(size).also { SecureRandom().nextBytes(it) }
