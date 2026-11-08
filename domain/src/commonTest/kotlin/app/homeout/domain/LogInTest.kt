package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LogInTest {
    private val repo = FakeAccounts()
    private val logIn = LogIn(repo, PrefixHasher)

    private suspend fun account() {
        CreateAccount(repo, PrefixHasher)("Ana", "ana@x.pt", "12345678", true)
        repo.signedIn = null
    }

    @Test
    fun theRightEmailAndPasswordSignIn() = runTest {
        account()
        assertEquals(LogInResult.SignedIn, logIn(" ANA@x.pt", "12345678"))
        assertEquals("ana@x.pt", repo.session())
    }

    @Test
    fun aWrongPasswordIsAMismatch() = runTest {
        account()
        assertEquals(LogInResult.Mismatch, logIn("ana@x.pt", "00000000"))
        assertNull(repo.session())
    }

    @Test
    fun anUnknownEmailIsAMismatch() = runTest {
        assertEquals(LogInResult.Mismatch, logIn("nobody@x.pt", "12345678"))
        assertNull(repo.session())
    }
}
