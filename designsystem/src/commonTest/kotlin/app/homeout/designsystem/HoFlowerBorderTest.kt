package app.homeout.designsystem

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertLeftPositionInRootIsEqualTo
import androidx.compose.ui.test.assertTopPositionInRootIsEqualTo
import androidx.compose.ui.test.assertWidthIsEqualTo
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.v2.runComposeUiTest
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertFalse

@OptIn(ExperimentalTestApi::class)
class HoFlowerBorderTest {

    private val pieces = listOf("flowers-left", "flowers-mid", "flowers-right")

    @Test
    fun piecesSitAtTheTopWhereTheDesignPlacesThem() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = false) {
                Box(Modifier.width(372.dp)) { HoFlowerBorder() }
            }
        }
        onNodeWithTag("flowers-left", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo(0.dp).assertTopPositionInRootIsEqualTo(0.dp).assertWidthIsEqualTo(124.dp)
        onNodeWithTag("flowers-mid", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo(112.dp).assertTopPositionInRootIsEqualTo(0.dp).assertWidthIsEqualTo(176.dp)
        onNodeWithTag("flowers-right", useUnmergedTree = true)
            .assertLeftPositionInRootIsEqualTo(222.dp).assertTopPositionInRootIsEqualTo(0.dp).assertWidthIsEqualTo(150.dp)
    }

    @Test
    fun borderIsDecorativeOnly() = runComposeUiTest {
        setContent {
            HoTheme(darkTheme = true) { HoFlowerBorder() }
        }
        pieces.forEach { tag ->
            val config = onNodeWithTag(tag, useUnmergedTree = true).fetchSemanticsNode().config
            assertFalse(SemanticsProperties.ContentDescription in config, "$tag must not be announced")
            assertFalse(SemanticsActions.OnClick in config, "$tag must not take taps")
        }
    }
}
