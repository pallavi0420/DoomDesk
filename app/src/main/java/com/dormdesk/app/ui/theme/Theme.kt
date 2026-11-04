package com.dormdesk.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EspressoBrown,
    onPrimary = SoftWhite,
    primaryContainer = NeutralBorder,
    onPrimaryContainer = EspressoBrown,
    secondary = MutedTerracotta,
    onSecondary = SoftWhite,
    secondaryContainer = TerracottaContainer,
    onSecondaryContainer = EspressoBrown,
    tertiary = SageGreen,
    onTertiary = SoftWhite,
    tertiaryContainer = SageContainer,
    onTertiaryContainer = DeepCharcoal,
    background = WarmIvory,
    onBackground = DeepCharcoal,
    surface = SoftWhite,
    onSurface = DeepCharcoal,
    surfaceVariant = NeutralSurface,
    onSurfaceVariant = EspressoLight,
    outline = NeutralBorder,
    outlineVariant = NeutralDivider
)

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaSoft,
    onPrimary = DarkBackground,
    primaryContainer = DarkSurfaceVariant,
    onPrimaryContainer = DarkTextPrimary,
    secondary = MutedTerracotta,
    onSecondary = DarkBackground,
    secondaryContainer = DarkSurfaceVariant,
    onSecondaryContainer = TerracottaSoft,
    tertiary = SageGreen,
    onTertiary = DarkBackground,
    tertiaryContainer = DarkSurfaceVariant,
    onTertiaryContainer = SageGreen,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorder
)

@Composable
fun DormDeskTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
