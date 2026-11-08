package app.homeout.website

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first

private val revealEasing = CubicBezierEasing(0.2f, 0.8f, 0.2f, 1f)

/**
 * Starts hidden (transparent, 36 dp low) and, once its top passes 92% of the viewport, fades and
 * rises over 700 ms after [delayMillis], once. With reduced motion, or outside the website, it is always shown.
 */
@Composable
internal fun Modifier.reveal(delayMillis: Int = 0): Modifier {
    val motion = LocalSiteMotion.current
    if (motion == null || motion.reduce) return this
    var top by remember { mutableFloatStateOf(Float.MAX_VALUE) }
    var revealed by remember { mutableStateOf(false) }
    LaunchedEffect(motion) {
        snapshotFlow { top - motion.scroll.value < motion.viewportPx * 0.92f }.first { it }
        revealed = true
    }
    val shown by animateFloatAsState(
        targetValue = if (revealed) 1f else 0f,
        animationSpec = tween(700, delayMillis = delayMillis, easing = revealEasing),
        label = "reveal",
    )
    val done by remember { derivedStateOf { shown >= 1f } }
    return this
        .semantics { stateDescription = if (done) "revealed" else "hidden" }
        .onGloballyPositioned { top = it.positionInRoot().y + motion.scroll.value }
        .graphicsLayer {
            alpha = shown
            translationY = (1f - shown) * 36.dp.toPx()
        }
}
