package app.homeout.website

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole

/**
 * Website text in a design font role at the design's own size. [lineHeight] and [letterSpacing]
 * are multiples of the size, like CSS `em`.
 */
@Composable
internal fun SiteText(
    text: String,
    role: HoTypeRole,
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    lineHeight: Float = 1.2f,
    letterSpacing: Float = 0f,
    align: TextAlign = TextAlign.Unspecified,
    underline: Boolean = false,
) {
    HoText(
        text = text,
        role = role,
        modifier = modifier,
        color = color,
        style = TextStyle(
            fontSize = size.value.sp,
            lineHeight = lineHeight.em,
            letterSpacing = letterSpacing.em,
            textAlign = align,
            textDecoration = if (underline) androidx.compose.ui.text.style.TextDecoration.Underline else null,
        ),
    )
}

/** JetBrains Mono in capitals: the posters' and eyebrows' labels. */
@Composable
internal fun MonoText(
    text: String,
    size: Dp,
    color: Color,
    modifier: Modifier = Modifier,
    letterSpacing: Float = 0f,
) {
    BasicText(
        text = text.uppercase(),
        modifier = modifier,
        style = TextStyle(
            fontFamily = HoTheme.typography.mono,
            fontSize = size.value.sp,
            lineHeight = 1.3f.em,
            letterSpacing = letterSpacing.em,
            color = color,
        ),
    )
}

/** A 1 dp line along the top edge, like the posters' `border-top`. */
internal fun Modifier.topRule(color: Color): Modifier = drawBehind {
    val y = 0.5.dp.toPx()
    drawLine(color, Offset(0f, y), Offset(size.width, y), strokeWidth = 1.dp.toPx())
}
