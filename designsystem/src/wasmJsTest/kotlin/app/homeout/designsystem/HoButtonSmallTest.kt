package app.homeout.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HoButtonSmallTest {

    @Test
    fun smallButtonIs32HighOnWeb() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                HoButton("Small", {}, Modifier.testTag("small"), size = HoButtonSize.Small)
            }
        }
        onNodeWithTag("small").assertHeightIsEqualTo(32.dp)
    }
}
