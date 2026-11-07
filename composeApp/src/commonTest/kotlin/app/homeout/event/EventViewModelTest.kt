package app.homeout.event

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import app.homeout.EventDestination
import app.homeout.data.SampleEventRepository
import app.homeout.domain.GetEvent
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
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class EventViewModelTest {

    @BeforeTest
    fun mainDispatcher() = Dispatchers.setMain(UnconfinedTestDispatcher())

    @AfterTest
    fun resetMainDispatcher() = Dispatchers.resetMain()

    private val getEvent = GetEvent(SampleEventRepository(friday, TimeZone.UTC), friday, TimeZone.UTC)

    /** Navigation stores a route's arguments under their property names. */
    private fun handleFor(id: String) = SavedStateHandle(mapOf("id" to id))

    @Test
    fun routeIdIsReadBack() {
        assertEquals("petiscos-tasting", handleFor("petiscos-tasting").toRoute<EventDestination>().id)
    }

    @Test
    fun findsTheEventItsRouteNames() = runTest {
        val viewModel = EventViewModel(handleFor("fado-old-cistern"), getEvent)
        val state = viewModel.state.value
        assertTrue(state is EventState.Found, "state = $state")
        assertEquals("Fado at the Old Cistern", state.detail.event.title)
        assertTrue(state.detail.isToday)
    }

    @Test
    fun unknownIdIsMissing() = runTest {
        assertEquals(EventState.Missing, EventViewModel(handleFor("unknown"), getEvent).state.value)
    }
}
