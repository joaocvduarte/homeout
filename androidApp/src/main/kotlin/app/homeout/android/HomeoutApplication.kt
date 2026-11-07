package app.homeout.android

import android.app.Application
import app.homeout.di.initKoin

/** Starts Koin once per process, so activity recreation keeps the same graph. */
class HomeoutApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
