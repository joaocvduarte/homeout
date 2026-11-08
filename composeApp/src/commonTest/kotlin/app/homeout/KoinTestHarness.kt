package app.homeout

import app.homeout.data.InMemoryKeyValueStore
import app.homeout.data.KeyValueStore
import app.homeout.di.initKoin
import app.homeout.domain.AccountRepository
import app.homeout.domain.PasswordHasher
import kotlinx.datetime.TimeZone
import org.koin.core.context.stopKoin
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.mp.KoinPlatform
import kotlin.time.Clock

/** Not secure: "hashes" by prefixing. Web can't hash, and 600,000 real rounds would slow every test. */
object TestPasswordHasher : PasswordHasher {
    override suspend fun hash(password: String) = "hashed:$password"
    override suspend fun verify(password: String, stored: String) = stored == "hashed:$password"
}

/** The email of the person [withKoin] signs in by default. */
const val SIGNED_IN_EMAIL = "maya@example.com"

/**
 * Runs [block] with the app's Koin graph, the clock and time zone replaced, plus any
 * [overrides]. Call it inside `runComposeUiTest` or `runTest`: on Web those run
 * asynchronously, so Koin must start and stop within the test body.
 *
 * The accounts live in memory with a fast hasher. With [signedIn], someone is already signed in, so the
 * app opens on Home; pass `false` to see Log in.
 */
suspend fun withKoin(
    clock: Clock,
    timeZone: TimeZone = TimeZone.UTC,
    overrides: List<Module> = emptyList(),
    signedIn: Boolean = true,
    block: suspend () -> Unit,
) {
    val fixed = module {
        single<Clock> { clock }
        single<TimeZone> { timeZone }
        single<PasswordHasher> { TestPasswordHasher }
    }
    initKoin(module { single<KeyValueStore> { InMemoryKeyValueStore() } }, listOf(fixed) + overrides)
    try {
        if (signedIn) KoinPlatform.getKoin().get<AccountRepository>().signIn(SIGNED_IN_EMAIL)
        block()
    } finally {
        stopKoin()
    }
}
