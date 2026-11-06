package app.homeout.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private val tabs: List<Pair<String, DrawableResource>> = listOf(
    "Home" to HoIcons.HouseFill,
    "Explore" to HoIcons.Compass,
    "Tickets" to HoIcons.Ticket,
    "Profile" to HoIcons.User,
)

/** Home is the only screen so far; the other tabs have no destination yet. */
@Composable
internal fun HomeBottomBar(modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surface)
            .padding(top = 8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets(bottom = 24.dp))),
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        tabs.forEachIndexed { index, (label, icon) ->
            val active = index == 0
            val tint = if (active) colors.onPrimaryText else colors.onSurfaceVariant
            Column(
                modifier = Modifier.width(72.dp).semantics(mergeDescendants = true) { selected = active },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Box(Modifier.height(32.dp), contentAlignment = Alignment.Center) {
                    Image(painterResource(icon), contentDescription = null, Modifier.size(24.dp), colorFilter = ColorFilter.tint(tint))
                }
                HoText(label, HoTypeRole.BodySmall, color = tint, style = TextStyle(fontSize = 11.sp, letterSpacing = 0.sp))
            }
        }
    }
}
