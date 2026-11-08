package app.homeout.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Event
import app.homeout.domain.EventIcon
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun EventRow(event: Event, timeZone: TimeZone, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(
        modifier = modifier
            .testTag("event-${event.id}")
            .clip(RoundedCornerShape(10.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .semantics(mergeDescendants = true) {},
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Thumbnail(event)
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

/** The event's poster color with its icon, 56dp. */
@Composable
private fun Thumbnail(event: Event) {
    Box(
        Modifier
            .size(56.dp)
            .background(Color(event.poster.background), RoundedCornerShape(8.dp))
            .testTag("thumb-${event.id}"),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(event.icon.drawable()),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(Color(event.poster.content)),
        )
    }
}

private fun EventIcon.drawable(): DrawableResource = when (this) {
    EventIcon.MUSIC_NOTES -> HoIcons.MusicNotes
    EventIcon.VINYL_RECORD -> HoIcons.VinylRecord
    EventIcon.FORK_KNIFE -> HoIcons.ForkKnife
    EventIcon.WINE -> HoIcons.Wine
}
