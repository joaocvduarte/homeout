package app.homeout.website

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoStore
import app.homeout.designsystem.HoStoreBadge
import app.homeout.designsystem.HoTypeRole

/**
 * The website's full-round button. Web only, so the phone app's `HoButton` can't change.
 * It lifts 1 dp on hover and shows a 2 dp ring in its ink color when focused. [outline] adds an inset line.
 */
@Composable
internal fun PillButton(
    text: String,
    onClick: () -> Unit,
    background: Color,
    ink: Color,
    modifier: Modifier = Modifier,
    height: Dp = 52.dp,
    horizontalPadding: Dp = 26.dp,
    textSize: Dp = 16.dp,
    outline: Color? = null,
    description: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val lift by animateFloatAsState(if (hovered) -1f else 0f, tween(160), label = "lift")
    val shape = RoundedCornerShape(percent = 50)
    Row(
        modifier
            .graphicsLayer { translationY = lift.dp.toPx() }
            .then(if (focused) Modifier.drawBehind { ring(ink) } else Modifier)
            .height(height)
            .background(background, shape)
            .then(if (outline != null) Modifier.border(1.5.dp, outline, shape) else Modifier)
            .hoverable(interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
            .then(if (description != null) Modifier.semantics { contentDescription = description } else Modifier)
            .padding(horizontal = horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        SiteText(text, HoTypeRole.TitleMedium, textSize, ink)
    }
}

/** A plain text link, underlined while hovered or focused, with a focus ring. */
@Composable
internal fun TextLink(text: String, onClick: () -> Unit, color: Color, modifier: Modifier = Modifier, size: Dp = 15.dp) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    SiteText(
        text = text,
        role = HoTypeRole.BodyLarge,
        size = size,
        color = color,
        underline = hovered || focused,
        modifier = modifier
            .then(if (focused) Modifier.drawBehind { ring(color) } else Modifier)
            .hoverable(interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
    )
}

/** A store badge as a button: it lifts 2 dp on hover and shows a 2 dp ring when focused. The badge says what it is. */
@Composable
internal fun StoreBadgeButton(store: HoStore, onClick: () -> Unit, ringColor: Color, modifier: Modifier = Modifier) {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    val focused by interaction.collectIsFocusedAsState()
    val lift by animateFloatAsState(if (hovered) -2f else 0f, tween(160), label = "lift")
    HoStoreBadge(
        store,
        modifier
            .graphicsLayer { translationY = lift.dp.toPx() }
            .then(if (focused) Modifier.drawBehind { ring(ringColor, radius = 10.dp.toPx()) } else Modifier)
            .hoverable(interaction)
            .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick),
    )
}

private fun DrawScope.ring(color: Color, radius: Float = size.height / 2) {
    val grow = 3.dp.toPx()
    drawRoundRect(
        color = color,
        topLeft = Offset(-grow, -grow),
        size = Size(size.width + 2 * grow, size.height + 2 * grow),
        cornerRadius = CornerRadius(radius + grow),
        style = Stroke(2.dp.toPx()),
    )
}
