package app.homeout.android

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import app.homeout.data.SharedPreferencesStore

/**
 * Wipes the accounts and the session from the phone's SharedPreferences. It goes through the same preferences
 * object the app reads, with `commit()`, so the running process sees the change at once.
 */
internal fun clearAccounts() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    context.getSharedPreferences(SharedPreferencesStore.FILE, Context.MODE_PRIVATE).edit().clear().commit()
}
