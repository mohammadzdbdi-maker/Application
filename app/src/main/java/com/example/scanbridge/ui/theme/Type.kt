package com.example.scanbridge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scanbridge.R

// فونت قبلی Pinar DS1 بود (فقط یک وزن داشت، برای Bold هم همون فایل استفاده می‌شد). طبق درخواست
// کاربر، هم‌رنگ سایت و نرم‌افزار دسکتاپ شد: همون Arad (نسخه‌ی فارسی‌دیجیت‌شده - AradFD) که آنجاها
// هم استفاده شده - این‌بار با دو فایل واقعی Regular/Bold، پس Bold دیگر کپی از Regular نیست.
val Arad = FontFamily(
    Font(R.font.arad_regular, FontWeight.Normal),
    Font(R.font.arad_bold, FontWeight.Bold)
)

// اندازه‌ها/فاصله‌گذاری‌ها طبق بخش ۱.۲ سند "ScanBridge UI Spec" تنظیم شدن.
val Typography = Typography(
    // Display — بنر/هدلاین صفحه‌ی Intro (29sp)
    displayLarge = TextStyle(
        fontFamily = Arad,
        fontWeight = FontWeight.Bold,
        fontSize = 29.sp,
        lineHeight = 34.sp
    ),
    displayMedium = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 26.sp, lineHeight = 31.sp),
    displaySmall = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 23.sp, lineHeight = 27.sp),

    // Screen title — History / User panel / Messages (21sp)
    headlineLarge = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 21.sp),
    headlineSmall = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 19.sp),

    // Card title — نام سیستم، عنوان شیت‌ها (16–19sp)
    titleLarge = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 19.sp),
    titleMedium = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 17.sp),
    titleSmall = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Bold, fontSize = 16.sp),

    // Body — توضیحات، عنوان ردیف‌ها (13.5sp / 1.6)
    bodyLarge = TextStyle(
        fontFamily = Arad,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.3.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Arad,
        fontWeight = FontWeight.Normal,
        fontSize = 13.5.sp,
        lineHeight = 21.5.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Arad,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp
    ),

    // Label — لیبل فیلدها، خط‌های متا (11.5sp)
    labelLarge = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Normal, fontSize = 12.5.sp),
    labelMedium = TextStyle(fontFamily = Arad, fontWeight = FontWeight.Normal, fontSize = 11.5.sp),
    // Caption / eyebrow — "SYSTEM PAIRING" و امثالش (10sp، حروف بزرگ، +0.12em)
    labelSmall = TextStyle(
        fontFamily = Arad,
        fontWeight = FontWeight.Bold,
        fontSize = 10.sp,
        letterSpacing = 1.2.sp
    )
)
