package app.homeout.domain

internal class FakeAccounts : AccountRepository {
    val accounts = mutableMapOf<String, Pair<Account, String>>()
    var signedIn: String? = null

    override suspend fun find(email: String) = accounts[email]?.first
    override suspend fun add(account: Account, passwordHash: String) {
        accounts[account.email] = account to passwordHash
    }
    override suspend fun passwordHash(email: String) = accounts[email]?.second
    override fun session() = signedIn
    override suspend fun signIn(email: String) {
        signedIn = email
    }
}

/** Not secure: "hashes" by prefixing, so tests can read what was stored. */
internal object PrefixHasher : PasswordHasher {
    override suspend fun hash(password: String) = "hashed:$password"
    override suspend fun verify(password: String, stored: String) = stored == "hashed:$password"
}
