package app.homeout.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole

/**
 * A field's text state, initialised from [value] and kept in step with it both ways. It is a plain `remember`,
 * never saveable: the ViewModel is the source, and a saved copy could overwrite a prefilled email.
 */
@Composable
internal fun rememberFieldState(value: String, onChange: (String) -> Unit): TextFieldState {
    val field = remember { TextFieldState(value) }
    val latest by rememberUpdatedState(onChange)
    LaunchedEffect(field) { snapshotFlow { field.text.toString() }.collect { latest(it) } }
    LaunchedEffect(value) { if (field.text.toString() != value) field.setTextAndPlaceCursorAtEnd(value) }
    return field
}

/** The wordmark block of Log in: the homeout logo, large. */
@Composable
internal fun BigLogo() {
    Box(Modifier.background(HoTheme.colors.brand).padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 6.dp)) {
        HoText(
            text = "homeout",
            role = HoTypeRole.HeadlineSmall,
            color = HoColors.Dark.onSurface,
            style = TextStyle(fontSize = 52.sp, lineHeight = 52.sp, letterSpacing = 4.sp),
        )
    }
}

/** The fake Google sign-in button: the surface fill, lighter when hovered. */
@Composable
internal fun GoogleButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    val interactions = remember { MutableInteractionSource() }
    val hovered by interactions.collectIsHoveredAsState()
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(if (hovered) colors.surfaceHover else colors.surface, shape)
            .border(1.dp, colors.divider, shape)
            .hoverable(interactions)
            .clickable(interactionSource = interactions, indication = null, role = Role.Button, onClick = onClick),
        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(HoGlyphs.Account, contentDescription = null, modifier = Modifier.size(20.dp), colorFilter = ColorFilter.tint(colors.onSurface))
        HoText(text, HoTypeRole.LabelLarge, style = TextStyle(fontSize = 15.sp), maxLines = 1)
    }
}

/** "or log in with email": a hairline either side of a muted caption. Decoration only. */
@Composable
internal fun OrDivider(text: String) {
    val colors = HoTheme.colors
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.weight(1f).height(1.dp).background(colors.divider))
        HoText(text, HoTypeRole.BodySmall, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 12.sp))
        Box(Modifier.weight(1f).height(1.dp).background(colors.divider))
    }
}

/** An error alert above a form: the error border and tint, the alert mark and the message. [action] sits under it. */
@Composable
internal fun ErrorAlert(message: String, modifier: Modifier = Modifier, action: (@Composable () -> Unit)? = null) {
    val colors = HoTheme.colors
    val shape = RoundedCornerShape(8.dp)
    Row(
        modifier
            .fillMaxWidth()
            .background(colors.errorContainer.compositeOver(colors.background), shape)
            .border(1.dp, colors.error, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Image(HoGlyphs.AlertCircle, contentDescription = null, modifier = Modifier.size(20.dp), colorFilter = ColorFilter.tint(colors.error))
        Column(Modifier.weight(1f)) {
            HoText(message, HoTypeRole.BodyMedium, style = TextStyle(lineHeight = 20.sp))
            action?.invoke()
        }
    }
}

/** An underlined text button in the primary text color. */
@Composable
internal fun LinkButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, textSize: Int = 14) {
    HoText(
        text,
        HoTypeRole.LabelLarge,
        modifier = modifier.clickable(role = Role.Button, onClick = onClick).padding(horizontal = 4.dp, vertical = 10.dp),
        color = HoTheme.colors.onPrimaryText,
        style = TextStyle(fontSize = textSize.sp, textDecoration = TextDecoration.Underline),
    )
}

/** The pinned footer: a muted question and the link that answers it. */
@Composable
internal fun AuthFooter(question: String, link: String, onClick: () -> Unit, linkTag: String) {
    Row(
        Modifier.fillMaxWidth().padding(bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HoText(question, HoTypeRole.BodyMedium, color = HoTheme.colors.onSurfaceVariant)
        LinkButton(link, onClick, Modifier.testTag(linkTag))
    }
}
