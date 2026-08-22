package com.example.scanbridge.ui.theme

import androidx.compose.ui.graphics.Color

// این پالت از دیتابیس واقعی اسکیل ui-ux-pro-max گرفته شده (جست‌وجوی محصولاتی مثل "Pet Tech"
// با نکته‌ی «Playful orange + trust blue» روی پس‌زمینه‌ی کرمی) - نزدیک‌ترین تطبیق واقعی به حس
// گرم و دوستانه‌ی عکس‌های رفرنس. نارنجی به‌عنوان لهجه‌ی گرم (AmberAccent) و آبی به‌عنوان لهجه‌ی
// اعتماد (BluePrimary) کنار سرمه‌ای اصلی (NavyPrimary) نگه داشته شدن.
val NavyPrimary = Color(0xFF1A237E)
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

// --- تم روشنِ اپ - هم‌رنگ نرم‌افزار دسکتاپ ---
// قبلاً این پالت («Nocturne») فقط برای تم تیره بود. طبق درخواست کاربر، حالا اپ همیشه روشن است:
// پس‌زمینه‌ی سفید/روشن (دقیقاً هم‌رنگ کارت‌های سفید دسکتاپ)، و لهجه/دکمه‌های بنفشِ پررنگ (هم‌خانواده
// با رنگ دکمه‌ی «ثبت مجدد» در دسکتاپ: #7C3AED). اسم توکن‌ها («Nocturne...») عمداً عوض نشده - فقط
// مقدارشون - چون همه‌جای کد (Buttons.kt، Cards.kt، Rows.kt، Dialogs.kt، Indicators.kt،
// Navigation.kt، Camera.kt، MainActivity.kt) مستقیماً به همین اسم‌ها ارجاع می‌دن؛ با عوض‌کردن فقط
// مقدارِ این چند خط، کل اپ بدون دست زدن به هیچ فایل دیگری رنگ عوض می‌کنه.
val CardDark = Color(0xFFFFFFFF)      // Nocturne Surface (کارت‌ها) - سفید
val AppBackground = Color(0xFFF8F9FB) // Nocturne Background (پس‌زمینه‌ی کلی) - سفید مایل به خاکستری
val GoldAccent = Color(0xFFFFB300)
val CyanAccent = Color(0xFF7C3AED)    // Nocturne Accent (لهجه‌ی اصلی) - بنفش پررنگ

// رنگ‌های تازه برای تایل‌های آیکون‌دار رنگارنگ (مثل گرید سرویس‌های اپ رفرنس) توی پنل کاربری
// و بنر خوش‌آمدگویی صفحه‌ی اسکن.
val CoralAccent = Color(0xFFFF6F59)
val PurpleAccent = Color(0xFF8B7CF6)

// --- توکن‌های پالت اصلی اپ (بخش ۱.۱ سند طراحی - حالا نسخه‌ی روشن) ---
val NocturneBackground = Color(0xFFF8F9FB)      // پس‌زمینه‌ی کلی صفحه‌ها
val NocturneSurface = Color(0xFFFFFFFF)         // کارت‌ها/سطرها/دیالوگ‌ها - سفید
val NocturneCameraGround = Color(0xFFEEF0F5)    // پشت قاب دوربین (وقتی هنوز پیش‌نمایش نیومده)
val NocturneText = Color(0xFF1C1B1F)            // متن اصلی - تقریباً مشکی
val NocturneTextMuted = Color(0xFF6B7280)       // متن کم‌رنگ - هم‌رنگ متن‌های کمکی دسکتاپ
val NocturneAccent = Color(0xFF7C3AED)          // لهجه‌ی اصلی (دکمه‌ها، سوییچ روشن، حاشیه‌ها)
val NocturneAccentLight = Color(0xFF8B5CF6)     // لهجه‌ی روشن‌تر (آیکون‌های فعال، حالت فشرده)
val NocturneAccentPale = Color(0xFF6D28D9)      // بنفش پررنگ‌تر - برای متن روی زمینه‌ی کم‌رنگ
val NocturneAccentContainer = Color(0xFFEDE9FE) // زمینه‌ی کم‌رنگ بنفش (تب فعال، تگ‌ها)
val NocturneAccentTint = Color(0xFFF3F0FF)      // زمینه‌ی کم‌رنگ‌تر (دکمه‌ی ثانویه، آیکون تنظیمات)
val NocturneDivider = Color(0xFF1C1B1F).copy(alpha = 0.08f)
val NocturneDividerAccent = Color(0xFF7C3AED).copy(alpha = 0.35f)
val NocturneNeutral = Color(0xFFE5E7EB)         // زمینه‌ی خنثی (تگ ناموفق، سوییچ خاموش)
val NocturneOnNeutral = Color(0xFF4B5563)       // متن/آیکون روی زمینه‌ی خنثی
