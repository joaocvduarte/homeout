package app.homeout.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoColors
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Category
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.painterResource

private const val City = "Lisbon"

@Composable
fun HomeRoute(holder: HomeStateHolder, timeZone: TimeZone) {
    val state by holder.state.collectAsState()
    HomeScreen(
        state = state,
        timeZone = timeZone,
        onQueryChange = holder::onQueryChange,
        onCategorySelect = holder::onCategorySelect,
    )
}

@Composable
internal fun HomeScreen(
    state: HomeState,
    timeZone: TimeZone,
    onQueryChange: (String) -> Unit,
    onCategorySelect: (Category?) -> Unit,
) {
    val colors = HoTheme.colors
    Column(Modifier.fillMaxSize().background(colors.background)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .windowInsetsPadding(WindowInsets.statusBars)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 6.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Header()
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                HoText(
                    text = "Upcoming near you",
                    role = HoTypeRole.DisplaySmall,
                    style = TextStyle(lineHeight = 36.sp, letterSpacing = 0.5.sp),
                )
                HoText(
                    text = "$City · ${eventsThisWeekLabel(state.eventsThisWeek)}",
                    role = HoTypeRole.BodyMedium,
                    color = colors.onSurfaceVariant,
                )
            }
            SearchField(onQueryChange)
            CategoryFilter(selected = state.category, onSelect = onCategorySelect)
            state.groups.forEach { group ->
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    HoText(
                        text = group.label(),
                        role = HoTypeRole.LabelSmall,
                        color = if (group.isToday) colors.tertiary else colors.onSurfaceVariant,
                    )
                    group.events.forEach { EventRow(it, timeZone) }
                }
            }
        }
        HomeBottomBar()
    }
}

@Composable
private fun Header() {
    val colors = HoTheme.colors
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.background(colors.brand).padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 3.dp)) {
            HoText(
                text = "homeout",
                role = HoTypeRole.HeadlineSmall,
                color = HoColors.Dark.onSurface,
                style = TextStyle(lineHeight = 26.sp, letterSpacing = 2.sp),
            )
        }
        Box(Modifier.weight(1f))
        Box(Modifier.size(44.dp), contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(HoIcons.Bell),
                contentDescription = "Notifications",
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(colors.onSurface),
            )
        }
        Box(
            Modifier.padding(start = 8.dp).size(36.dp).background(colors.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            HoText("MA", HoTypeRole.LabelLarge, color = colors.onSecondaryContainer, style = TextStyle(fontSize = 13.sp))
        }
    }
}

@Composable
private fun SearchField(onQueryChange: (String) -> Unit) {
    val colors = HoTheme.colors
    val text = rememberTextFieldState()
    LaunchedEffect(text) {
        snapshotFlow { text.text.toString() }.collect(onQueryChange)
    }
    val shape = RoundedCornerShape(8.dp)
    BasicTextField(
        state = text,
        modifier = Modifier.fillMaxWidth().height(44.dp).testTag("search"),
        lineLimits = TextFieldLineLimits.SingleLine,
        textStyle = HoTheme.typography.style(HoTypeRole.BodyMedium).copy(color = colors.onSurface),
        cursorBrush = SolidColor(colors.primary),
        decorator = { field ->
            Row(
                modifier = Modifier
                    .background(colors.surface, shape)
                    .border(1.dp, colors.divider, shape)
                    .padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(HoIcons.MagnifyingGlass),
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    colorFilter = ColorFilter.tint(colors.onSurfaceVariant),
                )
                Box(Modifier.weight(1f)) {
                    if (text.text.isEmpty()) {
                        HoText("Search events, venues, artists", HoTypeRole.BodyMedium, color = colors.onSurfaceVariant)
                    }
                    field()
                }
            }
        },
    )
}
