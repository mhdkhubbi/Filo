package io.mhdkhubbi.filo.ui.theme

import Blue100
import Blue200
import Blue300
import Blue500
import Blue700
import Blue800
import Blue900
import Gray100
import Gray200
import Gray300
import Gray50
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

private val LightColorScheme = lightColorScheme(
    primary = Blue500,               // Your exact 0791F8
    onPrimary = Color.White,
    primaryContainer = Blue100,      // Light blue for buttons/containers
    onPrimaryContainer = Blue900,

    secondary = Blue700,
    onSecondary = Color.White,

    surface = Color.White,           // Clean white background
    onSurface = Gray900,             // Almost black text
    surfaceVariant = Gray100,        // Very light gray for cards
    onSurfaceVariant = Gray600,      // Medium gray for labels

    outline = Gray300,               // For borders
    background = Color.White
)

private val DarkColorScheme = darkColorScheme(
    // Primary is lighter in dark mode (Blue 200 or 300) to ensure readability
    primary = Blue200,
    onPrimary = Blue900,             // Dark text on light blue
    primaryContainer = Blue800,      // Deep blue for buttons
    onPrimaryContainer = Blue100,    // Light blue text on deep blue

    secondary = Blue300,
    onSecondary = Blue900,

    // Backgrounds use your Monochrome Gray 900
    background = Gray900,
    onBackground = Gray50,           // Near-white text

    // Surfaces (Cards, Bottom Sheets)
    surface = Gray800,
    onSurface = Gray50,
    surfaceVariant = Gray700,        // For subtle separation
    onSurfaceVariant = Gray200,

    // Borders
    outline = Gray600                // Visible but subtle on dark backgrounds
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