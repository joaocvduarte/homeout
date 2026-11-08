package app.homeout.android

import android.app.Application
import app.homeout.data.KeyValueStore
import app.homeout.data.SharedPreferencesStore
import app.homeout.di.initKoin
import org.koin.dsl.module

/** Starts Koin once per process, so activity recreation keeps the same graph. Accounts live in SharedPreferences. */
class HomeoutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(module { single<KeyValueStore> { SharedPreferencesStore(applicationContext) } })
    }
}
