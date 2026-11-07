package app.homeout.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

enum class HoButtonVariant { Primary, Secondary, Outline }

/** Button sizes from the Button Set page. Small (32) exists on web only. */
class HoButtonSize internal constructor(
    internal val height: Dp,
    internal val horizontalPadding: Dp,
    internal val fontSizeSp: Int,
) {
    companion object {
        val Large = HoButtonSize(52.dp, 20.dp, 15)
        val Medium = HoButtonSize(44.dp, 16.dp, 14)
        internal val small = HoButtonSize(32.dp, 11.dp, 13)
    }
}

private val ButtonShape = RoundedCornerShape(8.dp)
private const val DisabledAlpha = 0.45f

/**
 * A labelled button, with an optional leading [icon]. [block] stretches it to the full width.
 * [tinted] gives a Primary button the accent tint at rest, as the event page's ticket button shows.
 */
@Composable
fun HoButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: HoButtonVariant = HoButtonVariant.Primary,
    size: HoButtonSize = HoButtonSize.Medium,
    icon: DrawableResource? = null,
    enabled: Boolean = true,
    block: Boolean = false,
    tinted: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    ButtonFrame(
        onClick = onClick,
        modifier = modifier.height(size.height).then(if (block) Modifier.fillMaxWidth() else Modifier),
        variant = variant,
        tinted = tinted,
        enabled = enabled,
        contentPadding = size.horizontalPadding,
        interactionSource = interactionSource,
    ) { content ->
        if (icon != null) {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                colorFilter = ColorFilter.tint(content),
            )
        }
        HoText(
            text = text,
            role = HoTypeRole.LabelLarge,
            color = content,
            style = TextStyle(fontSize = size.fontSizeSp.sp),
            maxLines = 1,
        )
    }
}

/** A square 44×44 button showing only [icon]; [contentDescription] names it for accessibility. */
@Composable
fun HoIconButton(
    icon: DrawableResource,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: HoButtonVariant = HoButtonVariant.Primary,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
) {
    ButtonFrame(
        onClick = onClick,
        modifier = modifier.size(44.dp),
        variant = variant,
        enabled = enabled,
        contentPadding = 0.dp,
        interactionSource = interactionSource,
    ) { content ->
        Image(
            painter = painterResource(icon),
            contentDescription = contentDescription,
            modifier = Modifier.size(18.dp),
            colorFilter = ColorFilter.tint(content),
        )
    }
}

internal data class ButtonColors(val container: Color, val pressed: Color, val content: Color, val border: Color)

@Composable
internal fun buttonColors(variant: HoButtonVariant, tinted: Boolean = false): ButtonColors {
    val c = HoTheme.colors
    return when (variant) {
        HoButtonVariant.Primary -> if (tinted) {
            ButtonColors(c.primaryPressed, c.primaryPressed.compositeOver(c.primaryPressed), c.onPrimaryText, c.primary)
        } else {
            ButtonColors(Color.Transparent, c.primaryPressed, c.onPrimaryText, c.primary)
        }
        HoButtonVariant.Secondary -> ButtonColors(c.secondaryContainer, c.secondaryPressed, c.onSecondaryContainer, c.secondaryContainer)
        HoButtonVariant.Outline -> ButtonColors(Color.Transparent, c.neutralPressed, c.onSurface, c.divider)
    }
}

@Composable
private fun ButtonFrame(
    onClick: () -> Unit,
    modifier: Modifier,
    variant: HoButtonVariant,
    enabled: Boolean,
    contentPadding: Dp,
    interactionSource: MutableInteractionSource?,
    tinted: Boolean = false,
    content: @Composable (contentColor: Color) -> Unit,
) {
    val colors = buttonColors(variant, tinted)
    val interactions = interactionSource ?: remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()

    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else DisabledAlpha)
            .clip(ButtonShape)
            .background(if (pressed && enabled) colors.pressed else colors.container, ButtonShape)
            .border(1.dp, colors.border, ButtonShape)
            .clickable(
                interactionSource = interactions,
                indication = null,
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = contentPadding),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content(colors.content)
    }
}
