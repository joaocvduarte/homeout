package app.homeout.data

/**
 * PBKDF2-HMAC-SHA256 from the platform's own crypto: `javax.crypto` on Android, CommonCrypto on iOS.
 * Web has no accounts, so its actual throws. [keyLength] is in bytes.
 */
internal expect fun pbkdf2Sha256(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray

/** [size] bytes from the platform's secure random source. */
internal expect fun secureRandomBytes(size: Int): ByteArray
