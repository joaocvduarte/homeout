package app.homeout.buy

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.HoButton
import app.homeout.designsystem.HoButtonSize
import app.homeout.designsystem.HoGlyphs
import app.homeout.designsystem.HoIcons
import app.homeout.designsystem.HoPosterFlower
import app.homeout.designsystem.HoText
import app.homeout.designsystem.HoTheme
import app.homeout.designsystem.HoTypeRole
import app.homeout.domain.PaymentMethod
import app.homeout.event.RoundButton
import app.homeout.event.fullVenue
import app.homeout.event.posterDate
import app.homeout.tickets.label
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone

/** Collects the Buy state; [onPaid] receives the confirmation once the order is placed. */
@Composable
fun BuyRoute(viewModel: BuyViewModel, timeZone: TimeZone, onBack: () -> Unit, onPaid: (String) -> Unit) {
    val state by viewModel.state.collectAsState()
    val scope = rememberCoroutineScope()
    var paying by remember { mutableStateOf(false) }
    state?.let {
        BuyScreen(
            state = it,
            timeZone = timeZone,
            onBack = onBack,
            onAdd = viewModel::add,
            onRemove = viewModel::remove,
            onSelect = viewModel::select,
            onPay = {
                if (!paying) {
                    paying = true
                    scope.launch { onPaid(viewModel.pay()) }
                }
            },
        )
    }
}

@Composable
internal fun BuyScreen(
    state: BuyState,
    timeZone: TimeZone,
    onBack: () -> Unit,
    onAdd: () -> Unit,
    onRemove: () -> Unit,
    onSelect: (PaymentMethod) -> Unit,
    onPay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = HoTheme.colors
    Column(modifier.fillMaxSize()) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .testTag("buy-page")
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(22.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.CenterVertically) {
                RoundButton(HoIcons.ArrowLeft, "Back to event", onBack, colors.glass, colors.divider, colors.onSurface, 22.dp)
                HoText(
                    "Buy tickets",
                    HoTypeRole.DisplaySmall,
                    modifier = Modifier.semantics { heading() },
                    style = TextStyle(fontSize = 34.sp, lineHeight = 34.sp, letterSpacing = 0.5.sp),
                )
            }
            Summary(state, timeZone)
            Counter(state, onAdd, onRemove)
            if (state.free) {
                HoText(
                    "This event is free. No payment needed: your tickets go straight to the Tickets tab.",
                    HoTypeRole.BodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                )
            } else {
                Methods(state.method, onSelect)
            }
        }
        TotalBar(state, onPay)
    }
}

@Composable
private fun Summary(state: BuyState, timeZone: TimeZone) {
    val colors = HoTheme.colors
    val event = state.detail.event
    Row(
        Modifier.fillMaxWidth().background(colors.surface, RoundedCornerShape(8.dp)).padding(10.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(56.dp).clip(RoundedCornerShape(6.dp)).background(Color(event.poster.background))) {
            HoPosterFlower(Modifier.align(Alignment.TopEnd).offset(x = 8.dp).width(64.dp))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            HoText(event.title, HoTypeRole.TitleMedium, style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp), maxLines = 1)
            HoText(event.posterDate(timeZone), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp))
            HoText(state.detail.fullVenue(), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 13.sp, lineHeight = 18.sp), maxLines = 1)
        }
    }
}

