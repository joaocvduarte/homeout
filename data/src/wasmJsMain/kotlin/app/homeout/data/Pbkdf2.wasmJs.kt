package app.homeout.data

internal actual fun pbkdf2Sha256(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray =
    throw UnsupportedOperationException("Web has no accounts, so it never hashes a password.")

internal actual fun secureRandomBytes(size: Int): ByteArray =
    throw UnsupportedOperationException("Web has no accounts, so it never hashes a password.")
