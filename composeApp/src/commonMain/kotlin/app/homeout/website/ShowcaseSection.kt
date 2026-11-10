package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.Event
import kotlinx.datetime.TimeZone

/** The design's three fanned posters come from the 3rd, 4th and 1st events. */
private val fanOrder = listOf(2, 3, 0)

/** The poster fan's fixed size in the design, before [WebsiteLayout.fanScale]. */
private val fanWidth = 340.dp
private val fanHeight = 420.dp

/**
 * The teal panel with the app's posters fanned out. It is 1.7 viewports tall and its content stays
 * pinned to the viewport while the page scrolls through it. One swipe plays it all: once the section
 * is on its way in, the panel opens, the posters rise and fan out and the words arrive. It plays back
 * when the page scrolls up past its start.
 */
@Composable
internal fun ShowcaseSection(
    layout: WebsiteLayout,
    events: List<WebsiteEvent>,
    timeZone: TimeZone,
    anchors: Anchors,
    modifier: Modifier = Modifier,
) {
    val motion = LocalSiteMotion.current
    val reduce = motion?.reduce ?: true
    val scroll = motion?.scroll
    val sectionHeight = layout.height * 1.7f
    val slack = sectionHeight - layout.height
    val on = rememberShowcaseOn(motion) { anchors[Section.Showcase] }
    val frame = rememberShowcaseFrame(on, reduce)
    val posters = fanOrder.mapNotNull { events.getOrNull(it)?.event }
    Box(modifier.fillMaxWidth().height(sectionHeight).semantics { stateDescription = if (on) "on" else "off" }) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(layout.height)
                .graphicsLayer {
                    val pin = ((scroll?.value ?: 0) - anchors[Section.Showcase]).coerceIn(0, slack.roundToPx().coerceAtLeast(0))
                    translationY = pin.toFloat()
                }
                .clipToBounds(),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                Modifier.matchParentSize().graphicsLayer {
                    scaleX = frame.panelScale
                    scaleY = frame.panelScale
                    shape = RoundedCornerShape(frame.panelRadius.dp)
                    clip = true
                }.background(WebsiteColors.panel),
            )
            Wrap(layout) {
                ShowcaseContent(layout, posters, timeZone, frame)
            }
        }
    }
}

@Composable
private fun ShowcaseContent(layout: WebsiteLayout, posters: List<Event>, timeZone: TimeZone, frame: ShowcaseFrame) {
    val gap = layout.clamp(24f, 6f, 96f)
    BoxWithConstraints(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        val available = maxWidth
        val fanSize = fanWidth * layout.fanScale
        val sideBySide = available >= 320.dp + gap + fanSize
        val copy = @Composable { modifier: Modifier ->
            ShowcaseCopy(
                layout,
                modifier.graphicsLayer {
                    alpha = frame.copyAlpha
                    translationY = frame.copyOffset * density
                },
            )
        }
        if (sideBySide) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap), verticalAlignment = Alignment.CenterVertically) {
                copy(Modifier.widthIn(max = (available - gap - fanSize).coerceAtMost(480.dp)))
                PosterFan(layout, posters, timeZone, frame)
            }
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(gap)) {
                copy(Modifier.widthIn(max = 480.dp))
                PosterFan(layout, posters, timeZone, frame)
            }
        }
    }
}

@Composable
private fun ShowcaseCopy(layout: WebsiteLayout, modifier: Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(0.dp)) {
        MonoText("One app", 13.dp, WebsiteColors.darkInk, Modifier.padding(bottom = 14.dp), letterSpacing = 0.1f)
        val size = layout.clamp(48f, 7f, 96f)
        SiteText(
            "Every night out in one place.", HoTypeRole.DisplayLarge, size, WebsiteColors.darkInk,
            lineHeight = 0.92f, letterSpacing = 0.01f,
        )
        SiteText(
            "Browse tonight's events, buy tickets in a few taps, and walk in with the QR code on your phone.",
            HoTypeRole.BodyLarge, 18.dp, WebsiteColors.darkInk,
            modifier = Modifier.padding(top = 18.dp).widthIn(max = 420.dp), lineHeight = 1.5f,
        )
    }
}

/** Three posters at the design's final fan: ±92 apart, ±9° tilted, the sides 22 lower. */
@Composable
private fun PosterFan(layout: WebsiteLayout, posters: List<Event>, timeZone: TimeZone, frame: ShowcaseFrame) {
    val scale = layout.fanScale
    Box(
        Modifier.size(fanWidth * scale, fanHeight * scale).graphicsLayer { translationY = frame.posterRise * density },
        contentAlignment = Alignment.Center,
    ) {
        Box(Modifier.requiredSize(fanWidth, fanHeight).graphicsLayer { scaleX = scale; scaleY = scale }) {
            posters.forEachIndexed { i, event ->
                val k = i - (posters.size - 1) / 2f
                FanPoster(
                    event, timeZone,
                    Modifier
                        .align(Alignment.Center)
                        .graphicsLayer {
                            val card = frame.fan(k)
                            translationX = card.dx * density
                            translationY = card.dy * density
                            rotationZ = card.rotation
                        },
                )
            }
        }
    }
}

@Composable
private fun FanPoster(event: Event, timeZone: TimeZone, modifier: Modifier) {
    val background = Color(event.poster.background)
    val content = Color(event.poster.content)
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier
            .size(230.dp, 300.dp)
            .shadow(30.dp, shape, ambientColor = WebsiteColors.darkInk.copy(alpha = 0.3f), spotColor = WebsiteColors.darkInk.copy(alpha = 0.3f))
            .background(background, shape)
            .clipToBounds(),
    ) {
        // Absolute in the design: it ignores the poster's padding.
        HoPosterFlower(Modifier.align(Alignment.TopEnd).fillMaxWidth(0.65f))
        Column(
            Modifier.fillMaxSize().padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Bottom),
        ) {
            SiteText(event.title, HoTypeRole.HeadlineLarge, 34.dp, content, lineHeight = 0.92f)
            MonoText(event.websiteDate(timeZone), 11.dp, content, Modifier.fillMaxWidth().topRule(content).padding(top = 8.dp))
        }
    }
}
