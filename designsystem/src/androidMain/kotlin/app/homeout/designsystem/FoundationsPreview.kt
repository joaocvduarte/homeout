package app.homeout.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/** Dev-only catalog of the design foundations, laid out like the UI Library and Button Set pages. */
@Composable
internal fun FoundationsCatalog() {
    val colors = HoTheme.colors
    Column(
        modifier = Modifier.background(colors.background).padding(22.dp),
        verticalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        Swatches()
        TypeScale()
        ButtonSet()
    }
}

private val swatchTokens: List<Triple<String, String, (HoColors) -> Color>> = listOf(
    Triple("Background", "background", { it.background }),
    Triple("Surface", "surface", { it.surface }),
    Triple("On surface", "onSurface", { it.onSurface }),
    Triple("Muted", "onSurfaceVariant", { it.onSurfaceVariant }),
    Triple("Outline", "outline", { it.outline }),
    Triple("Primary", "primary", { it.primary }),
    Triple("Primary text", "onPrimaryText", { it.onPrimaryText }),
    Triple("Tonal", "secondaryContainer", { it.secondaryContainer }),
    Triple("On tonal", "onSecondaryContainer", { it.onSecondaryContainer }),
    Triple("Brand teal", "brand", { it.brand }),
    Triple("Highlight", "tertiary", { it.tertiary }),
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Swatches() {
    val colors = HoTheme.colors
    FlowRow(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        swatchTokens.forEach { (name, code, pick) ->
            Column(Modifier.width(150.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    Modifier.fillMaxWidth().height(48.dp)
                        .background(pick(colors), RoundedCornerShape(8.dp))
                        .border(1.dp, colors.outline, RoundedCornerShape(8.dp)),
                )
                HoText(name, HoTypeRole.LabelLarge)
                HoText("colors.$code", HoTypeRole.LabelMedium, color = colors.onSurfaceVariant)
            }
        }
    }
}

private val samples = mapOf(
    "Display" to "Head out tonight",
    "Headline" to "Upcoming near you",
    "Title" to "Fado at the Old Cistern",
    "Body" to "Doors open at 21:00. Show your ticket QR code at the entrance.",
    "LabelLarge" to "Get tickets",
    "LabelMedium" to "Alfama · 21:30",
    "LabelSmall" to "Tonight · Fri 14 Nov",
)

@Composable
internal fun TypeScale() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HoTypeRole.entries.forEach { role ->
            val sample = samples[role.name] ?: samples.entries.first { role.name.startsWith(it.key) }.value
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.width(160.dp)) {
                    HoText(role.name, HoTypeRole.LabelLarge)
                    HoText(
                        "${role.sizeSp}/${role.lineHeightSp} · ${role.weight.weight}",
                        HoTypeRole.LabelMedium,
                        color = HoTheme.colors.onSurfaceVariant,
                    )
                }
                HoText(sample, role, maxLines = 1)
            }
        }
    }
}

@Composable
internal fun ButtonSet() {
    val rows = listOf(
        Triple(HoButtonVariant.Primary, "Get tickets", HoIcons.Ticket to HoIcons.Plus),
        Triple(HoButtonVariant.Secondary, "Save event", HoIcons.BookmarkSimple to HoIcons.Heart),
        Triple(HoButtonVariant.Outline, "View details", HoIcons.ArrowRight to HoIcons.ShareNetwork),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        rows.forEach { (variant, label, icons) ->
            HoText(variant.name, HoTypeRole.LabelSmall, color = HoTheme.colors.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HoButton(label, {}, variant = variant, icon = icons.first)
                HoButton("Pressed", {}, variant = variant, interactionSource = AlwaysPressed)
                HoButton("Disabled", {}, variant = variant, enabled = false)
                HoIconButton(icons.second, variant.name, {}, variant = variant)
            }
        }
        HoText("Sizes", HoTypeRole.LabelSmall, color = HoTheme.colors.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            HoButton("Large · 52", {}, size = HoButtonSize.Large)
            HoButton("Medium · 44", {}, size = HoButtonSize.Medium)
        }
        HoButton("Block · Get tickets", {}, size = HoButtonSize.Large, block = true)
    }
}

/** Holds a button in its pressed look for the catalog. */
private object AlwaysPressed : MutableInteractionSource {
    override val interactions: Flow<Interaction> = flowOf(PressInteraction.Press(Offset.Zero))
    override suspend fun emit(interaction: Interaction) = Unit
    override fun tryEmit(interaction: Interaction) = true
}

@Preview(widthDp = 600, heightDp = 1500)
@Composable
private fun FoundationsDarkPreview() = HoTheme(darkTheme = true) { FoundationsCatalog() }

@Preview(widthDp = 600, heightDp = 1500)
@Composable
private fun FoundationsLightPreview() = HoTheme(darkTheme = false) { FoundationsCatalog() }
