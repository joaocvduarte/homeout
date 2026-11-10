package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoFlower
import app.homeout.designsystem.HoFlowerPart
import app.homeout.designsystem.HoStore
import app.homeout.designsystem.HoTypeRole

/**
 * The call to action every ticket, download and "see all" action scrolls to. The store buttons
 * go nowhere yet. Mirrored flowers hang from the bottom edge.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun GetAppSection(layout: WebsiteLayout, goTo: (Section) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxWidth()
            .background(WebsiteColors.page)
            .clipToBounds(),
    ) {
        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(260.dp)
                .offset(y = 40.dp)
                .graphicsLayer { scaleY = -1f; alpha = 0.9f },
        ) {
            HoFlower(HoFlowerPart.Left, Modifier.align(Alignment.TopStart).width(layout.clamp(160f, 22f, 300f)))
            HoFlower(HoFlowerPart.Right, Modifier.align(Alignment.TopEnd).width(layout.clamp(180f, 26f, 360f)))
        }
        Wrap(layout, Modifier.padding(vertical = layout.clamp(100f, 14f, 180f))) {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(26.dp),
            ) {
                SiteText(
                    "Head out\ntonight", HoTypeRole.DisplayLarge, layout.clamp(60f, 11f, 160f), WebsiteColors.ink,
                    lineHeight = 0.9f, letterSpacing = 0.01f, align = TextAlign.Center, modifier = Modifier.reveal(),
                )
                SiteText(
                    "Free on iPhone and Android. Log in with your Google account or create one in a minute.",
                    HoTypeRole.BodyLarge, 18.dp, WebsiteColors.muted,
                    modifier = Modifier.widthIn(max = 520.dp).reveal(), lineHeight = 1.5f, align = TextAlign.Center,
                )
                FlowRow(
                    modifier = Modifier.reveal(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    StoreBadgeButton(HoStore.AppStore, { goTo(Section.App) }, WebsiteColors.ink, Modifier.testTag("store-app-store"))
                    StoreBadgeButton(HoStore.GooglePlay, { goTo(Section.App) }, WebsiteColors.ink, Modifier.testTag("store-google-play"))
                }
            }
        }
    }
}
