package app.homeout.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalHoColors = staticCompositionLocalOf { HoColors.Dark }
private val LocalHoTypography = staticCompositionLocalOf<HoTypography> {
    error("HoTypography is only available inside HoTheme")
}

/** Follows the phone's light or dark setting unless [darkTheme] is given. */
@Composable
fun HoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalHoColors provides if (darkTheme) HoColors.Dark else HoColors.Light,
        LocalHoTypography provides rememberHoTypography(),
        content = content,
    )
}

object HoTheme {
    val colors: HoColors
        @Composable @ReadOnlyComposable
        get() = LocalHoColors.current

    val typography: HoTypography
        @Composable @ReadOnlyComposable
        get() = LocalHoTypography.current
}
