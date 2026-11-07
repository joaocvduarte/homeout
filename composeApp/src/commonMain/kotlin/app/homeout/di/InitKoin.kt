package app.homeout.di

import app.homeout.data.dataModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

val appModules: List<Module> = listOf(dataModule, domainModule, presentationModule)

/** Starts Koin once per process. [overrides] load last, so they replace any app binding. */
fun initKoin(overrides: List<Module> = emptyList()) {
    startKoin { modules(appModules + overrides) }
}
