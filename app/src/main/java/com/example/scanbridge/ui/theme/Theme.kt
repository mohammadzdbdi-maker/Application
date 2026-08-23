package com.example.scanbridge.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = CyanAccent,
    onPrimary = Color.White,
    primaryContainer = NocturneAccentContainer,
    onPrimaryContainer = NocturneAccentPale,
    secondary = BluePrimary,
    tertiary = BrandTeal,
    error = ErrorRed,
    background = AppBackground,
    onBackground = NocturneText,
    surface = CardDark,
    onSurface = NocturneText,
    surfaceVariant = NocturneNeutral,
    onSurfaceVariant = NocturneOnNeutral,
    outline = NocturneDividerAccent,
    outlineVariant = NocturneDivider
)

// قبلاً این تم فقط تیره بود (طبق سند "ScanBridge UI Spec" - "Dark theme only"، مستقل از تنظیم
// خود گوشی). طبق درخواست کاربر، حالا اپ همیشه روشن است و هم‌رنگ صفحه‌ی راهنمای سایت: پس‌زمینه‌ی
// سفید/روشن + دکمه‌های آبی سایت. چیدمان/استایل (گردی گوشه‌ها، افکت شیشه‌ای روی دوربین و غیره)
// دست‌نخورده مانده - فقط پالت رنگ عوض شده (در Color.kt). DarkColorScheme پایین هم برای سازگاری
// نگه داشته شده (اگر یک روز دوباره لازم شد)، ولی دیگر پیش‌فرض نیست.
private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.White,
    primaryContainer = NocturneAccentContainer,
    onPrimaryContainer = NocturneAccentPale,
    secondary = BrandTeal,
    tertiary = GoldAccent,
    error = ErrorRed,
    background = AppBackground,
    onBackground = NocturneText,
    surface = CardDark,
    onSurface = NocturneText,
    surfaceVariant = NocturneNeutral,
    onSurfaceVariant = NocturneOnNeutral,
    outline = NocturneDividerAccent,
    outlineVariant = NocturneDivider
)

@Composable
fun ScanBridgeTheme(
    darkTheme: Boolean = false, // طبق درخواست کاربر: همیشه تم روشن، هم‌رنگ نرم‌افزار دسکتاپ
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content
    )
}
