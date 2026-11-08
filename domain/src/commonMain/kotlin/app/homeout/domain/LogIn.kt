package app.homeout.domain

enum class LogInResult { SignedIn, Mismatch }

/** Signs in when the email and password match an account on the phone. */
class LogIn(
    private val accounts: AccountRepository,
    private val hasher: PasswordHasher,
) {
    suspend operator fun invoke(email: String, password: String): LogInResult {
        val normalized = Credentials.normalizeEmail(email)
        val stored = accounts.passwordHash(normalized) ?: return LogInResult.Mismatch
        if (!hasher.verify(password, stored)) return LogInResult.Mismatch
        accounts.signIn(normalized)
        return LogInResult.SignedIn
    }
}
