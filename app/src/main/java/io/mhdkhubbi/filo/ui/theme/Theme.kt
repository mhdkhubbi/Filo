package io.mhdkhubbi.filo.ui.theme

import Blue100
import Blue200
import Blue50
import Blue500
import Blue800
import Blue900
import Gray100
import Gray300
import Gray400
import Gray50
import Gray500
import Gray600
import Gray700
import Gray800
import Gray900
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import io.mhdkhubbi.andro.ui.theme.AppTypography

// ui/theme/Theme.kt

private val LightColorScheme = lightColorScheme(    primary = Blue500,
    onPrimary = Color.White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,

    surface = Color.White,
    onSurface = Gray900,


    surfaceVariant = Gray50,         // background Gray50
    onSurfaceVariant = Gray500,      // tint of icon Gray500
    outlineVariant = Gray100,        // border Gray100

    outline = Gray300,
    background = Color.White,
    surfaceTint = Blue50             // trackColor Blue50
)

private val DarkColorScheme = darkColorScheme(
    primary = Blue200,
    onPrimary = Blue900,
    primaryContainer = Blue800,
    onPrimaryContainer = Blue100,

    background = Gray900,
    onBackground = Gray50,

    surface = Gray800,
    onSurface = Gray50,

    // MAPPING DARK MODE COMPLEMENTS:
    surfaceVariant = Gray700,        // Darker gray for backgrounds
    onSurfaceVariant = Gray400,      // Lighter gray for icons in dark mode
    outlineVariant = Gray700,        // Subtle border for dark mode

    outline = Gray600,
    surfaceTint = Blue900            // Deep blue track for dark mode
)

@Composable
fun FiloTheme(
    modifier: Modifier = Modifier, darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme ->DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}