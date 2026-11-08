package app.homeout

import androidx.compose.ui.window.ComposeUIViewController
import app.homeout.data.KeyValueStore
import app.homeout.data.UserDefaultsStore
import app.homeout.di.initKoin
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    if (KoinPlatform.getKoinOrNull() == null) initKoin(module { single<KeyValueStore> { UserDefaultsStore() } })
    return ComposeUIViewController { App() }
}
