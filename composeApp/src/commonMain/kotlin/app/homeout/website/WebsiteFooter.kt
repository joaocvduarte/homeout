package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoTypeRole

/** The brand, the line, and the two links the website keeps. There is no Terms or Privacy. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun WebsiteFooter(layout: WebsiteLayout, goTo: (Section) -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier.fillMaxWidth().background(WebsiteColors.footer).padding(top = 48.dp, bottom = 40.dp),
    ) {
        Wrap(layout) {
            FlowRow(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    itemVerticalAlignment = Alignment.CenterVertically,
                ) {
                    Wordmark(
                        size = 22.dp,
                        padding = PaddingValues(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 3.dp),
                        letterSpacing = 2f / 22f,
                        lineHeight = 1.1f,
                    )
                    SiteText("Made in Lisbon · © 2026", HoTypeRole.BodyMedium, 14.dp, WebsiteColors.muted)
                }
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    TextLink("Events", { goTo(Section.Events) }, WebsiteColors.muted, size = 14.dp)
                    TextLink("How it works", { goTo(Section.How) }, WebsiteColors.muted, size = 14.dp)
                }
            }
        }
    }
}
