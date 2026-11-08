package app.homeout.tickets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoButton
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Ticket
import app.homeout.domain.TicketGroup
import app.homeout.event.posterDate
import kotlinx.datetime.TimeZone

/** The Tickets tab: every ticket bought, one card per event, or the empty state. */
@Composable
internal fun TicketsScreen(
    groups: List<TicketGroup>,
    confirmation: String?,
    timeZone: TimeZone,
    onTicketClick: (eventId: String, index: Int) -> Unit,
    onFindEvents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    val ticketCount = groups.sumOf { it.tickets.size }
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            HoText(
                "Your tickets",
                HoTypeRole.DisplaySmall,
                modifier = Modifier.semantics { heading() },
                style = TextStyle(lineHeight = 36.sp, letterSpacing = 0.5.sp),
            )
            HoText(ticketsSummary(ticketCount, groups.size), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant)
        }
        if (confirmation != null) Confirmation(confirmation)
        groups.forEach { group -> EventTickets(group, timeZone, onTicketClick) }
        if (groups.isEmpty()) EmptyState(onFindEvents)
    }
}

@Composable
private fun Confirmation(message: String) {
    val colors = HoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.secondaryContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("confirmation")
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(HoGlyphs.CheckCircle, null, Modifier.size(22.dp), colorFilter = ColorFilter.tint(colors.onSecondaryContainer))
        HoText(message, HoTypeRole.BodyMedium, color = colors.onSecondaryContainer)
    }
}

@Composable
private fun EventTickets(group: TicketGroup, timeZone: TimeZone, onTicketClick: (String, Int) -> Unit) {
    val colors = HoTheme.colors
    val event = group.event
    val posterBg = Color(event.poster.background)
    val posterFg = Color(event.poster.content)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(colors.surface)
            .testTag("tickets-${event.id}"),
    ) {
        Row(
            Modifier.fillMaxWidth().background(posterBg).padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                HoText(
                    event.title,
                    HoTypeRole.HeadlineSmall,
                    color = posterFg,
                    modifier = Modifier.semantics { heading() },
                    style = TextStyle(lineHeight = 24.sp, letterSpacing = 0.3.sp),
                )
                HoText(
                    "${event.posterDate(timeZone)} · ${event.venue}".uppercase(),
                    HoTypeRole.BodySmall,
                    color = posterFg,
                    style = TextStyle(fontFamily = HoTheme.typography.mono, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.04.em),
                )
            }
            Box(
                Modifier
                    .border(1.dp, posterFg, RoundedCornerShape(6.dp))
                    .padding(horizontal = 10.dp, vertical = 3.dp),
            ) {
                HoText(ticketCountLabel(group.tickets.size), HoTypeRole.LabelMedium, color = posterFg, style = TextStyle(letterSpacing = 0.sp))
            }
        }
        group.tickets.forEachIndexed { index, ticket ->
            TicketRow(ticket, onClick = { onTicketClick(event.id, index) })
        }
    }
}

@Composable
private fun TicketRow(ticket: Ticket, onClick: () -> Unit) {
    val colors = HoTheme.colors
    val dash = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
    Row(
        Modifier
            .fillMaxWidth()
            .drawBehind { drawLine(colors.divider, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx(), pathEffect = dash) }
            .clickable(role = Role.Button, onClickLabel = "Show QR code", onClick = onClick)
            .testTag("ticket-${ticket.code}")
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).background(colors.background, RoundedCornerShape(6.dp)), contentAlignment = Alignment.Center) {
            Image(HoGlyphs.QrMini, null, Modifier.size(24.dp), colorFilter = ColorFilter.tint(colors.onSurface))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            HoText(ticket.label(), HoTypeRole.TitleSmall)
            HoText(ticket.detail(), HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
        }
        HoText(
            ticket.code,
            HoTypeRole.BodySmall,
            color = colors.onSurfaceVariant,
            style = TextStyle(fontFamily = HoTheme.typography.mono, fontSize = 11.sp, letterSpacing = 0.sp),
        )
        Image(HoGlyphs.ChevronRight, null, Modifier.size(18.dp), colorFilter = ColorFilter.tint(colors.onSurfaceVariant))
    }
}

@Composable
private fun ColumnScope.EmptyState(onFindEvents: () -> Unit) {
    val colors = HoTheme.colors
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 24.dp).testTag("tickets-empty"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(64.dp).clip(CircleShape).background(colors.secondaryContainer), contentAlignment = Alignment.Center) {
            Image(HoGlyphs.Ticket, null, Modifier.size(30.dp), colorFilter = ColorFilter.tint(colors.onSecondaryContainer))
        }
        HoText("No tickets yet", HoTypeRole.TitleMedium, style = TextStyle(fontSize = 18.sp))
        HoText(
            "Tickets you buy show up here, grouped by event.",
            HoTypeRole.BodyMedium,
            color = colors.onSurfaceVariant,
            modifier = Modifier.widthIn(max = 260.dp),
            style = TextStyle(textAlign = TextAlign.Center),
        )
        HoButton("Find events", onClick = onFindEvents, modifier = Modifier.padding(top = 6.dp))
    }
}
