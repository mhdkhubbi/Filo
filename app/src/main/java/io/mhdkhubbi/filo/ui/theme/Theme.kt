package io.mhdkhubbi.filo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.mhdkhubbi.andro.ui.theme.AppTypography

val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue100,
    onPrimaryContainer = Blue900,

    background = Gray50,
    onBackground = Gray900,

    surface = White,
    onSurface = Gray900,

    outline = Gray300
)
val DarkColors = darkColorScheme(
    primary = Blue300,
    onPrimary = Black,
    primaryContainer = Blue700,
    onPrimaryContainer = Blue50,

    background = Gray900,
    onBackground = Gray50,

    surface = Gray800,
    onSurface = Gray50,

    outline = Gray600
)


@Composable
fun FiloTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}