package app.homeout.getapp

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.EventDetail
import app.homeout.event.EventSummary
import app.homeout.event.RoundButton
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

/** Shows the event its ViewModel found, or goes back when there is none (a typed Web URL, say). */
@Composable
fun GetAppRoute(viewModel: GetAppViewModel, timeZone: TimeZone, onBack: () -> Unit) {
    val state by viewModel.state.collectAsState()
    when (val current = state) {
        GetAppState.Loading -> Unit
        GetAppState.Missing -> LaunchedEffect(Unit) { onBack() }
        is GetAppState.Found -> GetAppScreen(current.detail, timeZone, onBack)
    }
}

@Composable
internal fun GetAppScreen(detail: EventDetail, timeZone: TimeZone, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val colors = HoTheme.colors
    Column(
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .testTag("getapp-page")
            .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
            RoundButton(HoIcons.ArrowLeft, "Back to event", onBack, colors.glass, colors.divider, colors.onSurface, 22.dp)
            HoText(
                "Get the app",
                HoTypeRole.DisplaySmall,
                modifier = Modifier.semantics { heading() },
                style = TextStyle(fontSize = 34.sp, lineHeight = 34.sp, letterSpacing = 0.5.sp),
            )
        }
        EventSummary(detail, timeZone)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            HoText("Tickets live in the homeout app", HoTypeRole.TitleLarge)
            HoText(
                "Download the app to buy tickets for this event and keep each ticket's QR code on your phone.",
                HoTypeRole.BodyLarge,
                color = colors.onSurfaceVariant,
                style = TextStyle(fontSize = 15.sp, lineHeight = 23.sp),
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Placeholders: the store links come later, so neither badge goes anywhere yet.
            StoreBadge(HoIcons.AppleLogoFill, "Download on the", "App Store", "Download on the App Store", Modifier.testTag("badge-app-store"))
            StoreBadge(HoIcons.GooglePlayLogoFill, "GET IT ON", "Google Play", "Get it on Google Play", Modifier.testTag("badge-google-play"))
        }
    }
}

/** A black store badge: the store's logo, a small line and the store's name, in white on black in both themes. */
@Composable
private fun StoreBadge(icon: DrawableResource, small: String, store: String, description: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(10.dp)
    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.Black, shape)
            .border(1.dp, HoTheme.colors.fieldBorder, shape)
            .semantics(mergeDescendants = true) { contentDescription = description }
            .clickable(role = Role.Button, onClick = {}),
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(painterResource(icon), contentDescription = null, modifier = Modifier.size(28.dp), colorFilter = ColorFilter.tint(Color.White))
        Column {
            HoText(small, HoTypeRole.LabelMedium, color = Color.White, style = TextStyle(fontSize = 11.sp, lineHeight = 12.sp, letterSpacing = 0.4.sp))
            HoText(store, HoTypeRole.TitleLarge, color = Color.White, style = TextStyle(fontSize = 20.sp, lineHeight = 22.sp, fontWeight = FontWeight.Medium))
        }
    }
}
