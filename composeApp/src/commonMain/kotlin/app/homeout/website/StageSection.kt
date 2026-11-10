package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoFlower
import app.homeout.designsystem.HoFlowerPart
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoTypeRole
import kotlinx.datetime.TimeZone

private val cardRadius = 28.dp

/**
 * The website's first screen and its swipe into the next events. The page opens on a full-screen
 * poster, the first event's, with the promise and two actions over it. One swipe shrinks the poster
 * into the middle card, lifts the words away, slides the next two events in beside it and brings
 * in the heading; swiping back up plays it in reverse. It is two viewports tall and its content stays
 * pinned to the viewport while the page scrolls through it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun StageSection(
    layout: WebsiteLayout,
    events: List<WebsiteEvent>,
    timeZone: TimeZone,
    goTo: (Section) -> Unit,
    modifier: Modifier = Modifier,
) {
    val motion = LocalSiteMotion.current
    val on = rememberStageOn(motion)
    val frame = rememberStageFrame(on, motion?.reduce ?: true)
    val current by rememberUpdatedState(on)
    // The words and the heading take no taps while hidden, as the design's visibility and pointer-events do.
    val copyShown by remember(frame) { derivedStateOf { !(current && frame.copyGone) } }
    val headShown by remember(frame) { derivedStateOf { current || frame.headAlpha > 0f } }

    val center = events.getOrNull(0)
    val background = center?.let { Color(it.event.poster.background) } ?: WebsiteColors.page
    val content = center?.let { Color(it.event.poster.content) } ?: WebsiteColors.ink
    val cardWidth = layout.cardWidth
    val cardHeight = layout.cardHeight
    val cardX = (layout.width - cardWidth) / 2
    val cardY = layout.cardCenterY - cardHeight / 2
    val slack = layout.height

    Box(
        modifier
            .fillMaxWidth()
            .height(layout.height * 2)
            .background(WebsiteColors.lightSection)
            .semantics { stateDescription = if (on) "on" else "off" },
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(layout.height)
                .graphicsLayer { translationY = (motion?.scroll?.value ?: 0).coerceIn(0, slack.roundToPx()).toFloat() }
                .clipToBounds(),
        ) {
            if (headShown) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .offset(y = cardY - layout.headHeight)
                        .padding(horizontal = 16.dp)
                        .graphicsLayer {
                            alpha = frame.headAlpha
                            translationY = frame.headOffset * density
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    MonoText("This week in Lisbon", 13.dp, WebsiteColors.kicker, letterSpacing = 0.1f)
                    SiteText(
                        "Next events", HoTypeRole.DisplayLarge, layout.clamp(44f, 6f, 84f), WebsiteColors.darkInk,
                        lineHeight = 0.9f, letterSpacing = 0.01f, align = TextAlign.Center,
                    )
                    PillButton(
                        "See all events", { goTo(Section.Events) }, WebsiteColors.darkInk, WebsiteColors.lightSection,
                        modifier = Modifier.padding(top = 6.dp), height = 44.dp, horizontalPadding = 20.dp, textSize = 15.dp,
                    )
                }
            }

            listOf(events.getOrNull(1) to -1f, events.getOrNull(2) to 1f).forEach { (item, side) ->
                if (item != null) {
                    StageCard(
                        item, timeZone, layout, filled = true,
                        modifier = Modifier
                            .offset(cardX, cardY)
                            .size(cardWidth, cardHeight)
                            .graphicsLayer {
                                alpha = frame.sideAlpha
                                translationX = side * ((cardWidth + layout.cardGap).toPx() - frame.sideShift * density)
                                scaleX = frame.sideScale
                                scaleY = frame.sideScale
                            },
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        val left = cardX.toPx()
                        val top = cardY.toPx()
                        shape = StageClip(frame.shrink, Rect(left, top, left + cardWidth.toPx(), top + cardHeight.toPx()), cardRadius.toPx())
                        clip = true
                    }
                    .background(background),
            ) {
                Box(Modifier.fillMaxWidth().height(340.dp).graphicsLayer { alpha = frame.floraAlpha }) {
                    HoFlower(HoFlowerPart.Left, Modifier.align(Alignment.TopStart).width(layout.clamp(160f, 24f, 360f)))
                    HoFlower(HoFlowerPart.Mid, Modifier.align(Alignment.TopCenter).width(layout.clamp(200f, 30f, 440f)))
                }
                if (center != null) {
                    StageCard(center, timeZone, layout, filled = false, modifier = Modifier.offset(cardX, cardY).size(cardWidth, cardHeight))
                }
            }
            Box(
                Modifier
                    .offset(cardX, cardY)
                    .size(cardWidth, cardHeight)
                    .graphicsLayer { alpha = frame.frameAlpha }
                    .border(2.dp, content.copy(alpha = 0.75f), RoundedCornerShape(cardRadius)),
            )

            if (copyShown) {
                Wrap(
                    layout,
                    Modifier
                        .padding(top = (layout.height * 0.14f).coerceIn(96.dp, 140.dp))
                        .graphicsLayer {
                            alpha = frame.copyAlpha
                            translationY = frame.copyOffset * density
                        },
                ) {
                    Column(Modifier.widthIn(max = 470.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            val wordmark = layout.clamp(56f, 8.5f, 120f)
                            Wordmark(
                                size = wordmark,
                                padding = PaddingValues(start = wordmark * 0.14f, end = wordmark * 0.14f, top = wordmark * 0.02f, bottom = wordmark * 0.06f),
                                letterSpacing = 0.04f,
                                lineHeight = 0.95f,
                            )
                            SiteText(
                                "Head out tonight.", HoTypeRole.TitleLarge, layout.clamp(28f, 3.4f, 46f), content,
                                lineHeight = 1.1f, letterSpacing = -0.02f,
                            )
                        }
                        SiteText(
                            "Fado in candlelit cisterns, vinyl on the rooftops, night markets by the river. " +
                                "homeout finds what's on in Lisbon and keeps every ticket in your pocket.",
                            HoTypeRole.BodyLarge, layout.clamp(16f, 1.4f, 19f), content, lineHeight = 1.5f,
                        )
                        FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            PillButton("Download the app", { goTo(Section.App) }, content, background)
                            PillButton("See what's on", { goTo(Section.Events) }, Color.Transparent, content, outline = content)
                        }
                    }
                }
            }
        }
    }
}

/** The poster's clip: the whole screen at 0, the middle card, rounded, at 1. */
private class StageClip(private val shrink: Float, private val card: Rect, private val radius: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        fun lerp(from: Float, to: Float) = from + (to - from) * shrink
        return Outline.Rounded(
            RoundRect(
                left = lerp(0f, card.left),
                top = lerp(0f, card.top),
                right = lerp(size.width, card.right),
                bottom = lerp(size.height, card.bottom),
                cornerRadius = CornerRadius(radius * shrink),
            ),
        )
    }
}

