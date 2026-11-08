package app.homeout.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val BoxShape = RoundedCornerShape(4.dp)

/** A 20 dp checkbox with its [label]. The whole row toggles, so the label is a tap target too. */
@Composable
fun HoCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    Row(
        modifier
            .fillMaxWidth()
            .toggleable(value = checked, role = Role.Checkbox, onValueChange = onCheckedChange),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Canvas(
            Modifier
                .size(20.dp)
                .background(if (checked) colors.primary else colors.field, BoxShape)
                .border(1.dp, if (checked) colors.primary else colors.fieldBorder, BoxShape),
        ) {
            if (checked) {
                val stroke = 2.dp.toPx()
                val tick = colors.background
                drawLine(tick, Offset(size.width * 0.25f, size.height * 0.52f), Offset(size.width * 0.43f, size.height * 0.70f), stroke, StrokeCap.Round)
                drawLine(tick, Offset(size.width * 0.43f, size.height * 0.70f), Offset(size.width * 0.76f, size.height * 0.32f), stroke, StrokeCap.Round)
            }
        }
        HoText(label, HoTypeRole.BodySmall, style = TextStyle(fontSize = 13.sp, lineHeight = 19.sp))
    }
}
