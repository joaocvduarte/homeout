package app.homeout.qr

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Ticket
import app.homeout.domain.TicketGroup
import app.homeout.event.RoundButton
import app.homeout.event.posterDate
import app.homeout.tickets.detail
import app.homeout.tickets.label
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

/** Dark modules: the design draws them in its darkest ink on white, so scanners read them in dark mode too. */
private val QrInk = Color(0xFF0F1817)

@Composable
fun QrRoute(viewModel: QrViewModel, timeZone: TimeZone, onBack: () -> Unit) {
    val group by viewModel.group.collectAsState()
    group?.let { QrScreen(it, viewModel.initialIndex, timeZone, onBack) }
}

/** One card per ticket of the event, swiped sideways, opening on [initialIndex]. */
@Composable
internal fun QrScreen(group: TicketGroup, initialIndex: Int, timeZone: TimeZone, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    val count = group.tickets.size
    val pager = rememberPagerState(initialPage = initialIndex.coerceIn(0, count - 1)) { count }
    val scope = rememberCoroutineScope()
    val current = pager.currentPage
    Column(modifier.fillMaxSize()) {
        Row(
            Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoundButton(HoIcons.ArrowLeft, "Back to tickets", onBack, colors.glass, colors.divider, colors.onSurface, 22.dp)
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                HoText(
                    "Your ticket",
                    HoTypeRole.DisplaySmall,
                    modifier = Modifier.semantics { heading() },
                    style = TextStyle(fontSize = 30.sp, lineHeight = 30.sp, letterSpacing = 0.5.sp),
                )
                HoText(
                    "${group.event.title} · ${group.tickets[current].label()}",
                    HoTypeRole.BodyMedium,
                    color = colors.onSurfaceVariant,
                    modifier = Modifier.testTag("qr-position").semantics { liveRegion = LiveRegionMode.Polite },
                    style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                )
            }
        }
        HorizontalPager(
            state = pager,
            modifier = Modifier.weight(1f).fillMaxWidth().testTag("qr-pager"),
            contentPadding = PaddingValues(top = 18.dp, bottom = 14.dp),
        ) { page ->
            TicketCard(group, group.tickets[page], timeZone, Modifier.padding(horizontal = 20.dp))
        }
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets(bottom = 26.dp))),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (count > 1) {
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    StepArrow(HoGlyphs.ChevronLeft, "Previous ticket", enabled = current > 0) {
                        scope.launch { pager.animateScrollToPage(current - 1) }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        repeat(count) { Dot(active = it == current) }
                    }
                    StepArrow(HoGlyphs.ChevronRight, "Next ticket", enabled = current < count - 1) {
                        scope.launch { pager.animateScrollToPage(current + 1) }
                    }
                }
            }
            HoText(
                if (count > 1) "Swipe sideways for your other tickets. Show each code at the entrance." else "Show this code at the entrance.",
                HoTypeRole.BodyMedium,
                color = colors.onSurfaceVariant,
                style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp, textAlign = TextAlign.Center),
            )
        }
    }
}

@Composable
private fun TicketCard(group: TicketGroup, ticket: Ticket, timeZone: TimeZone, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    val event = group.event
    val posterFg = Color(event.poster.content)
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier
            .fillMaxSize()
            .clip(shape)
            .background(colors.surface)
            .border(1.dp, colors.divider, shape)
            .testTag("qr-card-${ticket.code}"),
    ) {
        Column(
            Modifier.fillMaxWidth().background(Color(event.poster.background)).padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            HoText(event.title, HoTypeRole.HeadlineMedium, color = posterFg, style = TextStyle(fontSize = 26.sp, lineHeight = 26.sp, letterSpacing = 0.3.sp))
            HoText(
                "${event.posterDate(timeZone)} · ${event.venue}".uppercase(),
                HoTypeRole.BodySmall,
                color = posterFg,
                style = TextStyle(fontFamily = HoTheme.typography.mono, fontSize = 11.sp, lineHeight = 14.sp, letterSpacing = 0.04.em),
            )
        }
        Column(
            Modifier.weight(1f).fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        ) {
            QrCode(ticket.code, "QR code for ${ticket.label()}, ${ticket.code}")
            HoText(
                ticket.code,
                HoTypeRole.BodyMedium,
                style = TextStyle(fontFamily = HoTheme.typography.mono, fontSize = 14.sp, letterSpacing = 0.08.em),
            )
        }
        val dash = PathEffect.dashPathEffect(floatArrayOf(4f, 4f))
        Column(
            Modifier
                .fillMaxWidth()
                .drawBehind { drawLine(colors.divider, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx(), pathEffect = dash) }
                .padding(start = 18.dp, end = 18.dp, top = 12.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            HoText(ticket.label(), HoTypeRole.TitleSmall)
            HoText(ticket.detail(), HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
        }
    }
}

/** The placeholder pattern on a white tile, 210dp, as the design draws it. */
@Composable
private fun QrCode(code: String, description: String) {
    val modules = remember(code) { QrPattern.modules(code) }
    Box(Modifier.background(Color.White, RoundedCornerShape(10.dp)).padding(14.dp)) {
        Canvas(Modifier.size(210.dp).semantics { contentDescription = description }) {
            val cell = size.width / QrPattern.SIZE
            modules.forEachIndexed { y, row ->
                row.forEachIndexed { x, dark ->
                    if (dark) drawRect(QrInk, topLeft = Offset(x * cell, y * cell), size = Size(cell, cell))
                }
            }
        }
    }
}

@Composable
private fun StepArrow(glyph: ImageVector, label: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = HoTheme.colors
    Box(
        Modifier
            .size(40.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .border(1.dp, colors.divider, CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(glyph, contentDescription = label, modifier = Modifier.size(18.dp), colorFilter = ColorFilter.tint(colors.onSurface))
    }
}

@Composable
private fun Dot(active: Boolean) {
    val colors = HoTheme.colors
    val width by animateDpAsState(if (active) 20.dp else 8.dp, tween(200), label = "dot")
    Box(Modifier.width(width).height(8.dp).background(if (active) colors.onPrimaryText else colors.divider, RoundedCornerShape(4.dp)))
}
