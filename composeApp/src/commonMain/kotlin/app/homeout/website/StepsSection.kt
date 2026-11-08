package app.homeout.website

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.homeout.designsystem.HoTypeRole

private class Step(val number: String, val title: String, val body: String, val background: Color, val ink: Color)

private val steps = listOf(
    Triple("01", "Find your night", "Music, food and more across Lisbon, sorted by day. Filter by what you are in the mood for."),
    Triple("02", "Buy in seconds", "Pick how many tickets and pay with MB WAY, card, Apple Pay or Google Wallet. Free events just need a reservation."),
    Triple("03", "Walk straight in", "Your tickets wait in the app, grouped by event. Swipe between QR codes and show each one at the door."),
).mapIndexed { i, (number, title, body) -> Step(number, title, body, WebsiteColors.steps[i].first, WebsiteColors.steps[i].second) }

/** How it works: the headline and the three steps, in 3 columns or stacked. */
@Composable
internal fun StepsSection(layout: WebsiteLayout, modifier: Modifier = Modifier) {
    Box(modifier.fillMaxWidth().background(WebsiteColors.lightSection).padding(bottom = layout.clamp(80f, 12f, 140f))) {
        Wrap(layout) {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(40.dp)) {
                SiteText(
                    "From “what's on?” to through the door in three taps.", HoTypeRole.TitleLarge, layout.clamp(34f, 5f, 64f),
                    WebsiteColors.darkInk, Modifier.widthIn(max = 820.dp), lineHeight = 1.05f, letterSpacing = -0.02f,
                )
                Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    steps.chunked(layout.stepColumns).forEach { row ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                            row.forEach { StepCard(it, Modifier.weight(1f)) }
                            repeat(layout.stepColumns - row.size) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepCard(step: Step, modifier: Modifier) {
    Column(
        modifier.heightIn(min = 340.dp).background(step.background, RoundedCornerShape(28.dp)).padding(28.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        SiteText(step.number, HoTypeRole.DisplayLarge, 72.dp, step.ink, lineHeight = 0.8f)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            SiteText(step.title, HoTypeRole.TitleLarge, 26.dp, step.ink, letterSpacing = -0.01f)
            SiteText(step.body, HoTypeRole.BodyLarge, 16.dp, step.ink.copy(alpha = 0.92f), lineHeight = 1.5f)
        }
    }
}
