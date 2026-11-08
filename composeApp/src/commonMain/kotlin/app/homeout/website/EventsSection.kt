package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoTypeRole
import kotlinx.datetime.TimeZone

/** The app's next events as posters, each with its price and a Get tickets action that leads to the app. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EventsSection(
    layout: WebsiteLayout,
    state: WebsiteState,
    timeZone: TimeZone,
    goTo: (Section) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxWidth()
            .background(WebsiteColors.lightSection, RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp))
            .padding(vertical = layout.clamp(80f, 12f, 140f)),
    ) {
        Wrap(layout) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(40.dp)) {
                FlowRow(
                    Modifier.fillMaxWidth().reveal(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    itemVerticalAlignment = Alignment.Bottom,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        MonoText("This week in Lisbon", 13.dp, WebsiteColors.kicker, letterSpacing = 0.1f)
                        SiteText(
                            "Next events", HoTypeRole.DisplayLarge, layout.clamp(52f, 8f, 112f), WebsiteColors.darkInk,
                            lineHeight = 0.9f, letterSpacing = 0.01f,
                        )
                    }
                    if (state.total > 0) {
                        PillButton(
                            seeAllLabel(state.total), { goTo(Section.App) }, WebsiteColors.darkInk, WebsiteColors.lightSection,
                            modifier = Modifier.testTag("website-see-all"),
                        )
                    }
                }
                EventCards(layout, state.events, timeZone, goTo)
            }
        }
    }
}

@Composable
private fun EventCards(layout: WebsiteLayout, events: List<WebsiteEvent>, timeZone: TimeZone, goTo: (Section) -> Unit) {
    if (layout.eventsScroll) {
        // Up to 640 the cards form one row that snaps card by card and bleeds to the screen's edges.
        val list = rememberLazyListState()
        LazyRow(
            modifier = Modifier.fillMaxWidth().fullBleed(layout.gutter),
            state = list,
            flingBehavior = rememberSnapFlingBehavior(list),
            contentPadding = PaddingValues(horizontal = layout.gutter),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            itemsIndexed(events, key = { _, item -> item.id }) { i, item -> EventCard(layout, item, timeZone, goTo, Modifier.width(layout.width * 0.82f).reveal(i * 90)) }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            events.chunked(layout.eventColumns).forEachIndexed { r, row ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                    row.forEachIndexed { c, item -> EventCard(layout, item, timeZone, goTo, Modifier.weight(1f).reveal((r * layout.eventColumns + c) * 90)) }
                    repeat(layout.eventColumns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

/** The row sits inside the content's gutters; this lets it run out to the screen's edges, as the design's negative margin does. */
private fun Modifier.fullBleed(gutter: Dp): Modifier = this.then(
    Modifier.layout { measurable, constraints ->
        val bleed = gutter.roundToPx()
        val width = constraints.maxWidth + 2 * bleed
        val placeable = measurable.measure(constraints.copy(minWidth = width, maxWidth = width))
        layout(constraints.maxWidth, placeable.height) { placeable.place(-bleed, 0) }
    },
)

@Composable
private fun EventCard(layout: WebsiteLayout, item: WebsiteEvent, timeZone: TimeZone, goTo: (Section) -> Unit, modifier: Modifier) {
    val event = item.event
    val background = Color(event.poster.background)
    val content = Color(event.poster.content)
    Column(modifier.testTag("website-event-${event.id}"), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 5f)
                .background(background, RoundedCornerShape(28.dp))
                .clipToBounds(),
        ) {
            HoPosterFlower(Modifier.align(Alignment.TopEnd).fillMaxWidth(0.62f))
            Column(Modifier.fillMaxSize().padding(22.dp), verticalArrangement = Arrangement.SpaceBetween) {
                SiteText(
                    item.kicker(timeZone), HoTypeRole.LabelLarge, 12.dp, content,
                    modifier = Modifier
                        .border(1.dp, content, RoundedCornerShape(percent = 50))
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                )
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SiteText(
                        event.title, HoTypeRole.HeadlineLarge, layout.clamp(34f, 3.4f, 44f), content,
                        lineHeight = 0.92f, letterSpacing = 0.01f,
                    )
                    Row(
                        Modifier.fillMaxWidth().topRule(content).padding(top = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        MonoText(event.websiteDate(timeZone), 12.dp, content, Modifier.weight(1f), letterSpacing = 0.04f)
                        MonoText(event.venue, 12.dp, content, letterSpacing = 0.04f)
                    }
                }
            }
        }
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                SiteText(event.categoryAndTime(timeZone), HoTypeRole.TitleMedium, 15.dp, WebsiteColors.darkInk)
                SiteText(event.websitePrice(), HoTypeRole.BodyMedium, 14.dp, WebsiteColors.lightMuted)
            }
            PillButton(
                "Get tickets", { goTo(Section.App) }, WebsiteColors.pill, WebsiteColors.pillInk,
                modifier = Modifier.testTag("website-tickets-${event.id}"),
                height = 40.dp, horizontalPadding = 16.dp, textSize = 14.dp, description = event.ticketsLabel(),
            )
        }
    }
}
