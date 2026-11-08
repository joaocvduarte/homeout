package app.homeout.home

import app.homeout.data.SampleEventRepository
import app.homeout.domain.Category
import app.homeout.domain.CountEventsThisWeek
import app.homeout.domain.GetUpcomingEvents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.datetime.TimeZone
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private fun holder(): HomeViewModel {
        val clock = object : Clock {
            override fun now() = Instant.parse("2026-11-13T19:00:00Z")
        }
        val repository = SampleEventRepository(clock, TimeZone.UTC)
        return HomeViewModel(
            GetUpcomingEvents(repository, clock, TimeZone.UTC),
            CountEventsThisWeek(repository, clock, TimeZone.UTC),
        )
    }

    private fun HomeViewModel.titles() = state.value.groups.flatMap { it.events }.map { it.title }

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
