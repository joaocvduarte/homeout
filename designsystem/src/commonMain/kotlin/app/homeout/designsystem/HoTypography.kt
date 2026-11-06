package app.homeout.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import app.homeout.designsystem.resources.Res
import app.homeout.designsystem.resources.big_shoulders_stencil_display_extrabold
import app.homeout.designsystem.resources.inter_medium
import app.homeout.designsystem.resources.inter_regular
import org.jetbrains.compose.resources.Font

enum class HoFont { Stencil, Inter }

/** The type scale from the homeout UI Library page. */
enum class HoTypeRole(
    val font: HoFont,
    val sizeSp: Int,
    val lineHeightSp: Int,
    val weight: FontWeight,
    val letterSpacingEm: Float,
    val uppercase: Boolean,
) {
    DisplayLarge(HoFont.Stencil, 57, 64, FontWeight.ExtraBold, 0.01f, true),
    DisplayMedium(HoFont.Stencil, 45, 52, FontWeight.ExtraBold, 0.01f, true),
    DisplaySmall(HoFont.Stencil, 36, 44, FontWeight.ExtraBold, 0.01f, true),
    HeadlineLarge(HoFont.Stencil, 32, 40, FontWeight.ExtraBold, 0.01f, true),
    HeadlineMedium(HoFont.Stencil, 28, 36, FontWeight.ExtraBold, 0.01f, true),
    HeadlineSmall(HoFont.Stencil, 24, 32, FontWeight.ExtraBold, 0.01f, true),
    TitleLarge(HoFont.Inter, 20, 28, FontWeight.Medium, 0f, false),
    TitleMedium(HoFont.Inter, 16, 24, FontWeight.Medium, 0f, false),
    TitleSmall(HoFont.Inter, 14, 20, FontWeight.Medium, 0f, false),
    BodyLarge(HoFont.Inter, 16, 24, FontWeight.Normal, 0f, false),
    BodyMedium(HoFont.Inter, 14, 20, FontWeight.Normal, 0f, false),
    BodySmall(HoFont.Inter, 12, 16, FontWeight.Normal, 0.01f, false),
    LabelLarge(HoFont.Inter, 14, 20, FontWeight.Medium, 0f, false),
    LabelMedium(HoFont.Inter, 12, 16, FontWeight.Normal, 0.02f, false),
    LabelSmall(HoFont.Inter, 11, 16, FontWeight.Normal, 0.08f, true),
}

@Immutable
class HoTypography internal constructor(
    private val stencil: FontFamily,
    private val inter: FontFamily,
) {
    fun style(role: HoTypeRole): TextStyle = TextStyle(
        fontFamily = if (role.font == HoFont.Stencil) stencil else inter,
        fontWeight = role.weight,
        fontSize = role.sizeSp.sp,
        lineHeight = role.lineHeightSp.sp,
        letterSpacing = if (role.letterSpacingEm == 0f) TextUnit.Unspecified else role.letterSpacingEm.em,
    )
}

@Composable
internal fun rememberHoTypography(): HoTypography = HoTypography(
    stencil = FontFamily(Font(Res.font.big_shoulders_stencil_display_extrabold, FontWeight.ExtraBold)),
    inter = FontFamily(
        Font(Res.font.inter_regular, FontWeight.Normal),
        Font(Res.font.inter_medium, FontWeight.Medium),
    ),
)
