package app.homeout.event

import app.homeout.data.SampleEventRepository
import app.homeout.domain.EventDetail
import app.homeout.domain.GetEvent
import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

/** Friday 13 November 2026, 19:00 UTC: the sample events fall tonight and tomorrow. */
internal val friday = object : Clock {
    override fun now() = Instant.parse("2026-11-13T19:00:00Z")
}

internal suspend fun sampleDetail(id: String): EventDetail =
    GetEvent(SampleEventRepository(friday, TimeZone.UTC), friday, TimeZone.UTC)(id)!!

