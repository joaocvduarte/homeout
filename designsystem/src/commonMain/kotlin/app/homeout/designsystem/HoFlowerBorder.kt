package app.homeout.designsystem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.resources.Res
import app.homeout.designsystem.resources.flowers_top_left
import app.homeout.designsystem.resources.flowers_top_mid
import app.homeout.designsystem.resources.flowers_top_right
import org.jetbrains.compose.resources.painterResource

/**
 * The border of real flowers cut from the homeout logo, flush with the top edge
 * (Home Screen page). Purely decorative: it has no semantics and takes no input.
 */
@Composable
fun HoFlowerBorder(modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().alpha(HoTheme.colors.decorationAlpha)) {
        Image(
            painter = painterResource(Res.drawable.flowers_top_left),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart).width(124.dp).testTag("flowers-left"),
            contentScale = ContentScale.FillWidth,
        )
        Image(
            painter = painterResource(Res.drawable.flowers_top_mid),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart).offset(x = 112.dp).width(176.dp).testTag("flowers-mid"),
            contentScale = ContentScale.FillWidth,
        )
        Image(
            painter = painterResource(Res.drawable.flowers_top_right),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).width(150.dp).testTag("flowers-right"),
            contentScale = ContentScale.FillWidth,
        )
    }
}

/** The top-right flower from the logo on its own, for the corner of an event poster. Purely decorative. */
@Composable
fun HoPosterFlower(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(Res.drawable.flowers_top_right),
        contentDescription = null,
        modifier = modifier.alpha(HoTheme.colors.decorationAlpha).testTag("poster-flower"),
        contentScale = ContentScale.FillWidth,
    )
}
