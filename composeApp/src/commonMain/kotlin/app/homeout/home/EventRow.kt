package app.homeout.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Event
import kotlinx.datetime.TimeZone
import kotlin.math.sqrt

@Composable
internal fun EventRow(event: Event, timeZone: TimeZone, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(
        modifier = modifier.testTag("event-${event.id}").semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StripedThumbnail()
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            HoText(
                text = event.title,
                role = HoTypeRole.TitleMedium,
                style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp),
                maxLines = 1,
            )
            HoText(
                text = "${event.category.label()} · ${event.venue} · ${event.startsAt.timeLabel(timeZone)}",
                role = HoTypeRole.BodyMedium,
                color = colors.onSurfaceVariant,
                style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp),
                maxLines = 1,
            )
        }
        Box(
            Modifier
                .background(colors.secondaryContainer, RoundedCornerShape(6.dp))
                .padding(horizontal = 10.dp, vertical = 3.dp),
        ) {
            HoText(event.price.label(), HoTypeRole.BodySmall, color = colors.onSecondaryContainer, style = TextStyle(fontSize = 11.sp))
        }
    }
}

/** The design's 56dp diagonal-striped image placeholder. */
@Composable
private fun StripedThumbnail() {
    val colors = HoTheme.colors
    Canvas(
        Modifier
            .size(56.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(colors.surface),
    ) {
        val band = 6.dp.toPx()
        val step = 2 * band * sqrt(2f)
        var c = -size.height
        while (c < size.width + size.height) {
            drawLine(
                color = colors.stripe,
                start = Offset(c, size.height),
                end = Offset(c + size.height, 0f),
                strokeWidth = band,
            )
            c += step
        }
    }
}
