package app.homeout.data

/** Small text values kept on the device. Each platform supplies its own. Reads are immediate. */
interface KeyValueStore {
    fun getString(key: String): String?

    fun putString(key: String, value: String)
}
