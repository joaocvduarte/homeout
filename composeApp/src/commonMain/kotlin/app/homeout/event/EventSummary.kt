package app.homeout.event

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.EventDetail
import kotlinx.datetime.TimeZone

/** The event in a card: its poster color, title, date and time, and venue. Buy tickets and Get the app both open with it. */
@Composable
internal fun EventSummary(detail: EventDetail, timeZone: TimeZone, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    val event = detail.event
    Row(
        modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(8.dp)).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)).background(Color(event.poster.background))) {
            HoPosterFlower(Modifier.align(Alignment.TopEnd).offset(x = 8.dp).width(64.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            HoText(event.title, HoTypeRole.TitleMedium, style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp), maxLines = 1)
            HoText(event.posterDate(timeZone), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp))
            HoText(detail.fullVenue(), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp), maxLines = 1)
        }
    }
}
