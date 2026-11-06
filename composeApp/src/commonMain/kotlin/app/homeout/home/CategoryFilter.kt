package app.homeout.home

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Category

private val FilterShape = RoundedCornerShape(8.dp)

/** All / Music / Food segmented control. A null [selected] means All. */
@Composable
internal fun CategoryFilter(
    selected: Category?,
    onSelect: (Category?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    val options = listOf<Pair<String, Category?>>("All" to null) + Category.entries.map { it.label() to it }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(36.dp)
            .clip(FilterShape)
            .border(1.dp, colors.divider, FilterShape)
            .selectableGroup(),
    ) {
        options.forEach { (label, category) ->
            val isSelected = category == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(36.dp)
                    .then(if (isSelected) Modifier.border(1.dp, colors.primary, FilterShape) else Modifier)
                    .selectable(selected = isSelected, role = Role.Tab, onClick = { onSelect(category) }),
                contentAlignment = Alignment.Center,
            ) {
                HoText(
                    text = label,
                    role = HoTypeRole.BodyMedium,
                    color = if (isSelected) colors.onPrimaryText else colors.onSurface,
                    style = TextStyle(fontSize = 13.sp),
                )
            }
        }
    }
}
