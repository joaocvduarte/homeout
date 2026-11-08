package app.homeout

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import org.jetbrains.compose.resources.painterResource

/**
 * The wordmark, notifications bell and avatar. It stays at the top on every screen except Log in and Create account,
 * where it fades out (and out of accessibility) but keeps its space; it fades back in over 360 ms on Home.
 */
@Composable
internal fun AppHeader(modifier: Modifier = Modifier, visible: Boolean = true) {
    val colors = HoTheme.colors
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(360, easing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)),
        label = "header",
    )
    Row(
        modifier.fillMaxWidth().alpha(alpha).then(if (visible) Modifier else Modifier.clearAndSetSemantics {}),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
