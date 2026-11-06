package app.homeout.home

import app.homeout.data.SampleEventRepository
import app.homeout.domain.Category
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.GetUpcomingEvents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeStateHolderTest {

    private fun TestScope.holder(): HomeStateHolder {
        val clock = object : Clock {
            override fun now() = Instant.parse("2026-11-13T19:00:00Z")
        }
        val repository = SampleEventRepository(clock, TimeZone.UTC)
        return HomeStateHolder(
            GetUpcomingEvents(repository, clock, TimeZone.UTC),
            CountEventsThisWeek(repository, clock, TimeZone.UTC),
            CoroutineScope(backgroundScope.coroutineContext + UnconfinedTestDispatcher(testScheduler)),
        )
    }

    private fun HomeStateHolder.titles() = state.value.groups.flatMap { it.events }.map { it.title }

    @Test
    fun startsWithEveryEventAndTheWeeklyCount() = runTest {
        val holder = holder()

        assertNull(holder.state.value.category)
        assertEquals(4, holder.state.value.eventsThisWeek)
        assertEquals(
            listOf("Fado at the Old Cistern", "Rooftop Vinyl Session", "Night Market by the River", "Petiscos Tasting Night"),
            holder.titles(),
        )
    }

    @Test
    fun categorySelectionFilters() = runTest {
        val holder = holder()
        holder.onCategorySelect(Category.FOOD)
        assertEquals(Category.FOOD, holder.state.value.category)
        assertEquals(listOf("Night Market by the River", "Petiscos Tasting Night"), holder.titles())

        holder.onCategorySelect(null)
        assertEquals(4, holder.titles().size)
    }

    @Test
    fun queryFiltersByArtist() = runTest {
        val holder = holder()
        holder.onQueryChange("Marta Rios")
        assertEquals(listOf("Rooftop Vinyl Session"), holder.titles())
    }

    @Test
    fun queryCombinesWithCategory() = runTest {
        val holder = holder()
        holder.onCategorySelect(Category.FOOD)
        holder.onQueryChange("fado")
        assertEquals(emptyList(), holder.titles())

        holder.onQueryChange("   ")
        assertEquals(listOf("Night Market by the River", "Petiscos Tasting Night"), holder.titles())
    }

    @Test
    fun weeklyCountIgnoresFilters() = runTest {
        val holder = holder()
        holder.onCategorySelect(Category.MUSIC)
        holder.onQueryChange("fado")
        assertEquals(4, holder.state.value.eventsThisWeek)
    }
}
