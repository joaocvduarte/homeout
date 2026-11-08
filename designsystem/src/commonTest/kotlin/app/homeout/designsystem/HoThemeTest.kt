package app.homeout.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.v2.runComposeUiTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

@OptIn(ExperimentalTestApi::class)
class HoThemeTest {

    @Test
    fun darkAndLightSelectTheirTokenSets() = runComposeUiTest {
        var dark: HoColors? = null
        var light: HoColors? = null
        setContent {
            HoTheme(darkTheme = true) { dark = HoTheme.colors }
            HoTheme(darkTheme = false) { light = HoTheme.colors }
        }
        waitForIdle()
        assertSame(HoColors.Dark, dark)
        assertSame(HoColors.Light, light)
    }

    @Test
    fun defaultFollowsTheSystemSetting() = runComposeUiTest {
        var systemDark: Boolean? = null
        var colors: HoColors? = null
        setContent {
            systemDark = isSystemInDarkTheme()
            HoTheme { colors = HoTheme.colors }
        }
        waitForIdle()
        assertEquals(if (systemDark == true) HoColors.Dark else HoColors.Light, colors)
    }
}
