package app.homeout.domain

/** Turns a password into a salted hash that can be stored, and checks a password against one. */
interface PasswordHasher {
    suspend fun hash(password: String): String

    suspend fun verify(password: String, stored: String): Boolean
}
