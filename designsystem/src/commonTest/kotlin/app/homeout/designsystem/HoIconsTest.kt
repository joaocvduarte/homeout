package app.homeout.designsystem

import androidx.compose.foundation.layout.Row
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test

@OptIn(ExperimentalTestApi::class)
class HoIconsTest {

    @Test
    fun theStoreLogosLoadAsIcons() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) {
                Row {
                    HoIconButton(HoIcons.AppleLogoFill, "Apple logo", onClick = {})
                    HoIconButton(HoIcons.GooglePlayLogoFill, "Google Play logo", onClick = {})
                }
            }
        }
        waitForIdle()
        onNodeWithContentDescription("Apple logo").assertExists()
        onNodeWithContentDescription("Google Play logo").assertExists()
    }
}
