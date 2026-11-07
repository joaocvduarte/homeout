package app.homeout.android

import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * The real activity is rebuilt, as on rotation, a language change or a system theme change,
 * and the ViewModels keep Home's filter and the open event.
 */
@RunWith(AndroidJUnit4::class)
class RecreationTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun filterFoodAndSearchPetiscos() {
        rule.onNodeWithText("Food").performClick()
        rule.onNodeWithTag("search").performTextInput("Petiscos")
        rule.waitForIdle()
    }

    private fun assertHomeKeptItsState() {
        rule.onNodeWithText("UPCOMING NEAR YOU").assertExists()
        rule.onNodeWithText("Food").assertIsSelected()
        rule.onNodeWithTag("search").assertTextEquals("Petiscos")
        assertEquals(1, rule.onAllNodes(hasTestTag("event-petiscos-tasting")).fetchSemanticsNodes().size)
        assertEquals(0, rule.onAllNodes(hasTestTag("event-night-market-river")).fetchSemanticsNodes().size)
    }

    @Test
    fun homeKeepsItsFilterAndSearchWhenTheActivityIsRecreated() {
        filterFoodAndSearchPetiscos()

        rule.activityRule.scenario.recreate()
        rule.waitForIdle()

        assertHomeKeptItsState()
    }

    @Test
    fun eventPageStaysOpenAndBackKeepsHomeWhenTheActivityIsRecreated() {
        filterFoodAndSearchPetiscos()
        rule.onNodeWithTag("event-petiscos-tasting").performClick()
        rule.waitForIdle()

        rule.activityRule.scenario.recreate()
        rule.waitForIdle()

        rule.onNodeWithText("PETISCOS TASTING NIGHT").assertExists()
        assertEquals(0, rule.onAllNodes(hasText("UPCOMING NEAR YOU")).fetchSemanticsNodes().size)

        rule.onNodeWithContentDescription("Back to events").performClick()
        rule.waitForIdle()
        assertHomeKeptItsState()
    }

    @Test
    fun buyTicketsAndQrKeepTheirStateWhenTheActivityIsRecreated() {
        rule.onNodeWithTag("event-fado-old-cistern").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Buy tickets").performClick()
        rule.waitForIdle()
        rule.onNodeWithContentDescription("Add a ticket").performClick()
        rule.onNodeWithContentDescription("Add a ticket").performClick()
        rule.onNodeWithTag("method-GOOGLE_WALLET").performClick()
        rule.waitForIdle()

        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        rule.onNodeWithText("Pay €54").assertExists()
        rule.onNodeWithTag("method-GOOGLE_WALLET").assertIsSelected()

        rule.onNodeWithText("Pay €54").performClick()
        rule.waitForIdle()
        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        rule.onNodeWithTag("tab-Tickets").assertIsSelected()
        rule.onNodeWithText("Payment confirmed. 3 tickets for Fado at the Old Cistern added.", useUnmergedTree = true).assertExists()
        rule.onNodeWithText("Ticket 3 of 3").assertExists()

        rule.onNodeWithTag("ticket-HO-FADO-002").performClick()
        rule.waitForIdle()
        rule.activityRule.scenario.recreate()
        rule.waitForIdle()
        rule.onNodeWithTag("qr-position").assertTextEquals("Fado at the Old Cistern · Ticket 2 of 3")
    }
}
