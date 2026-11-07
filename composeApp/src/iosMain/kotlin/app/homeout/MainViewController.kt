package app.homeout

import androidx.compose.ui.window.ComposeUIViewController
import app.homeout.di.initKoin
import org.koin.mp.KoinPlatform
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    if (KoinPlatform.getKoinOrNull() == null) initKoin()
    return ComposeUIViewController { App() }
}
