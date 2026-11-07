package app.homeout.event

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Back on the left; Share and Save on the right. Share and Save do nothing yet. */
@Composable
internal fun FloatingButtons(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        RoundButton(HoIcons.ArrowLeft, "Back to events", onBack, colors.glass, colors.divider, colors.onSurface, 22.dp)
        Box(Modifier.weight(1f))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RoundButton(HoIcons.ShareNetwork, "Share event", {}, colors.glass, colors.divider, colors.onSurface, 21.dp)
            RoundButton(
                HoIcons.BookmarkSimple,
                "Save event",
                {},
                colors.secondaryContainer,
                colors.secondaryContainer,
                colors.onSecondaryContainer,
                21.dp,
            )
        }
    }
}

@Composable
internal fun RoundButton(
    icon: DrawableResource,
    label: String,
    onClick: () -> Unit,
    fill: Color,
    border: Color,
    tint: Color,
    iconSize: Dp,
) {
    Box(
        Modifier
            .size(44.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .background(fill, CircleShape)
            .border(1.dp, border, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(painterResource(icon), contentDescription = label, Modifier.size(iconSize), colorFilter = ColorFilter.tint(tint))
    }
}
