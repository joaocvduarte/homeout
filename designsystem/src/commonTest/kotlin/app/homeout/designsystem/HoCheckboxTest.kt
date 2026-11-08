package app.homeout.designsystem

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HoCheckboxTest {

    @Test
    fun tappingTheLabelTogglesTheBoxBothWays() = runComposeUiTest {
        var checked by mutableStateOf(false)
        setContent {
            HoTheme(darkTheme = false) {
                HoCheckbox(checked, { checked = it }, "I agree to the homeout Terms of Service and Privacy Policy.")
            }
        }
        val row = onNodeWithText("I agree to the homeout Terms of Service and Privacy Policy.")
        row.assertIsOff()
        row.performClick()
        row.assertIsOn()
        row.performClick()
        row.assertIsOff()
    }
}
