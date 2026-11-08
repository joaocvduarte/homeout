package app.homeout.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HoTextTest {

    private val uppercaseRoles = HoTypeRole.entries.filter {
        it.name.startsWith("Display") || it.name.startsWith("Headline") || it == HoTypeRole.LabelSmall
    }

    @Test
    fun displayHeadlineAndLabelSmallRenderInUppercase() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    uppercaseRoles.forEach { HoText("Upcoming near you", it, Modifier.testTag(it.name)) }
                }
            }
        }
        uppercaseRoles.forEach { onNodeWithTag(it.name).assertTextEquals("UPCOMING NEAR YOU") }
    }

    @Test
    fun otherRolesKeepTheirCase() = runComposeUiTest {
        val roles = HoTypeRole.entries - uppercaseRoles.toSet()
        setContent {
            HoTheme(darkTheme = true) {
                Column {
                    roles.forEach { HoText("Fado at the Old Cistern", it, Modifier.testTag(it.name)) }
                }
            }
        }
        roles.forEach { onNodeWithTag(it.name).assertTextEquals("Fado at the Old Cistern") }
    }
}
