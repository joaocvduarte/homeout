package app.homeout.data

import kotlin.test.Test
import kotlin.test.assertEquals

/** The SharedPreferences file name is part of the contract: RecreationTest clears it by name. */
class SharedPreferencesStoreCompileTest {
    @Test
    fun theFileIsNamedHomeoutAccounts() = assertEquals("homeout.accounts", SharedPreferencesStore.FILE)
}
