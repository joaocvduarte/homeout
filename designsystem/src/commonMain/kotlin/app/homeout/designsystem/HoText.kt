package app.homeout.designsystem

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow

/** Text in a design type role. Display, Headline and Label Small roles render in uppercase. */
@Composable
fun HoText(
    text: String,
    role: HoTypeRole,
    modifier: Modifier = Modifier,
    color: Color = HoTheme.colors.onSurface,
    style: TextStyle = TextStyle.Default,
    maxLines: Int = Int.MAX_VALUE,
) {
    BasicText(
        text = if (role.uppercase) text.uppercase() else text,
        modifier = modifier,
        style = HoTheme.typography.style(role).copy(color = color).merge(style),
        overflow = TextOverflow.Ellipsis,
        softWrap = maxLines != 1,
        maxLines = maxLines,
    )
}
