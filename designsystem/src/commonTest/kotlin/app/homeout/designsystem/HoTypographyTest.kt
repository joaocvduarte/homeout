package app.homeout.designsystem

import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalTestApi::class)
class HoTypographyTest {

    /** The type scale exactly as the homeout UI Library page lists it: size/line height · weight, letter-spacing. */
    private val design = mapOf(
        HoTypeRole.DisplayLarge to "Stencil 57/64 · 800 · 0.01 · upper",
        HoTypeRole.DisplayMedium to "Stencil 45/52 · 800 · 0.01 · upper",
        HoTypeRole.DisplaySmall to "Stencil 36/44 · 800 · 0.01 · upper",
        HoTypeRole.HeadlineLarge to "Stencil 32/40 · 800 · 0.01 · upper",
        HoTypeRole.HeadlineMedium to "Stencil 28/36 · 800 · 0.01 · upper",
        HoTypeRole.HeadlineSmall to "Stencil 24/32 · 800 · 0.01 · upper",
        HoTypeRole.TitleLarge to "Inter 20/28 · 500 · 0.0 · none",
        HoTypeRole.TitleMedium to "Inter 16/24 · 500 · 0.0 · none",
        HoTypeRole.TitleSmall to "Inter 14/20 · 500 · 0.0 · none",
        HoTypeRole.BodyLarge to "Inter 16/24 · 400 · 0.0 · none",
        HoTypeRole.BodyMedium to "Inter 14/20 · 400 · 0.0 · none",
        HoTypeRole.BodySmall to "Inter 12/16 · 400 · 0.01 · none",
        HoTypeRole.LabelLarge to "Inter 14/20 · 500 · 0.0 · none",
        HoTypeRole.LabelMedium to "Inter 12/16 · 400 · 0.02 · none",
        HoTypeRole.LabelSmall to "Inter 11/16 · 400 · 0.08 · upper",
    )

    @Test
    fun everyRoleMatchesTheDesignScale() {
        assertEquals(15, HoTypeRole.entries.size)
        HoTypeRole.entries.forEach { role ->
            val actual = "${role.font} ${role.sizeSp}/${role.lineHeightSp} · ${role.weight.weight} · " +
                "${role.letterSpacingEm} · ${if (role.uppercase) "upper" else "none"}"
            assertEquals(design.getValue(role), actual, role.name)
        }
    }

    @Test
    fun stylesCarryTheRoleMetrics() = runComposeUiTest {
        val styles = mutableMapOf<HoTypeRole, TextStyle>()
        setContent {
            HoTheme(darkTheme = true) {
                HoTypeRole.entries.forEach { styles[it] = HoTheme.typography.style(it) }
            }
        }
        waitForIdle()

        val display = styles.getValue(HoTypeRole.DisplayLarge)
        assertEquals(57.sp, display.fontSize)
        assertEquals(64.sp, display.lineHeight)
        assertEquals(FontWeight.ExtraBold, display.fontWeight)
        assertEquals(0.01.em, display.letterSpacing)

        val label = styles.getValue(HoTypeRole.LabelSmall)
        assertEquals(11.sp, label.fontSize)
        assertEquals(0.08.em, label.letterSpacing)

        val stencil = styles.getValue(HoTypeRole.HeadlineSmall).fontFamily
        val inter = styles.getValue(HoTypeRole.BodyMedium).fontFamily
        assertTrue(stencil != inter, "Display/Headline roles must not share Inter's font family")
        assertEquals(inter, styles.getValue(HoTypeRole.TitleLarge).fontFamily)
    }

    @Test
    fun monoFamilyRendersOutsideTheScale() = runComposeUiTest {
        var mono: FontFamily? = null
        var inter: FontFamily? = null
        setContent {
            HoTheme(darkTheme = false) {
                mono = HoTheme.typography.mono
                inter = HoTheme.typography.style(HoTypeRole.BodyMedium).fontFamily
                BasicText("homeout presents", Modifier.testTag("mono"), style = TextStyle(fontFamily = HoTheme.typography.mono))
            }
        }
        onNodeWithTag("mono").assertExists()
        assertNotNull(mono)
        assertTrue(mono != inter, "mono must be its own family, not Inter")
        assertEquals(15, HoTypeRole.entries.size)
    }
}
