package app.homeout.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoButton
import app.homeout.designsystem.HoButtonSize
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.EventDetail
import app.homeout.home.label
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Tonight's events take the tertiary accent, as on Home; other days the accent text. */
internal fun dayLabelColor(isToday: Boolean, colors: HoColors): Color =
    if (isToday) colors.tertiary else colors.onPrimaryText

/** The design's Event page. The ticket button opens Buy tickets. */
@Composable
internal fun EventScreen(
    detail: EventDetail,
    timeZone: TimeZone,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    onBuy: () -> Unit = {},
) {
    Column(modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            Column(
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag("event-page")
                    .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                Poster(detail, timeZone)
                Details(detail, timeZone)
            }
            FloatingButtons(onBack, Modifier.padding(top = 28.dp, start = 32.dp, end = 32.dp))
        }
        TicketBar(detail, onBuy)
    }
}

@Composable
private fun Poster(detail: EventDetail, timeZone: TimeZone) {
    val background = Color(detail.event.poster.background)
    val content = Color(detail.event.poster.content)
    val mono = HoTheme.typography.mono
    Box(
        Modifier
            .fillMaxWidth()
            .height(380.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(background),
    ) {
        HoPosterFlower(Modifier.align(Alignment.TopEnd).width(190.dp))
        Column(
            Modifier.align(Alignment.BottomStart).fillMaxWidth().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            HoText(
                text = "homeout presents",
                role = HoTypeRole.LabelSmall,
                color = content,
                style = TextStyle(fontFamily = mono, fontSize = 11.sp, letterSpacing = 0.08.em),
            )
            HoText(
                text = detail.event.title,
                role = HoTypeRole.DisplayLarge,
                color = content,
                style = TextStyle(fontSize = 50.sp, lineHeight = 46.sp, letterSpacing = 0.5.sp),
            )
            val rule = content
            Row(
                Modifier
                    .fillMaxWidth()
                    .drawBehind { drawLine(rule, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val small = TextStyle(fontFamily = mono, fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.04.em)
                HoText(detail.posterDate(timeZone).uppercase(), HoTypeRole.BodySmall, color = content, style = small)
                HoText(detail.event.venue.uppercase(), HoTypeRole.BodySmall, color = content, style = small)
            }
        }
    }
}

@Composable
private fun Details(detail: EventDetail, timeZone: TimeZone) {
    val colors = HoTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HoText(
            text = detail.categoryAndDay(timeZone),
            role = HoTypeRole.LabelSmall,
            color = dayLabelColor(detail.isToday, colors),
            modifier = Modifier.testTag("event-day"),
        )
        HoText(
            text = detail.event.title,
            role = HoTypeRole.TitleLarge,
            style = TextStyle(fontSize = 22.sp, lineHeight = 28.sp),
            modifier = Modifier.semantics { heading() },
        )
    }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        IconLine(HoIcons.CalendarBlank, detail.whenLine(timeZone))
        IconLine(HoIcons.MapPin, detail.fullVenue())
    }
    Box(Modifier.fillMaxWidth().height(1.dp).background(colors.divider))
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        HoText(
            text = "About this event",
            role = HoTypeRole.LabelSmall,
            color = colors.onSurfaceVariant,
            modifier = Modifier.semantics { heading() },
        )
        HoText(
            text = detail.event.description,
            role = HoTypeRole.BodyLarge,
            style = TextStyle(fontSize = 15.sp, lineHeight = 23.sp),
        )
    }
}

@Composable
private fun IconLine(icon: DrawableResource, text: String) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(HoTheme.colors.onPrimaryText),
        )
        HoText(text, HoTypeRole.BodyMedium)
    }
}

@Composable
private fun TicketBar(detail: EventDetail, onBuy: () -> Unit) {
    val colors = HoTheme.colors
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind { drawLine(colors.divider, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .testTag("ticket-bar")
            .padding(start = 20.dp, end = 20.dp, top = 12.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets(bottom = 28.dp))),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            HoText(detail.event.price.label(), HoTypeRole.TitleLarge, style = TextStyle(lineHeight = 24.sp))
            HoText(detail.priceNote(), HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
        }
        HoButton(
            text = detail.ticketLabel(),
            onClick = onBuy,
            modifier = Modifier.weight(1f),
            size = HoButtonSize.Large,
            glyph = HoGlyphs.Ticket,
            block = true,
            tinted = true,
        )
    }
}
