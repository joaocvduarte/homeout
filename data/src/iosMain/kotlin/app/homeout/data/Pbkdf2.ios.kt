package app.homeout.data

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.refTo
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

@OptIn(ExperimentalForeignApi::class)
internal actual fun pbkdf2Sha256(password: String, salt: ByteArray, iterations: Int, keyLength: Int): ByteArray {
    val passwordBytes = password.encodeToByteArray()
    val saltBytes = salt.asUByteArray()
    val derived = UByteArray(keyLength)
    val status = passwordBytes.usePinned { pw ->
        saltBytes.usePinned { s ->
            derived.usePinned { out ->
                CCKeyDerivationPBKDF(
                    kCCPBKDF2,
                    pw.addressOf(0),
                    passwordBytes.size.convert(),
                    s.addressOf(0),
                    saltBytes.size.convert(),
                    kCCPRFHmacAlgSHA256,
                    iterations.convert(),
                    out.addressOf(0),
                    keyLength.convert(),
                )
            }
        }
    }
    check(status == 0) { "PBKDF2 failed with status $status." }
    return derived.asByteArray()
}

@OptIn(ExperimentalForeignApi::class)
internal actual fun secureRandomBytes(size: Int): ByteArray {
    val bytes = ByteArray(size)
    val status = SecRandomCopyBytes(kSecRandomDefault, size.convert(), bytes.refTo(0))
    check(status == 0) { "No secure random bytes: status $status." }
    return bytes
}
