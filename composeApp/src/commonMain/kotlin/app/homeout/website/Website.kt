package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.testTag
import app.homeout.designsystem.HoTheme
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlin.math.roundToInt

/** The page's sections, in order. The nav and every action scroll to one. */
internal enum class Section { Top, Showcase, Events, How, App }

/** Where each [Section] starts inside the scrolling page. Read when an action scrolls, and by the pinned showcase. */
internal class Anchors {
    private val tops = mutableStateListOf(0, 0, 0, 0, 0)

    operator fun get(section: Section): Int = tops[section.ordinal]

    fun set(section: Section, top: Int) {
        if (tops[section.ordinal] != top) tops[section.ordinal] = top
    }
}

private fun Modifier.anchored(anchors: Anchors, section: Section): Modifier =
    onGloballyPositioned { anchors.set(section, it.positionInParent().y.roundToInt()) }

/**
 * The Web edition: one scrolling page with a nav, hero, app showcase, next events, how it works, a
 * get-the-app call to action and a footer. It always uses the design's single look, whatever the
 * visitor's light or dark setting, and offers no buying or accounts: every ticket and download
 * action leads to [Section.App]. With [reduceMotion], anchor scrolls jump.
 */
@Composable
fun Website(viewModel: WebsiteViewModel, timeZone: TimeZone, reduceMotion: Boolean = false, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()
    HoTheme(darkTheme = true) {
        BoxWithConstraints(modifier.fillMaxSize().background(WebsiteColors.page).testTag("website")) {
            val layout = WebsiteLayout(maxWidth, maxHeight)
            val scroll = rememberScrollState()
            val scope = rememberCoroutineScope()
            val anchors = remember { Anchors() }
            val goTo: (Section) -> Unit = { section ->
                scope.launch {
                    val top = anchors[section]
                    if (reduceMotion) scroll.scrollTo(top) else scroll.animateScrollTo(top)
                }
            }
            Column(Modifier.fillMaxSize().verticalScroll(scroll)) {
                HeroSection(layout, goTo, Modifier.anchored(anchors, Section.Top).testTag("section-top"))
                ShowcaseSection(layout, state.events, timeZone, anchors, scroll, Modifier.anchored(anchors, Section.Showcase).testTag("section-showcase"))
                EventsSection(layout, state, timeZone, goTo, Modifier.anchored(anchors, Section.Events).testTag("section-events"))
                StepsSection(layout, Modifier.anchored(anchors, Section.How).testTag("section-how"))
                GetAppSection(layout, goTo, Modifier.anchored(anchors, Section.App).testTag("section-app"))
                WebsiteFooter(layout, goTo, Modifier.testTag("website-footer"))
            }
            WebsiteNav(layout, goTo, Modifier.align(Alignment.TopStart).testTag("website-nav"))
        }
    }
}
