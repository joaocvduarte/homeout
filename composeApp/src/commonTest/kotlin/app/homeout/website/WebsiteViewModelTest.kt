package app.homeout.website

import app.homeout.data.SampleEventRepository
import app.homeout.domain.EventRepository
import app.homeout.domain.GetUpcomingEvents
import app.homeout.event.friday
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

@OptIn(ExperimentalCoroutinesApi::class)
class WebsiteViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private fun viewModel(repository: EventRepository = SampleEventRepository(friday, TimeZone.UTC)) =
        WebsiteViewModel(GetUpcomingEvents(repository, friday, TimeZone.UTC))

    @Test
    fun loadsTheNextEventsInTimeOrderWithTheirCount() = runTest {
        val state = viewModel().state.value

        assertEquals(
            listOf("fado-old-cistern", "rooftop-vinyl", "night-market-river", "petiscos-tasting"),
            state.events.map { it.id },
        )
        assertEquals(listOf(true, true, false, false), state.events.map { it.isToday })
        assertEquals(4, state.total)
    }

    @Test
    fun keepsOnlyTheFirstFourButCountsThemAll() = runTest {
        val sample = SampleEventRepository(friday, TimeZone.UTC).events()
        val six = sample + sample.take(2).map { it.copy(id = it.id + "-again") }
        val state = viewModel(object : EventRepository { override suspend fun events() = six }).state.value

        assertEquals(4, state.events.size)
        assertEquals(6, state.total)
    }

    @Test
    fun noUpcomingEventsGivesAnEmptyState() = runTest {
        val state = viewModel(object : EventRepository { override suspend fun events() = emptyList<app.homeout.domain.Event>() }).state.value

        assertEquals(emptyList(), state.events)
        assertEquals(0, state.total)
    }
}
