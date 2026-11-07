package app.homeout

import app.homeout.di.initKoin
import kotlinx.datetime.TimeZone
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import kotlin.time.Clock

/**
 * Runs [block] with the app's Koin graph, the clock and time zone replaced, plus any
 * [overrides]. Call it inside `runComposeUiTest` or `runTest`: on Web those run
 * asynchronously, so Koin must start and stop within the test body.
 */
suspend fun withKoin(
    clock: Clock,
    timeZone: TimeZone = TimeZone.UTC,
    overrides: List<Module> = emptyList(),
    block: suspend () -> Unit,
) {
    val fixed = module {
        single<Clock> { clock }
        single<TimeZone> { timeZone }
    }
    initKoin(listOf(fixed) + overrides)
    try {
        block()
    } finally {
        stopKoin()
    }
}
