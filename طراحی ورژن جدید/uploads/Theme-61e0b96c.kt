package com.example.scanbridge.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = NavyPrimary,
    secondary = BluePrimary,
    tertiary = BrandTeal,
    error = ErrorRed,
    background = LightBackground,
    surface = LightSurface,
    surfaceVariant = Color(0xFFEEF1F5),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSurfaceVariant = TextGray,
    outline = NeutralGray.copy(alpha = 0.35f)
)

// این پالت تیره قبلاً در Color.kt تعریف شده بود ولی جایی استفاده نمی‌شد؛ تم برنامه همیشه
// روی حالت روشن قفل بود، حتی وقتی گوشی در حالت تاریک بود.
private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    secondary = BrandTeal,
    tertiary = GoldAccent,
    error = ErrorRed,
    background = AppBackground,
    surface = CardDark,
    surfaceVariant = Color(0xFF232A30),
    onPrimary = Color(0xFF00232B),
    onSecondary = Color(0xFF00201B),
    onTertiary = Color(0xFF2B1D00),
    onBackground = Color(0xFFE7EAEE),
    onSurface = Color(0xFFE7EAEE),
    onSurfaceVariant = Color(0xFFA9B4BD),
    outline = TextGray.copy(alpha = 0.4f)
)

@Composable
fun ScanBridgeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