@Composable
private fun Counter(state: BuyState, onAdd: () -> Unit, onRemove: () -> Unit) {
    val colors = HoTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Tickets")
        Row(
            Modifier
                .fillMaxWidth()
                .border(1.dp, colors.divider, RoundedCornerShape(8.dp))
                .padding(start = 16.dp, end = 14.dp, top = 14.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                HoText("General admission", HoTypeRole.TitleMedium, style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp))
                HoText(state.detail.event.price.eachLabel(), HoTypeRole.BodyMedium, color = colors.onSurfaceVariant, style = TextStyle(fontSize = 13.sp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                StepButton(HoGlyphs.Minus, "Remove a ticket", state.canRemove, Color.Transparent, colors.divider, colors.onSurface, onRemove)
                HoText(
                    state.quantity.toString(),
                    HoTypeRole.TitleLarge,
                    modifier = Modifier
                        .width(32.dp)
                        .testTag("quantity")
                        .semantics {
                            contentDescription = "Number of tickets"
                            liveRegion = LiveRegionMode.Polite
                        },
                    style = TextStyle(lineHeight = 24.sp, textAlign = TextAlign.Center),
                )
                StepButton(HoGlyphs.Plus, "Add a ticket", state.canAdd, colors.secondaryContainer, colors.secondaryContainer, colors.onSecondaryContainer, onAdd)
            }
        }
        HoText("Up to 8 tickets per order.", HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
    }
}

@Composable
private fun StepButton(glyph: ImageVector, label: String, enabled: Boolean, fill: Color, border: Color, tint: Color, onClick: () -> Unit) {
    Box(
        Modifier
            .size(40.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(CircleShape)
            .background(fill, CircleShape)
            .border(1.dp, border, CircleShape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(glyph, contentDescription = label, modifier = Modifier.size(18.dp), colorFilter = ColorFilter.tint(tint))
    }
}

@Composable
private fun Methods(selected: PaymentMethod, onSelect: (PaymentMethod) -> Unit) {
    Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionLabel("Payment method")
        PaymentMethod.entries.forEach { method ->
            MethodCard(method, method == selected, onClick = { onSelect(method) })
        }
    }
}

@Composable
private fun MethodCard(method: PaymentMethod, selected: Boolean, onClick: () -> Unit) {
    val colors = HoTheme.colors
    val shape = RoundedCornerShape(8.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 62.dp)
            .clip(shape)
            .background(if (selected) colors.primaryPressed else Color.Transparent, shape)
            .border(if (selected) 1.5.dp else 1.dp, if (selected) colors.primary else colors.divider, shape)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick)
            .testTag("method-${method.name}")
            .padding(horizontal = 14.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(Modifier.size(40.dp).background(colors.secondaryContainer, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
            Image(method.glyph(), null, Modifier.size(20.dp), colorFilter = ColorFilter.tint(colors.onSecondaryContainer))
        }
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            HoText(method.label(), HoTypeRole.TitleMedium, style = TextStyle(fontSize = 15.sp, lineHeight = 20.sp))
            HoText(method.note(), HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
        }
        RadioMark(selected)
    }
}

/** The design's native radio, drawn in the accent color. */
@Composable
private fun RadioMark(selected: Boolean) {
    val colors = HoTheme.colors
    val ring = if (selected) colors.primary else colors.onSurfaceVariant
    Box(Modifier.size(20.dp).border(2.dp, ring, CircleShape), contentAlignment = Alignment.Center) {
        if (selected) Box(Modifier.size(10.dp).background(colors.primary, CircleShape))
    }
}

private fun PaymentMethod.glyph(): ImageVector = when (this) {
    PaymentMethod.MB_WAY -> HoGlyphs.Phone
    PaymentMethod.CARD -> HoGlyphs.Card
    PaymentMethod.APPLE_PAY, PaymentMethod.GOOGLE_WALLET -> HoGlyphs.Wallet
}

@Composable
private fun SectionLabel(text: String) {
    HoText(text, HoTypeRole.LabelSmall, color = HoTheme.colors.onSurfaceVariant, modifier = Modifier.semantics { heading() })
}

@Composable
private fun TotalBar(state: BuyState, onPay: () -> Unit) {
    val colors = HoTheme.colors
    val price = state.detail.event.price
    Row(
        Modifier
            .fillMaxWidth()
            .background(colors.surface)
            .drawBehind { drawLine(colors.divider, Offset(0f, 0f), Offset(size.width, 0f), 1.dp.toPx()) }
            .testTag("total-bar")
            .padding(start = 20.dp, end = 20.dp, top = 12.dp)
            .windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets(bottom = 28.dp))),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
            HoText(totalCaption(state.quantity), HoTypeRole.BodySmall, color = colors.onSurfaceVariant)
            HoText(price.totalLabel(state.quantity), HoTypeRole.TitleLarge, style = TextStyle(lineHeight = 24.sp))
        }
        HoButton(
            text = price.payLabel(state.quantity),
            onClick = onPay,
            modifier = Modifier.weight(1f),
            size = HoButtonSize.Large,
            block = true,
            tinted = true,
        )
    }
}
