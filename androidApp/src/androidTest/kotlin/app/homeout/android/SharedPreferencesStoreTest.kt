package app.homeout.android

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.homeout.data.SharedPreferencesStore
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesStoreTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun clearFirst() = clearAccounts()

    @After
    fun clear() {
        context.getSharedPreferences(SharedPreferencesStore.FILE, android.content.Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    fun aValueWrittenByOneInstanceIsReadByANewOne() {
        assertNull(SharedPreferencesStore(context).getString("session.email"))
        SharedPreferencesStore(context).putString("session.email", "ana@x.pt")
        assertEquals("ana@x.pt", SharedPreferencesStore(context).getString("session.email"))
    }
}
