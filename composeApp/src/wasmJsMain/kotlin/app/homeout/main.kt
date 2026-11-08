package app.homeout

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import androidx.navigation.ExperimentalBrowserHistoryApi
import androidx.navigation.bindToBrowserNavigation
import app.homeout.data.InMemoryKeyValueStore
import app.homeout.data.KeyValueStore
import app.homeout.di.initKoin
import kotlinx.browser.document
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class, ExperimentalBrowserHistoryApi::class)
fun main() {
    // Web has no accounts, so its store stays empty and is never read.
    initKoin(module { single<KeyValueStore> { InMemoryKeyValueStore() } })
    ComposeViewport(document.body!!) {
        App(onNavHostReady = { it.bindToBrowserNavigation() })
    }
}
