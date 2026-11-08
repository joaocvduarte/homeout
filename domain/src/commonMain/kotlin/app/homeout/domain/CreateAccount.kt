package app.homeout.domain

sealed interface CreateAccountResult {
    data class Created(val account: Account) : CreateAccountResult
    data object EmailTaken : CreateAccountResult
}

/** Creates an account and signs it in. The email must be free; nothing is stored otherwise. */
class CreateAccount(
    private val accounts: AccountRepository,
    private val hasher: PasswordHasher,
) {
    suspend operator fun invoke(name: String, email: String, password: String, acceptedTerms: Boolean): CreateAccountResult {
        require(name.isNotBlank()) { "A name is needed." }
        require(Credentials.isValidEmail(email)) { "Not a valid email: $email." }
        require(Credentials.isValidPassword(password)) { "A password needs at least ${Credentials.MIN_PASSWORD_LENGTH} characters." }
        require(acceptedTerms) { "The terms must be accepted." }
        val normalized = Credentials.normalizeEmail(email)
        if (accounts.find(normalized) != null) return CreateAccountResult.EmailTaken
        val account = Account(name.trim(), normalized)
        accounts.add(account, hasher.hash(password))
        accounts.signIn(normalized)
        return CreateAccountResult.Created(account)
    }
}
