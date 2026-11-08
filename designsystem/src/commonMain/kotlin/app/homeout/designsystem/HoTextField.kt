package app.homeout.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val FieldShape = RoundedCornerShape(8.dp)

/**
 * A labelled text field from the design: 48 dp, the `field` fill, a 1 dp border that turns `error` when [error]
 * or [invalid] is set. The caller owns [state], so a password's state can be a plain `remember` that never
 * reaches saved state. [modifier] applies to the field itself, so a test tag lands on the node that takes text.
 * [error] replaces [supporting], the hint under the field; [invalid] alone only colors
 * the border and the hint. [onFocusLost] fires when the field had focus and loses it.
 */
@Composable
fun HoTextField(
    label: String,
    state: TextFieldState,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    password: Boolean = false,
    error: String? = null,
    supporting: String? = null,
    invalid: Boolean = false,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onFocusLost: () -> Unit = {},
) {
    val colors = HoTheme.colors
    val isError = error != null || invalid
    val note = error ?: supporting
    var hadFocus by remember { mutableStateOf(false) }
    val fieldModifier = modifier
        .fillMaxWidth()
        .height(48.dp)
        .onFocusChanged {
            if (hadFocus && !it.isFocused) onFocusLost()
            hadFocus = it.isFocused
        }
        .semantics {
            if (error != null) error(error)
            if (supporting != null && error == null) stateDescription = supporting
        }
    val textStyle = HoTheme.typography.style(HoTypeRole.BodyLarge).copy(color = colors.onSurface, fontSize = 15.sp)
    val cursor = SolidColor(colors.primary)
    val decorator: @Composable (@Composable () -> Unit) -> Unit = { inner ->
        Box(
            Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(colors.field, FieldShape)
                .border(1.dp, if (isError) colors.error else colors.fieldBorder, FieldShape)
                .padding(horizontal = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (placeholder != null && state.text.isEmpty()) {
                HoText(placeholder, HoTypeRole.BodyLarge, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 15.sp), maxLines = 1)
            }
            inner()
        }
    }

    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        HoText(label, HoTypeRole.LabelLarge, style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium))
        if (password) {
            BasicSecureTextField(
                state = state,
                modifier = fieldModifier,
                textStyle = textStyle,
                cursorBrush = cursor,
                decorator = { inner -> decorator(inner) },
            )
        } else {
            BasicTextField(
                state = state,
                modifier = fieldModifier,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = keyboardOptions,
                textStyle = textStyle,
                cursorBrush = cursor,
                decorator = { inner -> decorator(inner) },
            )
        }
        if (note != null) {
            HoText(
                note,
                HoTypeRole.BodySmall,
                color = if (isError) colors.error else colors.onSurfaceVariant,
                style = TextStyle(fontSize = 12.sp),
            )
        }
    }
}
