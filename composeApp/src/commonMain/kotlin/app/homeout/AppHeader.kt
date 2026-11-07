package app.homeout

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import org.jetbrains.compose.resources.painterResource

/** The wordmark, notifications bell and avatar. It stays at the top on every screen. */
@Composable
internal fun AppHeader(modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.background(colors.brand).padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 3.dp)) {
            HoText(
                text = "homeout",
                role = HoTypeRole.HeadlineSmall,
                color = HoColors.Dark.onSurface,
                style = TextStyle(lineHeight = 26.sp, letterSpacing = 2.sp),
            )
        }
        Box(Modifier.weight(1f))
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(HoIcons.Bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(colors.onSurface),
            )
        }
        Box(
            Modifier.padding(start = 8.dp).size(36.dp).background(colors.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            HoText("MA", HoTypeRole.LabelLarge, color = colors.onSecondaryContainer, style = TextStyle(fontSize = 13.sp))
        }
    }
}
