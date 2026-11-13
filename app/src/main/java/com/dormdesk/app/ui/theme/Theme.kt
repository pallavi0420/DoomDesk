package com.dormdesk.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = EspressoBrown,
    onPrimary = SoftWhite,
    primaryContainer = NeutralLightSurface,
    onPrimaryContainer = EspressoBrown,
    secondary = MutedTerracotta,
    onSecondary = SoftWhite,
    secondaryContainer = Color(0xFFF4E9E4),
    onSecondaryContainer = EspressoBrown,
    tertiary = SageGreen,
    onTertiary = SoftWhite,
    background = WarmIvory,
    onBackground = EspressoBrown,
    surface = SoftWhite,
    onSurface = EspressoBrown,
    surfaceVariant = NeutralLightSurface,
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    outlineVariant = WarmTaupe
)

private val DarkColorScheme = darkColorScheme(
    primary = SoftWhite,
    onPrimary = DeepCharcoal,
    primaryContainer = NeutralDarkCard,
    onPrimaryContainer = SoftWhite,
    secondary = MutedTerracotta,
    onSecondary = SoftWhite,
    secondaryContainer = Color(0xFF473630),
    onSecondaryContainer = SoftWhite,
    tertiary = SageGreen,
    onTertiary = SoftWhite,
    background = DeepCharcoal,
    onBackground = WarmIvory,
    surface = NeutralDarkCard,
    onSurface = WarmIvory,
    surfaceVariant = NeutralDarkSurface,
    onSurfaceVariant = TextMutedLight,
    outline = BorderDark,
    outlineVariant = WarmTaupe
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
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
