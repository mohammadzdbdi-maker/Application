package com.example.scanbridge.ui.theme

import androidx.compose.ui.graphics.Color

// این پالت از دیتابیس واقعی اسکیل ui-ux-pro-max گرفته شده (جست‌وجوی محصولاتی مثل "Pet Tech"
// با نکته‌ی «Playful orange + trust blue» روی پس‌زمینه‌ی کرمی) - نزدیک‌ترین تطبیق واقعی به حس
// گرم و دوستانه‌ی عکس‌های رفرنس. نارنجی به‌عنوان لهجه‌ی گرم (AmberAccent) و آبی به‌عنوان لهجه‌ی
// اعتماد (BluePrimary) کنار سرمه‌ای اصلی (NavyPrimary) نگه داشته شدن.
val NavyPrimary = Color(0xFF1A237E)
val GradientNavy = Color(0xFF1E3A8A) // شروع گرادیان لوکس دکمه‌ها — هم‌خانواده‌ی صفحه راهنمای سایت (#1E3A8A → #2563EB)
val GradientRed = Color(0xFFB3261E)    // شروع گرادیان قرمز دکمه خروج (→ ErrorRed)
val GradientOrange = Color(0xFFD97706) // شروع گرادیان نارنجی دکمه تغییر سیستم (→ AmberAccent)
val BluePrimary = Color(0xFF2563EB)
val AmberAccent = Color(0xFFF97316)
val SuccessGreen = Color(0xFF10B981) // هم‌رنگ با آیکون موفقیت نرم‌افزار دسکتاپ (ShowStyledMessage)
val ErrorRed = Color(0xFFE53935)     // هم‌رنگ با آیکون خطای نرم‌افزار دسکتاپ (ShowStyledMessage)

val LightBackground = Color(0xFFF8F9FB)
val LightSurface = Color(0xFFFFFFFF)

val BrandTeal = Color(0xFF00BFA5)
val NeutralGray = Color(0xFF607D8B)
val TextGray = Color(0xFF78909C)
val BorderWhite = Color(0xFF000000).copy(alpha = 0.05f)

// --- تم روشنِ اپ - هم‌رنگ صفحه‌ی راهنمای سایت (scanbridge.ir/guide) ---
// دکمه‌ها و لهجه‌ها: آبی سایت (#2563EB با گرادیان خانواده‌ی #1E3A8A)؛ کارت‌ها سفید با سایه‌ی ملایم؛
// زمینه‌ی کلی همان #F8FAFC راهنما. اسم توکن‌ها («Nocturne...») عمداً عوض نشده - فقط مقدارشون -
// چون همه‌جای کد (Buttons.kt، Cards.kt، Rows.kt، Dialogs.kt، Indicators.kt، Navigation.kt،
// Camera.kt، MainActivity.kt) مستقیماً به همین اسم‌ها ارجاع می‌دن؛ با عوض‌کردن فقط مقدارِ این
// چند خط، کل اپ بدون دست زدن به هیچ فایل دیگری رنگ عوض می‌کنه.
val CardDark = Color(0xFFFFFFFF)      // Nocturne Surface (کارت‌ها) - سفید
val AppBackground = Color(0xFFF8FAFC) // Nocturne Background (پس‌زمینه‌ی کلی) - هم‌رنگ راهنمای سایت
val GoldAccent = Color(0xFFFFB300)
val CyanAccent = Color(0xFF2563EB)    // Nocturne Accent (لهجه‌ی اصلی) - آبی سایت

// رنگ‌های تازه برای تایل‌های آیکون‌دار رنگارنگ (مثل گرید سرویس‌های اپ رفرنس) توی پنل کاربری
// و بنر خوش‌آمدگویی صفحه‌ی اسکن.
val CoralAccent = Color(0xFFFF6F59)
val PurpleAccent = Color(0xFF8B7CF6)

// --- توکن‌های پالت اصلی اپ — نسخه‌ی «آبی سایت» (هم‌خانواده‌ی صفحه راهنما) ---
val NocturneBackground = Color(0xFFF8FAFC)      // پس‌زمینه‌ی کلی صفحه‌ها
val NocturneSurface = Color(0xFFFFFFFF)         // کارت‌ها/سطرها/دیالوگ‌ها - سفید
val NocturneCameraGround = Color(0xFFEEF0F5)    // پشت قاب دوربین (وقتی هنوز پیش‌نمایش نیومده)
val NocturneText = Color(0xFF0F172A)            // متن اصلی - همان متن راهنما
val NocturneTextMuted = Color(0xFF475569)       // متن کم‌رنگ - خاکستری-آبی راهنما
val NocturneAccent = Color(0xFF2563EB)          // لهجه‌ی اصلی (دکمه‌های آبی راهنما)
val NocturneAccentLight = Color(0xFF60A5FA)     // لهجه‌ی روشن‌تر (آیکون‌های فعال)
val NocturneAccentPale = Color(0xFF1E40AF)      // آبی پررنگ - برای متن روی زمینه‌ی کم‌رنگ
val NocturneAccentContainer = Color(0xFFDBEAFE) // زمینه‌ی آبی کم‌رنگ (تب فعال، تگ‌ها)
val NocturneAccentTint = Color(0xFFEFF6FF)      // زمینه‌ی آبی خیلی کم‌رنگ (دکمه ثانویه، آیکون تنظیمات)
val NocturneDivider = Color(0xFF0F172A).copy(alpha = 0.08f)
val NocturneDividerAccent = Color(0xFF2563EB).copy(alpha = 0.35f)
val NocturneNeutral = Color(0xFFE5E7EB)         // زمینه‌ی خنثی (تگ ناموفق، سوییچ خاموش)
val NocturneOnNeutral = Color(0xFF4B5563)       // متن/آیکون روی زمینه‌ی خنثی
