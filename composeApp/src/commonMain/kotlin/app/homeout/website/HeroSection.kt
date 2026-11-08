package app.homeout.website

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoFlower
import app.homeout.designsystem.HoFlowerPart
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoTypeRole

/**
 * The first screen: the logo's flowers bleeding from the top, the wordmark, the promise and two
 * calls to action. It is at least the viewport tall, counting the nav's 72 above it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HeroSection(layout: WebsiteLayout, goTo: (Section) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().heightIn(min = layout.height).clipToBounds()) {
        Box(Modifier.fillMaxWidth()) {
            HoFlower(HoFlowerPart.Left, Modifier.align(Alignment.TopStart).width(layout.clamp(160f, 22f, 320f)))
            HoFlower(HoFlowerPart.Mid, Modifier.align(Alignment.TopCenter).width(layout.clamp(220f, 30f, 440f)))
            HoFlower(HoFlowerPart.Right, Modifier.align(Alignment.TopEnd).width(layout.clamp(180f, 26f, 380f)))
        }
        Wrap(layout, Modifier.align(Alignment.Center)) {
            Column(
                Modifier.fillMaxWidth().padding(
                    top = WebsiteLayout.NavHeight + (layout.height * 0.18f).coerceIn(140.dp, 200.dp),
                    bottom = 80.dp,
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(28.dp),
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(22.dp)) {
                    val wordmark = layout.clamp(76f, 17f, 232f)
                    Wordmark(
                        size = wordmark,
                        padding = PaddingValues(start = wordmark * 0.14f, end = wordmark * 0.14f, top = wordmark * 0.02f, bottom = wordmark * 0.06f),
                        letterSpacing = 0.04f,
                        lineHeight = 0.95f,
                    )
                    SiteText(
                        "Head out tonight.", HoTypeRole.TitleLarge, layout.clamp(28f, 4.4f, 56f), WebsiteColors.ink,
                        lineHeight = 1.1f, letterSpacing = -0.02f, align = TextAlign.Center,
                    )
                }
                SiteText(
                    "Fado in candlelit cisterns, vinyl on the rooftops, night markets by the river. " +
                        "homeout finds what's on in Lisbon and keeps every ticket in your pocket.",
                    HoTypeRole.BodyLarge, layout.clamp(17f, 1.6f, 20f), WebsiteColors.muted,
                    modifier = Modifier.widthIn(max = 560.dp), lineHeight = 1.5f, align = TextAlign.Center,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    PillButton("Download the app", { goTo(Section.App) }, WebsiteColors.pill, WebsiteColors.pillInk)
                    PillButton(
                        "See what's on", { goTo(Section.Events) }, Color.Transparent, WebsiteColors.ink,
                        outline = WebsiteColors.ink.copy(alpha = 0.4f),
                    )
                }
                Column(
                    Modifier.padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    SiteText("SCROLL", HoTypeRole.BodySmall, 12.dp, WebsiteColors.muted, letterSpacing = 0.12f)
                    Image(HoGlyphs.ArrowDown, null, Modifier.size(20.dp), colorFilter = ColorFilter.tint(WebsiteColors.muted))
                }
            }
        }
    }
}
