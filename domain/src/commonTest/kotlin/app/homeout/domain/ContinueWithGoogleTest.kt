package app.homeout.domain

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContinueWithGoogleTest {
    @Test
    fun startsASessionForThePlaceholderWithoutStoringAnAccount() = runTest {
        val repo = FakeAccounts()
        ContinueWithGoogle(repo)()
        assertEquals("google@homeout.local", GetSession(repo)())
        assertTrue(repo.accounts.isEmpty())
    }
}
