package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoTypeRole

/** The content column: at most 1200 wide, centered, with the page's gutters at its sides. */
@Composable
internal fun Wrap(layout: WebsiteLayout, modifier: Modifier = Modifier, content: @Composable androidx.compose.foundation.layout.BoxScope.() -> Unit) {
    Box(modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        Box(Modifier.widthIn(max = WebsiteLayout.ContentMax).fillMaxWidth().padding(horizontal = layout.gutter), content = content)
    }
}

/** The stencil "homeout" on its teal block. [letterSpacing] is in em. */
@Composable
internal fun Wordmark(size: Dp, padding: PaddingValues, letterSpacing: Float, lineHeight: Float, modifier: Modifier = Modifier) {
    Box(modifier.background(WebsiteColors.brand).padding(padding)) {
        SiteText("homeout", HoTypeRole.HeadlineSmall, size, WebsiteColors.ink, lineHeight = lineHeight, letterSpacing = letterSpacing)
    }
}

/** 72 high over the page: the wordmark (to the top), the section links when there is room, and the Get the app pill. */
@Composable
internal fun WebsiteNav(layout: WebsiteLayout, goTo: (Section) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().height(WebsiteLayout.NavHeight)) {
        Wrap(layout, Modifier.height(WebsiteLayout.NavHeight)) {
            Row(
                Modifier.fillMaxWidth().height(WebsiteLayout.NavHeight),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Wordmark(
                    size = 26.dp,
                    padding = PaddingValues(start = 12.dp, end = 12.dp, top = 2.dp, bottom = 4.dp),
                    letterSpacing = 2f / 26f,
                    lineHeight = 1.1f,
                    modifier = Modifier
                        .semantics { contentDescription = "homeout home" }
                        .clickable(role = Role.Button) { goTo(Section.Top) },
                )
                if (layout.navLinks) {
                    Row(horizontalArrangement = Arrangement.spacedBy(28.dp)) {
                        TextLink("Next events", { goTo(Section.Events) }, WebsiteColors.ink)
                        TextLink("How it works", { goTo(Section.How) }, WebsiteColors.ink)
                        TextLink("Get the app", { goTo(Section.App) }, WebsiteColors.ink)
                    }
                }
                PillButton(
                    "Get the app", { goTo(Section.App) }, WebsiteColors.pill, WebsiteColors.pillInk,
                    modifier = Modifier.testTag("nav-get-app"),
                    height = 44.dp, horizontalPadding = 20.dp, textSize = 15.dp,
                )
            }
        }
    }
}
