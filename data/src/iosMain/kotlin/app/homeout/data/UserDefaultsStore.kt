package app.homeout.data

import platform.Foundation.NSUserDefaults

/** Accounts and the session in the app's NSUserDefaults. */
class UserDefaultsStore(private val defaults: NSUserDefaults = NSUserDefaults.standardUserDefaults) : KeyValueStore {
    override fun getString(key: String): String? = defaults.stringForKey(key)

    override fun putString(key: String, value: String) {
        defaults.setObject(value, forKey = key)
    }
}
