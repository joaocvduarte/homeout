package app.homeout.data

import android.content.Context

/** Accounts and the session in the app's private SharedPreferences. */
class SharedPreferencesStore(context: Context) : KeyValueStore {
    private val preferences = context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    override fun getString(key: String): String? = preferences.getString(key, null)

    override fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    companion object {
        const val FILE = "homeout.accounts"
    }
}
