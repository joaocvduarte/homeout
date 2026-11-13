package app.homeout.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.resources.Res
import app.homeout.designsystem.resources.badge_app_store
import app.homeout.designsystem.resources.badge_google_play
import org.jetbrains.compose.resources.painterResource

/** The stores the website's badges point to. */
enum class HoStore(internal val description: String, internal val aspectRatio: Float) {
    AppStore("Download on the App Store", 119.664f / 40f),
    GooglePlay("Get it on Google Play", 238.96f / 70.87f),
}

/**
 * The store's own badge artwork (homeout Website page), 56 dp high with its own width. It takes no
 * input: the caller makes it tappable.
 */
@Composable
fun HoStoreBadge(store: HoStore, modifier: Modifier = Modifier) {
    val resource = when (store) {
        HoStore.AppStore -> Res.drawable.badge_app_store
        HoStore.GooglePlay -> Res.drawable.badge_google_play
    }
    Image(
        painter = painterResource(resource),
        contentDescription = store.description,
        modifier = modifier.height(56.dp).aspectRatio(store.aspectRatio),
        contentScale = ContentScale.FillBounds,
    )
}
