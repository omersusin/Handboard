package handboard.app.core.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Purple80, onPrimary = Purple20,
    primaryContainer = Purple30, onPrimaryContainer = Purple90,
    secondary = PurpleGrey80, onSecondary = PurpleGrey30,
    secondaryContainer = PurpleGrey30, onSecondaryContainer = PurpleGrey90,
    tertiary = Color(0xFF4CAF50), onTertiary = Color.White,
    error = Error80, onError = Error30,
    background = Color(0xFF1C1B1F), onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF1C1B1F), onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF2C2C30), onSurfaceVariant = Color(0xFFCAC4D0),
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40, onPrimary = Color.White,
    primaryContainer = Purple90, onPrimaryContainer = Purple10,
    secondary = PurpleGrey50, onSecondary = Color.White,
    secondaryContainer = PurpleGrey90, onSecondaryContainer = PurpleGrey30,
    tertiary = Color(0xFF388E3C), onTertiary = Color.White,
    error = Error40, onError = Color.White,
    background = Purple99, onBackground = Color(0xFF1C1B1F),
    surface = Purple99, onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC), onSurfaceVariant = Color(0xFF49454F),
)

@Composable
fun HandBoardTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && darkTheme ->
            dynamicDarkColorScheme(LocalContext.current)
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !darkTheme ->
            dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Sync keyboard colors with theme
    SideEffect { applyKeyboardColors(darkTheme) }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = HandBoardTypography,
        content = content
    )
}
