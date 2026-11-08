package app.homeout.domain

/** A person's account, kept on their phone. [email] is normalized: trimmed and lowercase. */
data class Account(val name: String, val email: String)