/** An event as a poster card. The middle one is not [filled]: the stage's own background is its poster. */
@Composable
private fun StageCard(item: WebsiteEvent, timeZone: TimeZone, layout: WebsiteLayout, filled: Boolean, modifier: Modifier) {
    val event = item.event
    val content = Color(event.poster.content)
    val shape = RoundedCornerShape(cardRadius)
    Box(
        modifier
            .then(if (filled) Modifier.background(Color(event.poster.background), shape) else Modifier)
            .clip(shape),
    ) {
        HoPosterFlower(Modifier.align(Alignment.TopEnd).fillMaxWidth(0.62f))
        Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.SpaceBetween) {
            SiteText(
                item.kicker(timeZone), HoTypeRole.LabelLarge, 12.dp, content,
                modifier = Modifier
                    .border(1.dp, content, RoundedCornerShape(percent = 50))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            )
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SiteText(
                    event.title, HoTypeRole.HeadlineLarge, layout.clamp(26f, 2.6f, 36f), content,
                    lineHeight = 0.92f, letterSpacing = 0.01f,
                )
                Row(
                    Modifier.fillMaxWidth().topRule(content).padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MonoText(event.websiteDate(timeZone), 11.dp, content, Modifier.weight(1f), letterSpacing = 0.04f)
                    MonoText(event.venue, 11.dp, content, letterSpacing = 0.04f)
                }
            }
        }
    }
}
