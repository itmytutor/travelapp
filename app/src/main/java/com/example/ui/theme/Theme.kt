package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = OceanTealDark,
    onPrimary = OceanTealOnDark,
    primaryContainer = OceanTealContainerDark,
    onPrimaryContainer = OceanTealOnContainerDark,
    secondary = SunsetCoralDark,
    onSecondary = SunsetCoralOnDark,
    secondaryContainer = SunsetCoralContainerDark,
    onSecondaryContainer = SunsetCoralOnContainerDark,
    tertiary = EmeraldDark,
    onTertiary = EmeraldOnDark,
    tertiaryContainer = EmeraldContainerDark,
    onTertiaryContainer = EmeraldOnContainerDark,
    background = MidnightBackgroundDark,
    surface = MidnightSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurface = OnSurfaceDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = OceanTealPrimary,
    onPrimary = OceanTealOnPrimary,
    primaryContainer = OceanTealContainer,
    onPrimaryContainer = OceanTealOnContainer,
    secondary = SunsetCoralSecondary,
    onSecondary = SunsetCoralOnSecondary,
    secondaryContainer = SunsetCoralContainer,
    onSecondaryContainer = SunsetCoralOnContainer,
    tertiary = EmeraldTertiary,
    onTertiary = EmeraldOnTertiary,
    tertiaryContainer = EmeraldContainer,
    onTertiaryContainer = EmeraldOnContainer,
    background = SandBackgroundLight,
    surface = SandSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun WanderListTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent wanderlust branding
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
