package app.homeout

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.homeout.data.InMemoryKeyValueStore
import app.homeout.data.KeyValueStore
import app.homeout.di.initKoin
import app.homeout.website.Website
import kotlinx.browser.document
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    // Web has no accounts, so its store stays empty and is never read.
    initKoin(module { single<KeyValueStore> { InMemoryKeyValueStore() } })
    ComposeViewport(document.body!!) {
        Website(koinViewModel(), koinInject())
    }
}
