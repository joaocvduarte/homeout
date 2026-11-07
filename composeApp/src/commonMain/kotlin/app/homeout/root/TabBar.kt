package app.homeout.root

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.tickets.ticketCountLabel

/** Home, Tickets and Profile. Profile has no screen yet, so it takes no taps. */
@Composable
internal fun TabBar(selected: Tab, ticketCount: Int, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(top = 6.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets(bottom = 24.dp)))
            .selectableGroup(),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        TabItem("Home", selected == Tab.HOME, { s, f -> HoGlyphs.home(s, f) }, onClick = { onSelect(Tab.HOME) })
        TabItem("Tickets", selected == Tab.TICKETS, { s, f -> HoGlyphs.tickets(s, f) }, onClick = { onSelect(Tab.TICKETS) }, badge = ticketCount)
        TabItem("Profile", false, { s, f -> HoGlyphs.profile(s, f) }, onClick = null)
    }
}

@Composable
private fun TabItem(
    label: String,
    active: Boolean,
    glyph: (stroke: Color, fill: Color) -> ImageVector,
    onClick: (() -> Unit)?,
    badge: Int = 0,
) {
    val colors = HoTheme.colors
    val tint = if (active) colors.onPrimaryText else colors.onSurfaceVariant
    val fill = if (active) colors.primaryPressed else Color.Transparent
    val icon = remember(tint, fill) { glyph(tint, fill) }
    Box(
        Modifier
            .width(80.dp)
            .testTag("tab-$label")
            .then(
                if (onClick != null) Modifier.selectable(selected = active, role = Role.Tab, onClick = onClick) else Modifier,
            )
            .padding(top = 2.dp, bottom = 4.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Box(Modifier.height(32.dp), contentAlignment = Alignment.Center) {
                Image(icon, contentDescription = null, Modifier.size(24.dp))
            }
            HoText(label, HoTypeRole.BodySmall, color = tint, style = TextStyle(fontSize = 11.sp, letterSpacing = 0.sp))
        }
        if (badge > 0) {
            val badgeText = ticketCountLabel(badge)
            Box(
                Modifier
                    .offset(x = 17.dp, y = (-2).dp)
                    .defaultMinSize(minWidth = 18.dp)
                    .height(18.dp)
                    .background(colors.tertiary, RoundedCornerShape(9.dp))
                    .padding(horizontal = 5.dp)
                    .testTag("tickets-badge")
                    .semantics { contentDescription = badgeText },
                contentAlignment = Alignment.Center,
            ) {
                HoText(
                    badge.toString(),
                    HoTypeRole.BodySmall,
                    color = colors.onTertiary,
                    style = TextStyle(fontSize = 11.sp, lineHeight = 14.sp, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
                )
            }
        }
    }
}
