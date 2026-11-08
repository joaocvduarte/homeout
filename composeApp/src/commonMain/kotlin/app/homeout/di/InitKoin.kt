package app.homeout.di

import app.homeout.data.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

val appModules: List<Module> = listOf(dataModule, domainModule, presentationModule)

/**
 * Starts Koin once per process. [platform] supplies what only the platform has, the `KeyValueStore`
 * behind the accounts. [overrides] load last, so they replace any app binding.
 */
fun initKoin(platform: Module, overrides: List<Module> = emptyList()) {
    startKoin { modules(appModules + platform + overrides) }
}
