package com.example.scanbridge

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.RenderEffect
import android.graphics.Shader
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.util.Size
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.animation.AnticipateInterpolator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import android.net.VpnService
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.scanbridge.ui.theme.ScanBridgeTheme
import com.example.scanbridge.ui.theme.*
import com.example.scanbridge.ui.components.*
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import okhttp3.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// --- Localization ---

enum class AppLanguage { FA, EN }

class Strings(val lang: AppLanguage) {
    val appName = "ScanBridge"
    val systemConnection = if (lang == AppLanguage.FA) "اتصال سیستم" else "System Connection"
    val connected = if (lang == AppLanguage.FA) "متصل" else "Connected"
    val connecting = if (lang == AppLanguage.FA) "در حال اتصال..." else "Connecting..."
    val disconnected = if (lang == AppLanguage.FA) "قطع" else "Disconnected"
    val scanSpeed = if (lang == AppLanguage.FA) "سرعت اسکن" else "Scan Speed"
    val identified = if (lang == AppLanguage.FA) "شناسایی شد" else "Identified"
    val resend = if (lang == AppLanguage.FA) "ارسال مجدد" else "Resend"
    val history = if (lang == AppLanguage.FA) "تاریخچه اسکن‌ها" else "Scan History"
    val switchSystem = if (lang == AppLanguage.FA) "تغییر سیستم" else "Switch System"
    val userPanel = if (lang == AppLanguage.FA) "پنل کاربری" else "Profile"
    val pharmacyMode = if (lang == AppLanguage.FA) "حالت داروخانه (فقط شبکه محلی)" else "Pharmacy Mode (LAN only)"
    val pharmacyModeDesc = if (lang == AppLanguage.FA) "اینترنت گوشی به‌کلی قطع می‌شود؛ فقط ارتباط با سیستم روی شبکه داروخانه باز می‌ماند" else "Cuts the phone's internet completely; only the connection to your system on the pharmacy network stays alive"
    val scanTab = if (lang == AppLanguage.FA) "اسکن" else "Scan"
    val exitApp = if (lang == AppLanguage.FA) "خروج از برنامه" else "Exit App"
    val greetingHello = if (lang == AppLanguage.FA) "سلام 👋" else "Hello 👋"
    val settings = if (lang == AppLanguage.FA) "تنظیمات برنامه" else "App Settings"
    val language = if (lang == AppLanguage.FA) "زبان" else "Language"
    val close = if (lang == AppLanguage.FA) "بستن" else "Close"
    val exitReminder = if (lang == AppLanguage.FA) "برای خروج از دکمه بالا استفاده کنید" else "Use the top button to exit"
    val pairingTitle = if (lang == AppLanguage.FA) "جفت‌سازی سیستم" else "System Pairing"
    val pairingInstruction = if (lang == AppLanguage.FA) "QR کد نمایش داده شده در ویندوز را اسکن کنید" else "Scan the QR code displayed on Windows"
    val startScan = if (lang == AppLanguage.FA) "شروع اسکن" else "Start Scanning"
    val connectToSystem = if (lang == AppLanguage.FA) "اتصال به سیستم" else "Connect to System"
    val notConnectedYet = if (lang == AppLanguage.FA) "هنوز به هیچ سیستمی وصل نیستی" else "Not connected to a system yet"
    val pairingErrorBrand = if (lang == AppLanguage.FA) "این QR کد مربوط به ScanBridge نیست" else "Not a ScanBridge QR code"
    val pairingErrorFormat = if (lang == AppLanguage.FA) "ساختار QR کد اشتباه است" else "Invalid QR format"
    val renameTitle = if (lang == AppLanguage.FA) "نام این سیستم رو چی میذاری؟" else "What will you call this system?"
    val confirm = if (lang == AppLanguage.FA) "تایید" else "Confirm"
    val okay = if (lang == AppLanguage.FA) "باشه" else "OK"
    val registerAgain = if (lang == AppLanguage.FA) "ثبت مجدد" else "Register Again"
    val emptyHistory = if (lang == AppLanguage.FA) "هنوز بارکدی اسکن نشده" else "No barcodes scanned yet"
    val clearHistory = if (lang == AppLanguage.FA) "پاک کردن" else "Clear"
    val delete = if (lang == AppLanguage.FA) "بله، پاک شود" else "Yes, Clear"
    val resent = if (lang == AppLanguage.FA) "دوباره ارسال شد" else "Resent"
    val notConnected = if (lang == AppLanguage.FA) "اتصال برقرار نیست" else "Not connected"
    val todayScans = if (lang == AppLanguage.FA) "امروز" else "Today"
    val totalScans = if (lang == AppLanguage.FA) "کل اسکن‌ها" else "Total Scans"

    // Intro
    val introHeadline = if (lang == AppLanguage.FA) "اسکن سریع، اتصال مطمئن" else "Fast Scan, Reliable Connection"
    val introSubtitle = if (lang == AppLanguage.FA) "بارکد رو در یک لحظه به سیستمت منتقل کن." else "Send barcodes to your computer in an instant."
    val introCaption = if (lang == AppLanguage.FA) "سریع، ساده و امن" else "Fast, simple and secure"
    val enter = if (lang == AppLanguage.FA) "ورود" else "Enter"
    val next = if (lang == AppLanguage.FA) "بعدی" else "Next"
    val previous = if (lang == AppLanguage.FA) "قبلی" else "Previous"
    val submitFormulaRemote = if (lang == AppLanguage.FA) "ایجاد نسخه و ثبت" else "Create Prescription & Submit"
    val birthDay = if (lang == AppLanguage.FA) "روز" else "Day"
    val birthMonth = if (lang == AppLanguage.FA) "ماه" else "Month"
    val birthYear = if (lang == AppLanguage.FA) "سال" else "Year"
    val gotIt = if (lang == AppLanguage.FA) "متوجه شدم" else "Got it"
    val skipTutorial = if (lang == AppLanguage.FA) "رد کردن آموزش" else "Skip tutorial"
    val editSystemName = if (lang == AppLanguage.FA) "ویرایش نام سیستم" else "Edit system name"
    val cancel = if (lang == AppLanguage.FA) "لغو" else "Cancel"

    // Guided tutorial
    val tutorialNameTitle = if (lang == AppLanguage.FA) "یه اسم براش انتخاب کن" else "Pick a name for it"
    val tutorialNameDesc = if (lang == AppLanguage.FA) "می‌تونی این سیستم رو هر اسمی که دوست داری صدا کنی، مثلاً «صندوق فروشگاه». بعداً هم از همین‌جا قابل تغییره." else "You can name this system anything you like, e.g. \"Front Desk\". You can always rename it later."
    val tutorialHistoryTitle = if (lang == AppLanguage.FA) "تاریخچه‌ی اسکن‌ها" else "Scan history"
    val tutorialHistoryDesc = if (lang == AppLanguage.FA) "با زدن این آیکون بالای صفحه، لیست همه‌ی بارکدهایی که اسکن کردی رو می‌بینی." else "Tap this icon at the top to see the list of every barcode you've scanned."
    val tutorialSettingsTitle = if (lang == AppLanguage.FA) "تنظیمات برنامه" else "App settings"
    val tutorialSettingsDesc = if (lang == AppLanguage.FA) "از اینجا می‌تونی زبان برنامه رو عوض کنی و آخرین نسخه رو بررسی کنی." else "From here you can change the app's language and check for updates."
    val tutorialTorchTitle = if (lang == AppLanguage.FA) "چراغ‌قوه" else "Flashlight"
    val tutorialTorchDesc = if (lang == AppLanguage.FA) "توی محیط تاریک، با زدن این دکمه چراغ گوشی روشن می‌شه تا بارکد بهتر دیده بشه." else "In a dark spot, tap this to turn on the flash so the barcode is easier to read."
    val tutorialSwitchTitle = if (lang == AppLanguage.FA) "تغییر سیستم" else "Switch system"
    val tutorialSwitchDesc = if (lang == AppLanguage.FA) "اگه خواستی این گوشی رو به یه کامپیوتر دیگه وصل کنی، از همین دکمه استفاده کن." else "If you want to connect this phone to a different computer, use this button."
    val tutorialScanTitle = if (lang == AppLanguage.FA) "حالا نوبت توئه" else "Now it's your turn"
    val tutorialScanDesc = if (lang == AppLanguage.FA) "یک بارکد یا QR واقعی رو جلوی دوربین بگیر تا اسکن بشه و برای سیستم ارسال بشه." else "Point the camera at a real barcode or QR code to scan and send it to your computer."
    val tutorialGotoHistoryTitle = if (lang == AppLanguage.FA) "عالی بود!" else "Nicely done!"
    val tutorialGotoHistoryDesc = if (lang == AppLanguage.FA) "بارکد با موفقیت ارسال شد. الان می‌بریمت به تاریخچه تا نتیجه رو ببینی." else "Your barcode was sent successfully. Taking you to history now to see the result."
    val tutorialFinishTitle = if (lang == AppLanguage.FA) "آموزش تموم شد" else "Tutorial complete"
    val tutorialFinishDesc = if (lang == AppLanguage.FA) "این‌جا تاریخچه‌ی همه‌ی اسکن‌هاته. روی هرکدوم بزنی می‌تونی دوباره برای سیستم ارسالش کنی." else "This is the history of all your scans. Tap any item to resend it to your computer."

    // Update Checker
    val version = if (lang == AppLanguage.FA) "نسخه" else "Version"
    val checkUpdate = if (lang == AppLanguage.FA) "بررسی بروزرسانی" else "Check for Update"
    val checking = if (lang == AppLanguage.FA) "در حال بررسی..." else "Checking..."
    val latestVersionMsg = if (lang == AppLanguage.FA) "شما از آخرین نسخه استفاده می‌کنید" else "You are using the latest version"

    // Website & Messages
    val website = if (lang == AppLanguage.FA) "وب‌سایت" else "Website"
    val messages = if (lang == AppLanguage.FA) "پیام‌ها" else "Messages"
    val guide = if (lang == AppLanguage.FA) "راهنما" else "Guide"
    val guideTitle = if (lang == AppLanguage.FA) "راهنمای کامل برنامه" else "Complete Guide"
    val guideSubtitle = if (lang == AppLanguage.FA) "توضیح همه‌ی بخش‌های برنامه، قدم‌به‌قدم" else "Every section explained, step by step"
    val guideScreenshotSoon = if (lang == AppLanguage.FA) "📸 اسکرین‌شات این بخش به‌زودی اضافه می‌شود" else "📸 Screenshot coming soon"
    val newUpdateAvailable = if (lang == AppLanguage.FA) "نسخه‌ی جدید موجوده" else "New update available"
    val noNewMessages = if (lang == AppLanguage.FA) "پیام جدیدی نیست" else "No new messages"
    val updateNow = if (lang == AppLanguage.FA) "بروزرسانی" else "Update Now"
}

// --- App State ---

enum class AppScreen {
    INTRO,
    PAIRING,
    SCANNER,
    HISTORY,
    USER_PANEL
}

// --- Guided, step-by-step first-run tutorial ---
// این مراحل به ترتیب طی می‌شن: بعد از جفت‌سازی و انتخاب اسم سیستم، هر دکمه‌ی مهم صفحه‌ی
// اسکنر توضیح داده می‌شه، بعد کاربر مجبوره یک بارکد واقعی اسکن کنه، و در آخر بره توی
// تاریخچه تا نتیجه رو ببینه. با NONE یعنی آموزشی در حال اجرا نیست.
enum class TutorialStep {
    NONE,
    NAME_SYSTEM,
    HISTORY_BUTTON,
    SETTINGS_BUTTON,
    TORCH_BUTTON,
    SWITCH_BUTTON,
    SCAN_BARCODE,
    GOTO_HISTORY,
    FINISH
}

data class ScanHistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val barcode: String,
    val time: String,
    val format: Int,
    val timestamp: Long = System.currentTimeMillis()
)

// نتیجه‌ی ثبت شیرخشک که دسکتاپ از طریق همین وب‌سوکت برای گوشی می‌فرستد - چه ثبت موفق باشد، چه
// ناموفق، چه هر اخطار دیگری که موقع ثبت نشان داده شده. عیناً همان عنوان/متنی که روی دسکتاپ دیده
// می‌شود، اینجا هم دیده می‌شود. اگر ثبت موفق بوده و برای همین قلم عکسی روی دسکتاپ موجود باشد،
// همان عکس هم به‌صورت Base64 همراه پیام می‌آید تا کنار متن نشان داده شود (photoBase64 == null
// یعنی عکسی برای این پیام ارسال نشده - مثلاً هشدارهای ناموفق فعلاً بدون عکس هستند).
// canRepeat فقط برای یک ثبتِ شیرخشکِ واقعاً موفق true است (نه هشدار «قبلاً ثبت شده» و نه هیچ پیام
// دیگری) - وقتی true است، کنار دکمه‌ی «باشه» یک دکمه‌ی «ثبت مجدد» هم نشان داده می‌شود.
data class FormulaAlert(
    val title: String,
    val body: String,
    val success: Boolean,
    val photoBase64: String? = null,
    val canRepeat: Boolean = false
)

// یک مرحله از فرم ثبت شیرخشک («ورود اطلاعات از راه دور») که دسکتاپ روی گوشی نشان می‌دهد - کد
// ملی، تاریخ تولد، شماره نظام پزشکی، شماره تماس، کپچا و در آخر دکمه‌ی نهایی ثبت. inputType
// می‌گوید این مرحله چه شکلی رندر شود: "info" (فقط عکس/نام محصول + دکمه‌ی بعدی، بدون ورودی)،
// "number"/"text" (یک فیلد متنی + دکمه‌ی بعدی)، "captcha" (عکس کپچا + فیلد متنی + دکمه‌ی بعدی)،
// یا "button" (فقط یک دکمه‌ی نهایی «ثبت»، بدون ورودی).
data class RemoteEntryStep(
    val barcode: String,
    val stepId: String,
    val label: String,
    val hint: String,
    val inputType: String,
    val photoBase64: String? = null,
    val captchaImageBase64: String? = null,
    // مقداری که همین الان در کادر دسکتاپ متناظر این مرحله هست (اگر از قبل چیزی وارد شده) - برای
    // این‌که وقتی همین مرحله دوباره نشان داده می‌شود (مثلاً با «قبلی»)، کادر گوشی خالی نباشد و
    // کاربر مجبور نشود چیزی را که قبلاً درست وارد کرده دوباره تایپ کند.
    val prefillValue: String? = null
)

// پیام موفقیت ثبت شیرخشک معمولاً عیناً از سرور تی‌تک می‌آید و یک جمله با دو مبلغ ریالی دارد:
// «قیمت این فرآورده X ریال است که ... و Y ریال آن توسط متقاضی ... پرداخت شود». این الگو همان دو
// مبلغ (قیمت اولیه‌ی فرآورده و مبلغ قابل پرداخت متقاضی) را پیدا می‌کند - دقیقاً همان الگویی که
// سمت دسکتاپ هم برای رنگی کردن همین پیام استفاده می‌کند.
private val ttacPriceMessageRegex = Regex(
    "قیمت\\s+این\\s+فرآورده\\s+(?<base>[\\d,٬]+\\s*ریال).*?(?<patient>[\\d,٬]+\\s*ریال)\\s+آن\\s+توسط\\s+متقاضی",
    setOf(RegexOption.DOT_MATCHES_ALL)
)

private fun ttacAmountTextsEqual(a: String, b: String): Boolean {
    val na = a.filter { it.isDigit() }
    val nb = b.filter { it.isDigit() }
    return na.isNotEmpty() && na == nb
}

// متن پیام را می‌سازد؛ اگر با الگوی بالا مطابقت داشته باشد، قیمت اولیه‌ی فرآورده را قرمز پررنگ و
// مبلغ قابل پرداخت متقاضی را سبز پررنگ می‌کند (یا اگر بیمه چیزی پرداخت نکرده و دو مبلغ برابرند، هر
// دو را قرمز پررنگ نشان می‌دهد). هر پیام دیگری که با این الگو مطابقت نداشته باشد، بدون تغییر و به
// همان رنگ متن پیش‌فرض دیالوگ نمایش داده می‌شود.
private fun formulaAlertBodyAnnotatedString(body: String) = buildAnnotatedString {
    val match = ttacPriceMessageRegex.find(body)
    if (match == null) {
        append(body)
        return@buildAnnotatedString
    }

    val baseGroup = match.groups["base"]!!
    val patientGroup = match.groups["patient"]!!
    val insurancePaidNothing = ttacAmountTextsEqual(baseGroup.value, patientGroup.value)

    val red = Color(0xFFE53935)
    val green = Color(0xFF10B981)

    append(body.substring(0, baseGroup.range.first))
    withStyle(SpanStyle(color = red, fontWeight = FontWeight.Bold)) {
        append(baseGroup.value)
    }
    append(body.substring(baseGroup.range.last + 1, patientGroup.range.first))
    withStyle(SpanStyle(color = if (insurancePaidNothing) red else green, fontWeight = FontWeight.Bold)) {
        append(patientGroup.value)
    }
    append(body.substring(patientGroup.range.last + 1))
}

// رشته‌ی Base64 عکس شیرخشک که همراه پیام هشدار می‌آید را به یک Bitmap قابل نمایش تبدیل می‌کند.
// اگر رشته خالی/نامعتبر باشد یا دیکد کردنش خطا بدهد، null برمی‌گرداند - این حالت نباید کل دیالوگ
// را خراب کند، فقط یعنی بدون عکس (فقط متن) نمایش داده می‌شود.
private fun decodeFormulaAlertPhoto(photoBase64: String?): Bitmap? {
    if (photoBase64.isNullOrBlank()) return null
    return try {
        val bytes = Base64.decode(photoBase64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) {
        null
    }
}

// org.json.JSONObject.optString(key, fallback) یک تله دارد: اگر کلید وجود داشته باشد ولی مقدارش
// صریحاً JSON null باشد (مثلاً وقتی دسکتاپ photoBase64/prefillValue را چون خالی بوده null
// می‌فرستد)، optString رشته‌ی fallback را برنمی‌گرداند - رشته‌ی تحت‌اللفظی "null" را برمی‌گرداند
// (چون داخلش روی JSONObject.NULL هم toString صدا می‌زند). همین باعث می‌شد کلمه‌ی "null" واقعاً
// توی کادرهای گوشی نوشته شود. این تابع هم غیاب کلید، هم null صریح، هم رشته‌ی خالی را یکسان null
// در نظر می‌گیرد.
private fun JSONObject.optStringOrNull(key: String): String? {
    if (!has(key) || isNull(key)) return null
    return optString(key, "").ifBlank { null }
}

// --- WebSocket Manager ---

class WebSocketManager(
    private val url: String,
    private val deviceName: String,
    private val onStatusChange: (ConnectionStatus) -> Unit,
    private val onAckReceived: (String) -> Unit,
    private val onMessageBuffered: (Int) -> Unit,
    private val onAlertReceived: (FormulaAlert) -> Unit = {},
    // فراخوانی می‌شود وقتی دسکتاپ یک مرحله‌ی جدید از فرم ثبت شیرخشک را برای گوشی می‌فرستد
    // (REMOTE_ENTRY_STEP)؛ با null فراخوانی می‌شود وقتی دسکتاپ آن را لغو می‌کند (REMOTE_ENTRY_CANCEL).
    private val onRemoteEntryStep: (RemoteEntryStep?) -> Unit = {}
) {
    private val client = OkHttpClient.Builder()
        .pingInterval(20, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null
    private val messageQueue = ConcurrentLinkedQueue<String>()
    private var isIntentionalShutdown = false
    private var reconnectTimer: Timer? = null

    enum class ConnectionStatus { CONNECTED, CONNECTING, DISCONNECTED }

    fun connect() {
        cancelReconnect()
        webSocket?.close(1001, "Reconnecting")

        isIntentionalShutdown = false
        onStatusChange(ConnectionStatus.CONNECTING)
        try {
            val request = Request.Builder().url(url).build()
            webSocket = client.newWebSocket(request, object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    onStatusChange(ConnectionStatus.CONNECTED)
                    val registerMsg = JSONObject().apply {
                        put("deviceName", deviceName)
                    }.toString()
                    webSocket.send(registerMsg)
                    flushQueue()
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    if (text.startsWith("OK")) {
                        onAckReceived(text)
                        return
                    }

                    // پیام‌های دیگر (نتیجه‌ی ثبت شیرخشک، پینگ سلامت و ...) به‌صورت JSON با یک
                    // فیلد "type" می‌آیند؛ اگر پارس نشد یا نوعش را نشناختیم، بی‌صدا نادیده گرفته
                    // می‌شود (مثلاً "DISCONNECT" یا خودِ پینگ سلامت).
                    try {
                        val json = JSONObject(text)
                        when (json.optString("type")) {
                            "SCANBRIDGE_ALERT" -> {
                                val photo = json.optStringOrNull("photoBase64")
                                onAlertReceived(
                                    FormulaAlert(
                                        title = json.optString("title"),
                                        body = json.optString("body"),
                                        success = json.optBoolean("success", true),
                                        photoBase64 = photo,
                                        canRepeat = json.optBoolean("canRepeat", false)
                                    )
                                )
                            }
                            "REMOTE_ENTRY_STEP" -> {
                                onRemoteEntryStep(
                                    RemoteEntryStep(
                                        barcode = json.optString("barcode"),
                                        stepId = json.optString("stepId"),
                                        label = json.optString("label"),
                                        hint = json.optString("hint"),
                                        inputType = json.optString("inputType", "text"),
                                        photoBase64 = json.optStringOrNull("photoBase64"),
                                        captchaImageBase64 = json.optStringOrNull("captchaImageBase64"),
                                        prefillValue = json.optStringOrNull("prefillValue")
                                    )
                                )
                            }
                            "REMOTE_ENTRY_CANCEL" -> {
                                onRemoteEntryStep(null)
                            }
                        }
                    } catch (_: Exception) {
                    }
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    onStatusChange(ConnectionStatus.DISCONNECTED)
                    reconnect()
                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    if (!isIntentionalShutdown) {
                        onStatusChange(ConnectionStatus.DISCONNECTED)
                        reconnect()
                    }
                }
            })
        } catch (e: Exception) {
            onStatusChange(ConnectionStatus.DISCONNECTED)
        }
    }

    private fun reconnect() {
        if (!isIntentionalShutdown) {
            cancelReconnect()
            reconnectTimer = Timer().apply {
                schedule(object : TimerTask() {
                    override fun run() {
                        connect()
                    }
                }, 3000)
            }
        }
    }

    private fun cancelReconnect() {
        reconnectTimer?.cancel()
        reconnectTimer = null
    }

    fun sendBarcode(barcode: String): Boolean {
        val jsonMsg = JSONObject().apply {
            put("deviceName", deviceName)
            put("barcode", barcode)
        }.toString()

        val socket = webSocket
        val success = if (socket != null && socket.send(jsonMsg)) {
            true
        } else {
            if (messageQueue.size < 200) {
                messageQueue.offer(barcode)
                onMessageBuffered(messageQueue.size)
            }
            false
        }
        flushQueue()
        return success
    }

    // این دو پیام مربوط به «ورود اطلاعات از راه دور»اند - برخلاف sendBarcode، عمداً هیچ صف/تلاش
    // مجددی ندارند: اگر همین لحظه اتصال برقرار نباشد، ارسال بی‌سروصدا ناموفق می‌ماند (کاربر روی
    // گوشی می‌تواند دوباره دکمه را بزند) - چون این پیام‌ها برخلاف بارکد، معنایشان به وضعیت لحظه‌ای
    // فرم روی دسکتاپ گره خورده و صف‌کردن/ارسال دیرهنگام آن‌ها می‌تواند اطلاعات نامرتبط را وارد کند.
    fun sendRemoteEntryValue(barcode: String, stepId: String, value: String): Boolean {
        val jsonMsg = JSONObject().apply {
            put("type", "REMOTE_ENTRY_VALUE")
            put("barcode", barcode)
            put("stepId", stepId)
            put("value", value)
        }.toString()
        return webSocket?.send(jsonMsg) ?: false
    }

    fun sendRemoteEntrySubmit(barcode: String): Boolean {
        val jsonMsg = JSONObject().apply {
            put("type", "REMOTE_ENTRY_SUBMIT")
            put("barcode", barcode)
        }.toString()
        return webSocket?.send(jsonMsg) ?: false
    }

    fun sendRemoteEntryBack(barcode: String): Boolean {
        val jsonMsg = JSONObject().apply {
            put("type", "REMOTE_ENTRY_BACK")
            put("barcode", barcode)
        }.toString()
        return webSocket?.send(jsonMsg) ?: false
    }

    // «ثبت مجدد»: کاربر روی دیالوگ موفقیتِ ثبت شیرخشک دکمه‌ی «ثبت مجدد» را زده - یعنی می‌خواهد
    // قلم بعدی را با همان اطلاعات بیمار (که دسکتاپ نگه داشته) فقط با اسکن بارکد + کپچای تازه ثبت
    // کند. بارکدی همراه این پیام نیست (هنوز اسکن نشده)؛ مثل بقیه‌ی پیام‌های ورود از راه دور، صف
    // نمی‌شود - اگر همین لحظه وصل نباشیم، بی‌سروصدا ناموفق می‌ماند.
    fun sendRemoteEntryRepeatArm(): Boolean {
        val jsonMsg = JSONObject().apply {
            put("type", "REMOTE_ENTRY_REPEAT_ARM")
        }.toString()
        return webSocket?.send(jsonMsg) ?: false
    }

    private fun flushQueue() {
        val socket = webSocket ?: return
        while (messageQueue.isNotEmpty()) {
            val barcode = messageQueue.peek() ?: break
            val jsonMsg = JSONObject().apply {
                put("deviceName", deviceName)
                put("barcode", barcode)
            }.toString()

            if (socket.send(jsonMsg)) {
                messageQueue.poll()
                onMessageBuffered(messageQueue.size)
            } else {
                break
            }
        }
    }

    fun shutdown() {
        isIntentionalShutdown = true
        cancelReconnect()
        webSocket?.close(1000, "Shutdown")
        webSocket = null
    }

    fun isConnected() = webSocket != null
}

// --- MainActivity ---

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()

        splashScreen.setOnExitAnimationListener { splashScreenView ->
            val scaleX = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_X, 1f, 1.4f)
            val scaleY = ObjectAnimator.ofFloat(splashScreenView.iconView, View.SCALE_Y, 1f, 1.4f)
            val alpha = ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)

            scaleX.interpolator = AnticipateInterpolator()
            scaleY.interpolator = AnticipateInterpolator()
            alpha.duration = 400
            scaleX.duration = 400
            scaleY.duration = 400

            scaleX.doOnEnd { splashScreenView.remove() }

            scaleX.start()
            scaleY.start()
            alpha.start()
        }

        super.onCreate(savedInstanceState)
        setContent {
            val sharedPrefs = remember { getSharedPreferences("ScanBridgePrefs", Context.MODE_PRIVATE) }
            val langName = sharedPrefs.getString("app_lang", AppLanguage.FA.name)
            var currentLanguage by remember { mutableStateOf(AppLanguage.valueOf(langName!!)) }
            val s = remember(currentLanguage) { Strings(currentLanguage) }

            ScanBridgeTheme {
                val layoutDirection = if (currentLanguage == AppLanguage.FA) LayoutDirection.Rtl else LayoutDirection.Ltr
                CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                        MainApp(
                            s = s,
                            currentLanguage = currentLanguage,
                            onLanguageChange = {
                                currentLanguage = it
                                sharedPrefs.edit().putString("app_lang", it.name).apply()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MainApp(
    s: Strings,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("ScanBridgePrefs", Context.MODE_PRIVATE) }
    val deviceName = remember { "${Build.MANUFACTURER} ${Build.MODEL}" }

    var currentScreen by remember { mutableStateOf(AppScreen.INTRO) }
    var tutorialStep by remember { mutableStateOf(TutorialStep.NONE) }

    // از این به بعد، چه کاربر قبلاً جفت شده باشه چه نه، همیشه مستقیم وارد «پنجره اصلی» (تب اسکن،
    // با نوار پایین کامل) می‌شیم؛ دیگه صفحه‌ی جفت‌سازی یه صفحه‌ی جدا و مسدودکننده نیست. اگه هنوز
    // به سیستمی وصل نشده، همون تب اسکن به‌جای دوربین بارکد، کارت «اتصال به سیستم» رو نشون می‌ده
    // (پایین‌تر، توی isPaired) - در نتیجه کاربر همیشه می‌تونه بدون وصل شدن هم به تب تاریخچه و
    // پنل کاربری سر بزنه.
    fun goToMainFlow() {
        val alreadyPaired = sharedPrefs.getString("computer_ip", null) != null
        val tutorialDone = sharedPrefs.getBoolean("tutorial_completed", false)
        currentScreen = AppScreen.SCANNER
        if (alreadyPaired) {
            // اگه قبلاً جفت شده ولی هنوز آموزش رو ندیده (مثلاً کاربر قدیمی)، از همون قسمت
            // توضیح دکمه‌ها شروع می‌شه، نه از نام‌گذاری سیستم که فقط توی جفت‌سازی معنی داره.
            tutorialStep = if (tutorialDone) TutorialStep.NONE else TutorialStep.SETTINGS_BUTTON
        } else {
            tutorialStep = if (tutorialDone) TutorialStep.NONE else TutorialStep.NAME_SYSTEM
        }
        // همین که آموزش شروع شد (نه فقط وقتی تموم شد)، یه بار برای همیشه ثبت می‌شه که دیده شده.
        // اگه کاربر وسط آموزش از دکمه‌ی خروج بزنه و دوباره برنامه رو باز کنه، دیگه آموزش
        // نباید از اول شروع بشه.
        if (!tutorialDone && tutorialStep != TutorialStep.NONE) {
            sharedPrefs.edit().putBoolean("tutorial_completed", true).apply()
        }
    }

    val scanHistory = remember { mutableStateListOf<ScanHistoryItem>() }
    var connectionStatus by remember { mutableStateOf(WebSocketManager.ConnectionStatus.DISCONNECTED) }
    var bufferedCount by remember { mutableIntStateOf(0) }
    // نتیجه‌ی ثبت شیرخشک که همین الان از دسکتاپ رسیده و باید به کاربر نشان داده شود؛ null یعنی
    // فعلاً چیزی برای نمایش نیست. با تپ روی «باشه» (یا بستن دیالوگ) دوباره null می‌شود.
    var pendingFormulaAlert by remember { mutableStateOf<FormulaAlert?>(null) }
    // مرحله‌ی فعلی «ورود اطلاعات از راه دور» که باید روی گوشی نشان داده شود؛ null یعنی الان چنین
    // جریانی فعال نیست. هیچ‌جا ذخیره (SharedPreferences و ...) نمی‌شود - کاملاً یک‌بارمصرف است.
    var remoteEntryStep by remember { mutableStateOf<RemoteEntryStep?>(null) }
    // هر بار که دسکتاپ یک REMOTE_ENTRY_STEP جدید می‌فرستد - حتی اگر همان مرحله‌ی قبلی باشد (مثلاً
    // وقتی دکمه‌ی «ثبت نهایی» دوباره با متن خطا فرستاده می‌شود، stepId هنوز «submit» است) - این
    // شماره یکی زیاد می‌شود. چون فقط بر اساس stepId کلید بزنیم، وقتی همان مرحله با محتوای تازه
    // دوباره می‌آید، Compose آن را «همان قبلی» حساب می‌کند و state هایی مثل «الان منتظر پاسخم»
    // (isWaitingForNextStep) هیچ‌وقت ریست نمی‌شوند - نتیجه‌اش این بود که بعد از یک خطا، هم دکمه‌ی
    // «قبلی» و هم دکمه‌ی «ثبت» برای همیشه غیرفعال می‌ماندند.
    var remoteEntryStepRevision by remember { mutableStateOf(0) }

    val serverUrlState = remember { mutableStateOf(sharedPrefs.getString("server_url", "") ?: "") }

    val wsManager = remember(serverUrlState.value) {
        if (serverUrlState.value.isNotEmpty()) {
            WebSocketManager(
                url = serverUrlState.value,
                deviceName = deviceName,
                onStatusChange = { connectionStatus = it },
                onAckReceived = { ack -> Log.d("ScanBridge", "Ack: $ack") },
                onMessageBuffered = { bufferedCount = it },
                onAlertReceived = { alert ->
                    pendingFormulaAlert = alert
                    // رسیدن نتیجه (موفق یا ناموفق) یعنی جریان ورود از راه دور تمام شده - ویزارد
                    // پاک می‌شود و به‌جایش همین دیالوگ نتیجه دیده می‌شود.
                    remoteEntryStep = null
                },
                onRemoteEntryStep = { step -> remoteEntryStep = step; remoteEntryStepRevision++ }
            )
        } else null
    }

    LaunchedEffect(wsManager) {
        wsManager?.connect()
    }

    DisposableEffect(wsManager) {
        onDispose { wsManager?.shutdown() }
    }

    // صدای هشدار شیرخشک - جدا از toneGenerator صفحه‌ی اسکن/جفت‌سازی، چون این دیالوگ سطح کل اپه
    // و ممکنه وقتی کاربر توی هر تبی هست پخش بشه. با موفق/ناموفق بودن پیام، دو تن کاملاً متفاوت
    // پخش می‌شه (ACK برای موفق، NACK برای ناموفق) تا بدون نگاه کردن به گوشی هم بشه فهمید نتیجه
    // چی بوده.
    val formulaAlertToneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }
    LaunchedEffect(pendingFormulaAlert) {
        pendingFormulaAlert?.let { alert ->
            val tone = if (alert.success) ToneGenerator.TONE_PROP_ACK else ToneGenerator.TONE_PROP_NACK
            try {
                formulaAlertToneGenerator.startTone(tone, 250)
            } catch (_: Exception) {
            }
        }
    }

    // --- هماهنگی خودکار تب‌ها با مراحل آموزش ---
    // چون حالا تنظیمات و تغییر سیستم توی تب «پنل کاربری» هستن (نه توی خود صفحه‌ی اسکنر)، وقتی
    // نوبت توضیح اون بخش‌ها می‌رسه، خود برنامه تب رو عوض می‌کنه؛ کاربر مجبور نیست خودش دنبالش
    // بگرده. بخش GOTO_HISTORY هم اینجا مدیریت می‌شه: بعد از یه اسکن واقعی، با کمی تأخیر خودش
    // می‌ره توی تب تاریخچه.
    LaunchedEffect(tutorialStep) {
        when (tutorialStep) {
            TutorialStep.SETTINGS_BUTTON -> currentScreen = AppScreen.USER_PANEL
            TutorialStep.TORCH_BUTTON -> currentScreen = AppScreen.SCANNER
            TutorialStep.GOTO_HISTORY -> {
                kotlinx.coroutines.delay(900)
                currentScreen = AppScreen.HISTORY
                tutorialStep = TutorialStep.FINISH
            }
            // دکمه‌ی «تغییر سیستم» دیگه توی پنل کاربری نیست، الان بالای تب اسکنه؛ پس این مرحله
            // از آموزش هم باید کاربر رو به همون تب ببره، نه پنل کاربری.
            TutorialStep.SWITCH_BUTTON -> currentScreen = AppScreen.SCANNER
            else -> {}
        }
    }

    val tabScreens = remember { setOf(AppScreen.SCANNER, AppScreen.HISTORY, AppScreen.USER_PANEL) }
    // آیا به یه سیستم وصل شدیم؟ تا وقتی وصل نشدیم، تب اسکن به‌جای دوربین بارکد، کارت
    // «اتصال به سیستم» رو نشون می‌ده؛ تب‌های تاریخچه و پنل کاربری همیشه در دسترسن.
    val isPaired = serverUrlState.value.isNotEmpty()

    Scaffold(
        modifier = Modifier.then(
            // وقتی دیالوگ هشدار شیرخشک باز است، کل پشت صفحه (همین Scaffold - یعنی هر تبی که الان
            // بازه) تار می‌شود تا خودِ پیام واضح‌تر و خواناتر دیده شود. بلور واقعی (RenderEffect)
            // فقط از اندروید ۱۲ (API 31) به بعد در دسترس است - دقیقاً همان محدودیتی که
            // GlassIconButton این فایل هم دارد؛ روی نسخه‌های قدیمی‌تر دیالوگ همچنان با همان سایه‌ی
            // تیره‌ی پیش‌فرض خودش (بدون بلور) نمایش داده می‌شود.
            if ((pendingFormulaAlert != null || remoteEntryStep != null) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer {
                    renderEffect = RenderEffect.createBlurEffect(24f, 24f, Shader.TileMode.CLAMP).asComposeRenderEffect()
                }
            } else Modifier
        ),
        bottomBar = {
            // وقتی آموزش در حال اجراست، نوار پایین مخفی می‌شه تا کاربر مجبور بشه مراحل رو پشت سر
            // هم طی کنه، نه اینکه با زدن یه تب دیگه از وسط آموزش بیرون بزنه.
            if (currentScreen in tabScreens && tutorialStep == TutorialStep.NONE) {
                AppBottomNavBar(
                    s = s,
                    currentScreen = currentScreen,
                    onSelect = { currentScreen = it }
                )
            }
        }
    ) { innerPadding ->
        // یه کراس‌فید ساده و نرم به‌جای اسلاید جهت‌دار قبلی - چون جابه‌جایی بین تب‌های نوار پایین
        // پرش بین جهت‌های مختلف نیست (بر خلاف مراحل خطی آموزش)، اسلاید حسش شلوغ و ناهماهنگ بود.
        AnimatedContent(
            targetState = currentScreen,
            modifier = Modifier.padding(innerPadding),
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + scaleIn(initialScale = 0.98f, animationSpec = tween(220)))
                    .togetherWith(fadeOut(animationSpec = tween(140)))
            },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                AppScreen.INTRO -> {
                    IntroScreen(
                        s = s,
                        onEnter = { goToMainFlow() }
                    )
                }
                AppScreen.PAIRING -> {
                    PairingScreen(
                        s = s,
                        tutorialStep = tutorialStep,
                        onTutorialStepChange = { tutorialStep = it },
                        onPaired = {
                            serverUrlState.value = sharedPrefs.getString("server_url", "") ?: ""
                            currentScreen = AppScreen.SCANNER
                        }
                    )
                }
                AppScreen.SCANNER -> {
                    // تا وقتی وصل نشدیم، همین تب اسکن کارت جفت‌سازی رو نشون می‌ده - نه یه صفحه‌ی
                    // جدا و مسدودکننده - پس نوار پایین (و در نتیجه تب‌های تاریخچه/پنل کاربری)
                    // همیشه در دسترس می‌مونه.
                    if (isPaired) {
                        ScannerScreen(
                            s = s,
                            history = scanHistory,
                            wsManager = wsManager,
                            connectionStatus = connectionStatus,
                            bufferedCount = bufferedCount,
                            onSwitchSystem = {
                                wsManager?.shutdown()
                                sharedPrefs.edit().remove("computer_ip").apply()
                                serverUrlState.value = ""
                            },
                            onExit = { (context as? Activity)?.finish() },
                            tutorialStep = tutorialStep,
                            onTutorialStepChange = { tutorialStep = it }
                        )
                    } else {
                        PairingScreen(
                            s = s,
                            tutorialStep = tutorialStep,
                            onTutorialStepChange = { tutorialStep = it },
                            onPaired = {
                                serverUrlState.value = sharedPrefs.getString("server_url", "") ?: ""
                            }
                        )
                    }
                }
                AppScreen.HISTORY -> {
                    HistoryScreen(
                        s = s,
                        history = scanHistory,
                        onBack = {
                            currentScreen = AppScreen.SCANNER
                        },
                        wsManager = wsManager,
                        tutorialStep = tutorialStep,
                        onTutorialStepChange = { newStep ->
                            tutorialStep = newStep
                            if (newStep == TutorialStep.NONE) {
                                sharedPrefs.edit().putBoolean("tutorial_completed", true).apply()
                            }
                        }
                    )
                }
                AppScreen.USER_PANEL -> {
                    // «تغییر سیستم» و «خروج» طبق درخواست از این صفحه برداشته شدن و رفتن بالای تب اسکن.
                    UserPanelScreen(
                        s = s,
                        currentLanguage = currentLanguage,
                        onLanguageChange = onLanguageChange,
                        onBack = {
                            currentScreen = AppScreen.SCANNER
                        },
                        tutorialStep = tutorialStep,
                        onTutorialStepChange = { newStep ->
                            tutorialStep = newStep
                            if (newStep == TutorialStep.NONE) {
                                sharedPrefs.edit().putBoolean("tutorial_completed", true).apply()
                            }
                        }
                    )
                }
            }
        }
    }

    // دیالوگ نتیجه‌ی ثبت شیرخشک - عمداً بیرون از Scaffold/AnimatedContent است تا مستقل از این‌که
    // کاربر روی کدام تب است (اسکن/تاریخچه/پنل کاربری)، همیشه بالای همه چیز نمایش داده شود.
    pendingFormulaAlert?.let { alert ->
        val photoBitmap = remember(alert.photoBase64) { decodeFormulaAlertPhoto(alert.photoBase64) }
        AlertDialog(
            onDismissRequest = { pendingFormulaAlert = null },
            containerColor = NocturneSurface,
            titleContentColor = NocturneText,
            textContentColor = NocturneTextMuted,
            title = { Text(alert.title, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    // اگر دسکتاپ عکس شیرخشک را همراه پیام فرستاده باشد (فقط برای ثبت‌های موفق)،
                    // همان عکسی که روی دسکتاپ توی دیالوگ «ثبت شد» دیده می‌شود، بالای متن پیام
                    // اینجا هم نشان داده می‌شود.
                    if (photoBitmap != null) {
                        Image(
                            bitmap = photoBitmap.asImageBitmap(),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 220.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color.White)
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                    Text(formulaAlertBodyAnnotatedString(alert.body))
                }
            },
            confirmButton = {
                TextButton(onClick = { pendingFormulaAlert = null }) {
                    Text(
                        s.okay,
                        color = if (alert.success) NocturneAccentPale else MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = if (alert.canRepeat) {
                {
                    // «ثبت مجدد»: به دسکتاپ اطلاع بده که قلم بعدی را با همان اطلاعات بیمار (که خودش
                    // نگه داشته) فقط با اسکن بارکد + کپچای تازه ثبت کند - دیالوگ بسته می‌شود و کاربر
                    // مستقیم برمی‌گردد به صفحه‌ی اسکن تا بارکد بعدی را بزند.
                    TextButton(onClick = {
                        wsManager?.sendRemoteEntryRepeatArm()
                        pendingFormulaAlert = null
                    }) {
                        Text(s.registerAgain, color = NocturneAccentPale)
                    }
                }
            } else null
        )
    }

    // ویزارد «ورود اطلاعات از راه دور» (فرم ثبت شیرخشک) - مثل دیالوگ بالا، عمداً بیرون از
    // Scaffold/AnimatedContent است تا مستقل از تب فعلی همیشه بالای همه چیز دیده شود.
    remoteEntryStep?.let { step ->
        RemoteFormulaEntryDialog(
            step = step,
            revision = remoteEntryStepRevision,
            s = s,
            onNext = { value -> wsManager?.sendRemoteEntryValue(step.barcode, step.stepId, value) },
            onSubmit = { wsManager?.sendRemoteEntrySubmit(step.barcode) },
            onBack = { wsManager?.sendRemoteEntryBack(step.barcode) },
            onDismiss = {
                // این فقط یک بستنِ محلیِ روی گوشی است - چیزی به دسکتاپ فرستاده نمی‌شود، چون طبق
                // تصمیم کاربر، فرم روی دسکتاپ دست‌نخورده باقی می‌ماند و «هر دو باز می‌مانند».
                remoteEntryStep = null
            }
        )
    }
}

// یک مرحله از فرم ثبت شیرخشک را روی گوشی نشان می‌دهد (عکس/نام محصول، یا یک فیلد ورودی، یا عکس
// کپچا + فیلد، یا فقط یک دکمه‌ی نهایی) - نگاه کنید به تعریف RemoteEntryStep برای معنی هر inputType.
// وقتی کاربر «بعدی»/«ثبت» را می‌زند، فقط مقدار را به دسکتاپ می‌فرستد؛ مرحله‌ی بعدی روی صفحه فقط
// وقتی عوض می‌شود که دسکتاپ واقعاً REMOTE_ENTRY_STEP جدیدی بفرستد - این کامپوننت خودش هیچ فرضی
// درباره‌ی ترتیب مراحل ندارد (آن منطق کاملاً سمت دسکتاپ است).
@Composable
fun RemoteFormulaEntryDialog(
    step: RemoteEntryStep,
    revision: Int,
    s: Strings,
    onNext: (String) -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    onDismiss: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    // نکته‌ی مهم: کلید همه‌ی remember/LaunchedEffect های این تابع عمداً «revision» است، نه
    // step.stepId. چون وقتی دسکتاپ همان مرحله را با محتوای تازه دوباره می‌فرستد (مثلاً «ثبت
    // نهایی» بعد از خطا دوباره با متن خطا فرستاده می‌شود ولی stepId هنوز «submit» است)، اگر کلید
    // فقط stepId بود، Compose آن را «هیچ تغییری نکرده» حساب می‌کرد و state هایی مثل
    // isWaitingForNextStep هیچ‌وقت ریست نمی‌شدند - نتیجه‌اش این بود که دکمه‌های «قبلی» و «ثبت»
    // بعد از اولین خطا برای همیشه غیرفعال می‌ماندند. revision با هر پیام REMOTE_ENTRY_STEP یکی
    // زیاد می‌شود، پس همیشه یک کلید واقعاً تازه است.
    // اگر دسکتاپ همراه این مرحله یک prefillValue فرستاده باشد (یعنی از قبل چیزی در همان کادر
    // دسکتاپ هست - مثلاً چون کاربر با «قبلی» به مرحله‌ای برگشته که قبلاً پرش کرده بود)، کادر گوشی
    // خالی شروع نمی‌شود؛ همان مقدار قبلی از قبل در آن هست تا لازم نباشد دوباره از صفر تایپ شود.
    var textValue by remember(revision) { mutableStateOf(if (step.inputType != "birthDate") step.prefillValue ?: "" else "") }
    // برای مرحله‌ی تاریخ تولد، درست مثل سه کادر روز/ماه/سال روی خودِ سیستم، سه فیلد جدا نگه
    // می‌داریم تا کاربر مجبور نباشد خودش تاریخ را با اسلش تایپ کند. prefillValue برای این مرحله با
    // فرمت YYYY/MM/DD می‌رسد (همان چیزی که خودمان موقع فرستادن می‌سازیم).
    val prefillDateParts = remember(revision) {
        if (step.inputType == "birthDate") step.prefillValue?.split("/")?.takeIf { it.size == 3 } else null
    }
    var dayValue by remember(revision) { mutableStateOf(prefillDateParts?.getOrNull(2)?.toIntOrNull()?.toString() ?: "") }
    var monthValue by remember(revision) { mutableStateOf(prefillDateParts?.getOrNull(1)?.toIntOrNull()?.toString() ?: "") }
    var yearValue by remember(revision) { mutableStateOf(prefillDateParts?.getOrNull(0)?.toIntOrNull()?.toString() ?: "") }
    var isWaitingForNextStep by remember { mutableStateOf(false) }
    LaunchedEffect(revision) { isWaitingForNextStep = false }

    // فیلد اصلیِ همین مرحله (کادر «روز» برای تاریخ تولد، یا تنها کادر متنی برای بقیه‌ی مراحل) -
    // به‌محض رسیدن مرحله، خودش فوکوس و صفحه‌کلید را باز می‌کند تا لازم نباشد کاربر اول روی کادر
    // بزند.
    val primaryFieldFocusRequester = remember(revision) { FocusRequester() }
    LaunchedEffect(revision) {
        if (step.inputType != "info" && step.inputType != "button") {
            delay(150)
            runCatching { primaryFieldFocusRequester.requestFocus() }
            keyboardController?.show()
        }
    }

    val photoBitmap = remember(step.photoBase64) { decodeFormulaAlertPhoto(step.photoBase64) }
    val captchaBitmap = remember(step.captchaImageBase64) { decodeFormulaAlertPhoto(step.captchaImageBase64) }
    val isBirthDateStep = step.inputType == "birthDate"
    val needsTextInput = step.inputType != "info" && step.inputType != "button" && !isBirthDateStep
    val isBirthDateComplete = dayValue.isNotBlank() && monthValue.isNotBlank() && yearValue.isNotBlank()
    val isConfirmEnabled = !isWaitingForNextStep &&
        (!needsTextInput || textValue.isNotBlank()) &&
        (!isBirthDateStep || isBirthDateComplete)

    // همان کاری که دکمه‌ی «بعدی»/«ثبت» انجام می‌دهد - هم از خودِ دکمه صدا زده می‌شود، هم از دکمه‌ی
    // Enter/تأیید کیبورد روی کادرهای تک‌مقداری (کد ملی، شماره نظام، موبایل، کپچا) تا لازم نباشد
    // بعد از تایپ حتماً روی دکمه هم انگشت بگذارند.
    fun confirmCurrentStep() {
        if (!isConfirmEnabled) return
        isWaitingForNextStep = true
        when {
            step.inputType == "button" -> onSubmit()
            isBirthDateStep -> {
                // فرمت YYYY/MM/DD - همان فرمتی که سمت دسکتاپ برای سه کادر روز/ماه/سال انتظار
                // می‌رود (نگاه کنید به HandleRemoteEntryValueFromPhone).
                val normalized = "%04d/%02d/%02d".format(
                    yearValue.toIntOrNull() ?: 0,
                    monthValue.toIntOrNull() ?: 0,
                    dayValue.toIntOrNull() ?: 0
                )
                onNext(normalized)
            }
            else -> onNext(textValue)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NocturneSurface,
        titleContentColor = NocturneText,
        textContentColor = NocturneTextMuted,
        title = { Text(step.label, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                if (step.hint.isNotBlank()) {
                    Text(step.hint, style = MaterialTheme.typography.bodySmall, color = NocturneTextMuted)
                    Spacer(Modifier.height(10.dp))
                }
                if (photoBitmap != null) {
                    Image(
                        bitmap = photoBitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (captchaBitmap != null) {
                    Image(
                        bitmap = captchaBitmap.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 170.dp, max = 240.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .padding(6.dp)
                    )
                    Spacer(Modifier.height(12.dp))
                }
                if (isBirthDateStep) {
                    // نکته‌ی مهم: کیبورد عددی (KeyboardType.Number) روی خیلی از گوشی‌ها اصلاً
                    // دکمه‌ی «بعدی»/Enter نشان نمی‌دهد یا IME action درخواستی (Next) را نادیده
                    // می‌گیرد، و تکیه به focusManager.moveFocus (که بر اساس موقعیت روی صفحه حدس
                    // می‌زند) هم همیشه قابل‌اعتماد نیست - به همین خاطر اینجا مستقیم با
                    // FocusRequester به کادر بعدی/قبلی می‌رویم که همیشه دقیقاً همان کادر مقصود را
                    // فوکوس می‌کند. جابه‌جایی هم دقیقاً مثل منطق خودِ دسکتاپ
                    // (TryAutoAdvanceTtacDateField در MainWindow.xaml.cs) کار می‌کند: بعد از ۲
                    // رقم، یا حتی بعد از فقط ۱ رقم اگر آن یک رقم از قبل کل مقدار معتبر را تعیین
                    // کند (مثلاً روز «5» یا ماه «9» - چون هیچ روز/ماه معتبری با آن رقم شروع
                    // نمی‌شود جز خودش).
                    val monthFocusRequester = remember(revision) { FocusRequester() }
                    val yearFocusRequester = remember(revision) { FocusRequester() }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = dayValue,
                            onValueChange = {
                                val digits = it.filter(Char::isDigit).take(2)
                                dayValue = digits
                                val day = digits.toIntOrNull()
                                if (day != null && day in 1..31 && (digits.length >= 2 || day >= 4)) {
                                    monthFocusRequester.requestFocus()
                                }
                            },
                            label = { Text(s.birthDay) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { monthFocusRequester.requestFocus() },
                                onDone = { monthFocusRequester.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(primaryFieldFocusRequester)
                        )
                        OutlinedTextField(
                            value = monthValue,
                            onValueChange = {
                                val digits = it.filter(Char::isDigit).take(2)
                                monthValue = digits
                                val month = digits.toIntOrNull()
                                if (month != null && month in 1..12 && (digits.length >= 2 || month >= 2)) {
                                    yearFocusRequester.requestFocus()
                                }
                            },
                            label = { Text(s.birthMonth) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                            keyboardActions = KeyboardActions(
                                onNext = { yearFocusRequester.requestFocus() },
                                onDone = { yearFocusRequester.requestFocus() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(monthFocusRequester)
                        )
                        OutlinedTextField(
                            value = yearValue,
                            onValueChange = {
                                val digits = it.filter(Char::isDigit).take(4)
                                yearValue = digits
                                if (digits.length == 4) focusManager.clearFocus()
                            },
                            label = { Text(s.birthYear) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                            // روی کادر سال (آخرین کادر تاریخ تولد)، زدن Enter/تأیید کیبورد - اگر هر
                            // سه کادر پر باشند - دقیقاً مثل زدن دکمه‌ی «بعدی» عمل می‌کند.
                            keyboardActions = KeyboardActions(
                                onDone = { focusManager.clearFocus(); confirmCurrentStep() },
                                onNext = { focusManager.clearFocus(); confirmCurrentStep() }
                            ),
                            modifier = Modifier
                                .weight(1.3f)
                                .focusRequester(yearFocusRequester)
                        )
                    }
                }
                if (needsTextInput) {
                    OutlinedTextField(
                        value = textValue,
                        onValueChange = { textValue = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            // کپچای ما همیشه فقط عدد است - پس هم برای مرحله‌ی «number» (کد ملی،
                            // شماره نظام، موبایل) و هم برای مرحله‌ی «captcha» کیبورد عددی باز شود.
                            keyboardType = if (step.inputType == "number" || step.inputType == "captcha") KeyboardType.Number else KeyboardType.Text,
                            imeAction = ImeAction.Done
                        ),
                        // کیبورد عددی معمولاً به‌جای «بعدی»، دکمه‌ی «تأیید»/Done نشان می‌دهد - هر دو
                        // را به همان عملِ دکمه‌ی «بعدی» وصل می‌کنیم تا زدن Enter همیشه کار کند.
                        keyboardActions = KeyboardActions(
                            onDone = { confirmCurrentStep() },
                            onNext = { confirmCurrentStep() }
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(primaryFieldFocusRequester)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = isConfirmEnabled,
                onClick = { confirmCurrentStep() }
            ) {
                Text(
                    if (step.inputType == "button") s.submitFormulaRemote else s.next,
                    color = NocturneAccentPale
                )
            }
        },
        dismissButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // مرحله‌ی «info» همیشه اولین مرحله است - قبل از آن چیزی برای برگشتن نیست.
                if (step.inputType != "info") {
                    TextButton(
                        enabled = !isWaitingForNextStep,
                        onClick = {
                            isWaitingForNextStep = true
                            onBack()
                        }
                    ) {
                        Text(s.previous, color = NocturneTextMuted)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(s.cancel, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    )
}

// نوار پایین به‌جای NavigationBar پیش‌فرض متریال (تمام‌عرض با متن زیر هر آیکون)، حالا یه کادر
// شناور با لبه‌ی کاملاً گرد و فقط آیکونه - بدون هیچ نوشته‌ای زیرش، هر سه تب هم توی یه کادر واحد.
@Composable
fun AppBottomNavBar(s: Strings, currentScreen: AppScreen, onSelect: (AppScreen) -> Unit) {
    val items = listOf(
        Triple(AppScreen.USER_PANEL, Icons.Default.Person, s.userPanel),
        Triple(AppScreen.SCANNER, Icons.Default.QrCodeScanner, s.scanTab),
        Triple(AppScreen.HISTORY, Icons.Default.History, s.history)
    )
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 14.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 10.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (screen, icon, label) ->
                val selected = currentScreen == screen
                val bgColor by animateColorAsState(
                    targetValue = if (selected) NavyPrimary else Color.Transparent,
                    animationSpec = tween(200),
                    label = "nav_item_bg"
                )
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(bgColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = LocalIndication.current
                        ) { onSelect(screen) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

// --- Components ---

@Composable
fun BrandingLogo(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_launcher_foreground),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(36.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text = "ScanBridge",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun GlassIconButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    iconColor: Color = Color.White,
    backgroundColor: Color = Color.White.copy(alpha = 0.15f)
) {
    Box(
        modifier = modifier
            .size(48.dp)
            .shadow(6.dp, CircleShape, spotColor = Color.Black.copy(alpha = 0.1f))
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .then(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Modifier.graphicsLayer {
                        renderEffect = RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP).asComposeRenderEffect()
                    }
                } else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun TileButton(
    title: String,
    icon: ImageVector,
    tileColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, label = "button_scale")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                onClick = onClick
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(tileColor, RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(Modifier.width(16.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.weight(1f))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.graphicsLayer { rotationZ = 180f }
                )
            }
        }
    }
}

@androidx.annotation.OptIn(ExperimentalGetImage::class)
@Composable
fun BaseScannerView(
    options: BarcodeScannerOptions,
    onBarcodeDetected: (String, Int) -> Unit,
    onCameraBound: (Camera) -> Unit = {},
    modifier: Modifier = Modifier,
    overlayContent: @Composable BoxScope.(isScanned: Boolean) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    var isScannedSuccessfully by remember { mutableStateOf(false) }
    var localScanCounter by remember { mutableIntStateOf(0) }

    val executor = remember { Executors.newSingleThreadExecutor() }
    val scanner = remember(options) { BarcodeScanning.getClient(options) }

    DisposableEffect(scanner) {
        onDispose {
            scanner.close()
            executor.shutdown()
        }
    }

    LaunchedEffect(localScanCounter) {
        if (localScanCounter > 0) {
            kotlinx.coroutines.delay(300)
            isScannedSuccessfully = false
        }
    }

    Box(modifier = modifier) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val imageAnalysis = ImageAnalysis.Builder()
                        .setTargetResolution(Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also {
                            it.setAnalyzer(executor) { imageProxy ->
                                try {
                                    val mediaImage = imageProxy.image
                                    if (mediaImage != null) {
                                        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                                        scanner.process(image)
                                            .addOnCompleteListener {
                                                imageProxy.close()
                                            }
                                            .addOnSuccessListener { barcodes ->
                                                barcodes.firstOrNull()?.let { b ->
                                                    if (!isScannedSuccessfully) {
                                                        isScannedSuccessfully = true
                                                        localScanCounter++
                                                        onBarcodeDetected(b.rawValue ?: "", b.format)
                                                    }
                                                }
                                            }
                                    } else {
                                        imageProxy.close()
                                    }
                                } catch (e: Exception) {
                                    Log.e("ScanBridge", "Analysis error", e)
                                    imageProxy.close()
                                }
                            }
                        }
                    try {
                        cameraProvider.unbindAll()
                        val cameraInstance = cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis)
                        onCameraBound(cameraInstance)
                    } catch (e: Exception) { Log.e("ScanBridge", "Binding failed", e) }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )
        overlayContent(isScannedSuccessfully)
    }
}

// یه کارت راهنمای کوچیک ته صفحه که در طول آموزش قدم‌به‌قدم استفاده می‌شه. اگه onNext
// داده بشه یه دکمه‌ی «بعدی» داره (برای مرحله‌های صرفاً توضیحی)؛ اگه null باشه یعنی این مرحله
// با یه اکشن واقعی کاربر (اسکن بارکد واقعی، زدن آیکون تاریخچه) جلو می‌ره، نه با دکمه.
@Composable
fun TutorialCard(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    nextLabel: String = "بعدی",
    onSkip: (() -> Unit)? = null,
    skipLabel: String = "فعلاً رد می‌کنم",
    onDismissTutorial: (() -> Unit)? = null
) {
    // ورود نرم: کارت با کمی تأخیرِ محسوس از پایین بالا می‌آید و محو-به-واضح می‌شود.
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }
    val progress by animateFloatAsState(if (entered) 1f else 0f, label = "tutorialEnter")
    val offsetY by animateDpAsState(if (entered) 0.dp else 36.dp, label = "tutorialOffsetY")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY.toPx()
                alpha = progress
                shape = RoundedCornerShape(24.dp)
                clip = true
                shadowElevation = 18.dp.toPx()
                ambientShadowColor = NocturneAccent.copy(alpha = 0.30f)
                spotShadowColor = NocturneAccent.copy(alpha = 0.45f)
            },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // چیپ گرادیانی آیکون — همان زبان بصری دکمه‌های لوکس
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Brush.linearGradient(listOf(GradientNavy, NocturneAccent))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "راهنمای قدم‌به‌قدم",
                        color = NocturneAccentPale,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        title,
                        color = NocturneText,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(NocturneDivider)
            )

            Spacer(Modifier.height(12.dp))

            Text(
                description,
                color = NocturneTextMuted,
                style = MaterialTheme.typography.bodySmall
            )

            if (onNext != null || onSkip != null) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
                ) {
                    // گزینه‌ی رد کردن این قدم — برای مراحلی که منتظر اکشن واقعی کاربر هستند
                    // (مثل اسکن بارکد) تا کاربر بدون اون اکشن هم گیر نیافتد.
                    if (onSkip != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(13.dp))
                                .background(NocturneAccentTint)
                                .clickable { onSkip() }
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                skipLabel,
                                color = NocturneAccentPale,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                    // دکمه‌ی «بعدی» — همان دکمه‌ی گرادیانی برجسته‌ی اپ
                    if (onNext != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(13.dp))
                                .background(Brush.linearGradient(listOf(GradientNavy, NocturneAccent)))
                                .clickable { onNext() }
                                .padding(horizontal = 22.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                nextLabel,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- اسکریم آموزش با افکت سوراخ‌نور (Spotlight) ---
// کل صفحه تاریک می‌شود و فقط حول دکمه‌ای که در حال توضیح داده شدن است یک سوراخ شفاف با
// حلقه‌ی نور آبی باقی می‌ماند — همان onboarding های مدرن. اگر anchorRect نباشد، کل صفحه
// به‌طور یکنواخت تاریک می‌شود و کارت آموزش روی آن شناور می‌ماند.
@Composable
fun BoxScope.TutorialSpotlightScrim(
    holeRect: Rect?,
    blockTouches: Boolean = false
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .matchParentSize()
            .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            .drawWithContent {
                drawContent()
                drawRect(Color.Black.copy(alpha = 0.62f))
                if (holeRect != null) {
                    val pad = 12.dp.toPx()
                    val topLeft = Offset(holeRect.left - pad, holeRect.top - pad)
                    val holeSize = androidx.compose.ui.geometry.Size(
                        holeRect.width + 2 * pad,
                        holeRect.height + 2 * pad
                    )
                    val corner = CornerRadius(22.dp.toPx())
                    // سوراخ شفاف داخل اسکریم
                    drawRoundRect(
                        color = Color.Black,
                        topLeft = topLeft,
                        size = holeSize,
                        cornerRadius = corner,
                        blendMode = BlendMode.Clear
                    )
                    // حلقه‌ی نور آبی دور سوراخ — چراغ‌قوه‌ی مجازی
                    drawRoundRect(
                        brush = Brush.linearGradient(
                            colors = listOf(GradientNavy, NocturneAccent),
                            start = topLeft,
                            end = Offset(topLeft.x + holeSize.width, topLeft.y + holeSize.height)
                        ),
                        topLeft = topLeft,
                        size = holeSize,
                        cornerRadius = corner,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
            .then(
                if (blockTouches) {
                    Modifier.clickable(interactionSource = interaction, indication = null) {}
                } else {
                    Modifier
                }
            )
    )
}

// جعبه‌ای که TutorialCard رو نگه می‌داره؛ اگه anchorRect داده بشه (یعنی موقعیت واقعی یه آیکون
// خاص رو داریم)، کارت درست زیر همون آیکون باز می‌شه، نه همیشه ته صفحه.
@Composable
fun BoxScope.TutorialOverlayBox(
    anchorRect: Rect?,
    gapPx: Float,
    content: @Composable () -> Unit
) {
    if (anchorRect != null) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset { IntOffset(x = 0, y = (anchorRect.bottom + gapPx).toInt()) }
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        ) {
            content()
        }
    } else {
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            content()
        }
    }
}

// --- Screens ---

// صفحه‌ی ورود به سبک یه هدر عکسی بزرگ + تیتر + دکمه‌ی بزرگ گرد، شبیه رفرنسی که کاربر فرستاد.
// فعلاً به‌جای عکس واقعی از خود لوگوی برنامه (با یه پس‌زمینه‌ی دایره‌ای رنگی برای پرکردن فضا)
// استفاده شده؛ وقتی کاربر عکس دلخواهش رو فرستاد، همین‌جا با یه Image ساده جایگزین می‌شه.
@Composable
fun IntroScreen(s: Strings, onEnter: () -> Unit) {
    var heroVisible by remember { mutableStateOf(false) }
    var textVisible by remember { mutableStateOf(false) }
    var buttonVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        heroVisible = true
        kotlinx.coroutines.delay(350)
        textVisible = true
        kotlinx.coroutines.delay(300)
        buttonVisible = true
    }

    val heroOffsetY by animateDpAsState(
        targetValue = if (heroVisible) 0.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_offset"
    )
    val heroAlpha by animateFloatAsState(
        targetValue = if (heroVisible) 1f else 0f,
        animationSpec = tween(500),
        label = "hero_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NocturneBackground)
    ) {
        // Hero image area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .offset(y = heroOffsetY)
                    .graphicsLayer { alpha = heroAlpha },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(260.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(NocturneAccent.copy(alpha = 0.16f), Color.Transparent)
                            ),
                            CircleShape
                        )
                )
                Box(
                    modifier = Modifier
                        .size(172.dp)
                        .graphicsLayer {
                            shape = RoundedCornerShape(40.dp)
                            clip = true
                            shadowElevation = 22.dp.toPx()
                            ambientShadowColor = NocturneAccent.copy(alpha = 0.35f)
                            spotShadowColor = NocturneAccent.copy(alpha = 0.45f)
                        }
                        .background(Color.White, RoundedCornerShape(40.dp))
                        .border(1.dp, NocturneAccent.copy(alpha = 0.15f), RoundedCornerShape(40.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_3d),
                        contentDescription = null,
                        modifier = Modifier.size(132.dp)
                    )
                }
            }
        }

        // Headline + subtitle + button area
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .padding(bottom = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(
                visible = textVisible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 3 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = s.appName.uppercase(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        color = NocturneAccentLight
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = s.introHeadline,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NocturneText,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = s.introSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = NocturneTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(28.dp))

            AnimatedVisibility(
                visible = buttonVisible,
                enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    PrimaryButton(
                        label = s.enter,
                        onClick = onEnter,
                        icon = Icons.AutoMirrored.Filled.ArrowForward
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = NocturneTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(s.introCaption, style = MaterialTheme.typography.bodySmall, color = NocturneTextMuted)
                    }
                }
            }
        }
    }
}

@Composable
fun PairingScreen(
    s: Strings,
    tutorialStep: TutorialStep = TutorialStep.NONE,
    onTutorialStepChange: (TutorialStep) -> Unit = {},
    onPaired: () -> Unit
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("ScanBridgePrefs", Context.MODE_PRIVATE) }
    val haptic = LocalHapticFeedback.current

    var errorMsg by remember { mutableStateOf("") }
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    var showRenameDialog by remember { mutableStateOf<JSONObject?>(null) }

    val options = remember {
        BarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_QR_CODE).build()
    }

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { hasCameraPermission = it }

    // برخلاف قبل، به‌محض باز شدن این صفحه دیگه خودکار درخواست دسترسی دوربین نمی‌فرستیم. کاربر
    // اول یه دکمه‌ی «شروع اسکن» می‌بینه و فقط وقتی خودش بزنه، دوربین باز و درخواست دسترسی (اگه
    // لازم باشه) نشون داده می‌شه.
    if (!hasCameraPermission) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(32.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(NocturneAccent.copy(alpha = 0.16f), Color.Transparent)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        modifier = Modifier.size(56.dp),
                        tint = NocturneAccentLight
                    )
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    s.notConnectedYet,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NocturneText,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    s.pairingInstruction,
                    style = MaterialTheme.typography.bodyMedium,
                    color = NocturneTextMuted,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(28.dp))
                PrimaryButton(
                    label = s.connectToSystem,
                    onClick = { launcher.launch(Manifest.permission.CAMERA) },
                    icon = Icons.Default.QrCodeScanner
                )
            }
        }
        return
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(750, easing = LinearEasing), RepeatMode.Reverse),
        label = "pulse_alpha"
    )

    BaseScannerView(
        options = options,
        onBarcodeDetected = { payload, _ ->
            try {
                val json = JSONObject(payload)
                if (json.optString("type") == "SCANBRIDGE_PAIR") {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showRenameDialog = json
                } else {
                    errorMsg = s.pairingErrorBrand
                }
            } catch (e: Exception) {
                errorMsg = s.pairingErrorFormat
            }
        }
    ) { isScanned ->
        // Instruction Glass Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.TopCenter)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.Black.copy(alpha = 0.35f)).border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp)),
                color = Color.Transparent
            ) {
                Text(
                    text = s.pairingInstruction,
                    color = Color.White,
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val boxSize = size.width * 0.7f
            val left = (size.width - boxSize) / 2f
            val top = (size.height - boxSize) / 2f

            drawRect(color = Color.Black.copy(alpha = 0.6f), size = androidx.compose.ui.geometry.Size(size.width, top))
            drawRect(color = Color.Black.copy(alpha = 0.6f), topLeft = Offset(0f, top + boxSize), size = androidx.compose.ui.geometry.Size(size.width, size.height - (top + boxSize)))
            drawRect(color = Color.Black.copy(alpha = 0.6f), topLeft = Offset(0f, top), size = androidx.compose.ui.geometry.Size(left, boxSize))
            drawRect(color = Color.Black.copy(alpha = 0.6f), topLeft = Offset(left + boxSize, top), size = androidx.compose.ui.geometry.Size(size.width - (left + boxSize), boxSize))

            val frameColor = if (errorMsg.isNotEmpty()) ErrorRed else if (isScanned) SuccessGreen else NocturneAccent.copy(alpha = pulseAlpha)
            drawRect(
                color = frameColor,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(boxSize, boxSize),
                style = Stroke(width = 4.dp.toPx())
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(s.pairingTitle, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center, style = MaterialTheme.typography.titleMedium)
            if (errorMsg.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }
    }

    showRenameDialog?.let { json ->
        val defaultName = json.getString("computerName")
        val computerId = json.getString("computerId")
        val savedCustomName = sharedPrefs.getString("custom_name_$computerId", defaultName) ?: defaultName
        var customName by remember { mutableStateOf(savedCustomName) }

        AlertDialog(
            onDismissRequest = { showRenameDialog = null },
            containerColor = NocturneSurface,
            titleContentColor = NocturneText,
            textContentColor = NocturneTextMuted,
            title = { Text(s.renameTitle, style = MaterialTheme.typography.titleLarge) },
            text = {
                Column {
                    OutlinedTextField(
                        value = customName,
                        onValueChange = { customName = it },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NocturneAccent,
                            unfocusedBorderColor = NocturneTextMuted.copy(alpha = 0.4f),
                            focusedTextColor = NocturneText,
                            unfocusedTextColor = NocturneText,
                            cursorColor = NocturneAccent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (tutorialStep == TutorialStep.NAME_SYSTEM) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = s.tutorialNameDesc,
                            style = MaterialTheme.typography.bodySmall,
                            color = NocturneTextMuted
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val ip = json.getString("ip")
                    val port = json.optInt("port", 5050)

                    sharedPrefs.edit()
                        .putString("computer_id", computerId)
                        .putString("computer_name", defaultName)
                        .putString("custom_computer_name", customName)
                        .putString("custom_name_$computerId", customName)
                        .putString("computer_ip", ip)
                        .putInt("computer_port", port)
                        .putString("server_url", "ws://$ip:$port")
                        .apply()

                    toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 150)
                    showRenameDialog = null
                    if (tutorialStep == TutorialStep.NAME_SYSTEM) {
                        onTutorialStepChange(TutorialStep.SETTINGS_BUTTON)
                    }
                    onPaired()
                }) { Text(s.confirm, color = NocturneAccentPale) }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = null }) { Text(s.cancel, color = NocturneTextMuted) }
            }
        )
    }
}

@Composable
fun ScannerScreen(
    s: Strings,
    history: MutableList<ScanHistoryItem>,
    wsManager: WebSocketManager?,
    connectionStatus: WebSocketManager.ConnectionStatus,
    bufferedCount: Int,
    onSwitchSystem: () -> Unit = {},
    onExit: () -> Unit = {},
    tutorialStep: TutorialStep = TutorialStep.NONE,
    onTutorialStepChange: (TutorialStep) -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val sharedPrefs = remember { context.getSharedPreferences("ScanBridgePrefs", Context.MODE_PRIVATE) }
    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_MUSIC, 100) }

    // فقط برای نمایش توی هدر خوش‌آمدگویی - ویرایشش الان توی تب «پنل کاربری» انجام می‌شه.
    val customComputerName = remember { sharedPrefs.getString("custom_computer_name", "---") ?: "---" }

    var lastScannedValue by remember { mutableStateOf("") }
    var lastScannedTime by remember { mutableLongStateOf(0L) }
    var scanEventCounter by remember { mutableIntStateOf(0) }
    var borderState by remember { mutableIntStateOf(0) } // 0: idle, 1: success, 2: failure
    var isTorchOn by remember { mutableStateOf(false) }
    var cameraControl: CameraControl? by remember { mutableStateOf(null) }
    var isLastScanVisible by remember { mutableStateOf(false) }

    // موقعیت واقعی روی صفحه‌ی آیکون چراغ‌قوه و دکمه‌ی تغییر سیستم - برای اینکه کارت راهنما
    // دقیقاً نزدیک همون آیکون باز بشه، نه همیشه ته صفحه.
    var torchIconRect by remember { mutableStateOf<Rect?>(null) }
    var switchIconRect by remember { mutableStateOf<Rect?>(null) }
    val density = LocalDensity.current
    val tutorialGapPx = with(density) { 10.dp.toPx() }

    // --- خودکارسازی مرحله‌ی چراغ‌قوه ---
    // وقتی نوبت توضیح این دکمه می‌رسه، خود برنامه یک‌بار چراغ‌قوه رو روشن/خاموش می‌کنه تا
    // کاربرد دکمه رو ببینه، به‌جای اینکه منتظر بمونیم خودش بزنه.
    LaunchedEffect(tutorialStep) {
        if (tutorialStep == TutorialStep.TORCH_BUTTON) {
            kotlinx.coroutines.delay(400)
            isTorchOn = true
            cameraControl?.enableTorch(true)
            kotlinx.coroutines.delay(700)
            isTorchOn = false
            cameraControl?.enableTorch(false)
        }
    }

    // Speedometer State
    var scansPerMinute by remember { mutableIntStateOf(0) }
    LaunchedEffect(history.size) {
        val now = System.currentTimeMillis()
        val oneMinuteAgo = now - 60000
        val recentScans = history.count { it.timestamp > oneMinuteAgo }
        scansPerMinute = recentScans
    }

    LaunchedEffect(scanEventCounter) {
        if (scanEventCounter > 0) {
            kotlinx.coroutines.delay(1200)
            borderState = 0
        }
    }

    val cameraScale by animateFloatAsState(
        targetValue = if (borderState == 1) 1.03f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "camera_pulse"
    )

    BackHandler {
        Toast.makeText(context, s.exitReminder, Toast.LENGTH_SHORT).show()
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Greeting Header - به‌جای لوگو و آیکون‌های پراکنده، یه هدر دوستانه‌تر شبیه رفرنس
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .graphicsLayer {
                        shape = RoundedCornerShape(16.dp)
                        clip = true
                        shadowElevation = 10.dp.toPx()
                        ambientShadowColor = NocturneAccent.copy(alpha = 0.30f)
                        spotShadowColor = NocturneAccent.copy(alpha = 0.40f)
                    }
                    .background(Color.White, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_3d),
                    contentDescription = null,
                    modifier = Modifier.size(38.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(s.greetingHello, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(customComputerName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.weight(1f))
            // دسترسی سریع به «تغییر سیستم» همین‌جا توی تب اسکن - بدون نیاز به رفتن به تب پنل
            // کاربری - چون این یکی از پرکاربردترین اکشن‌هاست.
            Box(
                modifier = Modifier
                    .onGloballyPositioned { coordinates -> switchIconRect = coordinates.boundsInRoot() }
            ) {
                IconActionButton(
                    icon = Icons.Default.SwapHoriz,
                    onClick = onSwitchSystem,
                    tone = IconActionTone.Warn,
                    contentDescription = s.switchSystem
                )
            }
            Spacer(Modifier.width(8.dp))
            // دکمه‌ی خروج از برنامه - طبق درخواست، از پنل کاربری برداشته شد و آورده شد بالای همین
            // تب اسکن — قرمز گرادیانی با سایه‌ی هم‌رنگ.
            IconActionButton(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                onClick = onExit,
                tone = IconActionTone.Danger,
                contentDescription = s.exitApp
            )
        }

        Spacer(Modifier.weight(0.5f))

        // Connection & Speed Card
        val connectionState = when (connectionStatus) {
            WebSocketManager.ConnectionStatus.CONNECTED -> ConnectionState.Live
            WebSocketManager.ConnectionStatus.CONNECTING -> ConnectionState.Connecting
            WebSocketManager.ConnectionStatus.DISCONNECTED -> ConnectionState.Offline
        }
        val connectionStatusLabel = when (connectionStatus) {
            WebSocketManager.ConnectionStatus.CONNECTED -> s.connected
            WebSocketManager.ConnectionStatus.CONNECTING -> s.connecting
            WebSocketManager.ConnectionStatus.DISCONNECTED -> s.disconnected
        }
        ConnectionCard(
            systemName = s.systemConnection,
            state = connectionState,
            scanSpeed = "$scansPerMinute SPM",
            statusLabel = connectionStatusLabel
        )

        Spacer(Modifier.height(16.dp))

        Spacer(Modifier.weight(0.8f))

        // Square Camera Box
        val borderColor by animateColorAsState(
            targetValue = when(borderState) {
                1 -> SuccessGreen
                2 -> ErrorRed
                else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
            },
            animationSpec = tween(400),
            label = "border_color"
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .aspectRatio(1f)
                .graphicsLayer {
                    scaleX = cameraScale
                    scaleY = cameraScale
                }
                .border(6.dp, borderColor, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
        ) {
            val options = remember {
                BarcodeScannerOptions.Builder().setBarcodeFormats(
                    Barcode.FORMAT_EAN_13, Barcode.FORMAT_EAN_8, Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E,
                    Barcode.FORMAT_CODE_128, Barcode.FORMAT_CODE_39, Barcode.FORMAT_QR_CODE,
                    Barcode.FORMAT_DATA_MATRIX, Barcode.FORMAT_PDF417
                ).build()
            }

            BaseScannerView(
                options = options,
                onBarcodeDetected = { barcode, format ->
                    val now = System.currentTimeMillis()
                    // اگر همان بارکد قبلی را ظرف یک ثانیه دوباره اسکن کنید، برنامه آن را نادیده می‌گیرد
                    // تا چند فریم پیاپی دوربین از یک نگه‌داشتن فیزیکی روی یک بارکد، چند بار ثبت نشود.
                    if (barcode != lastScannedValue || now - lastScannedTime > 1000) {
                        lastScannedValue = barcode
                        lastScannedTime = now
                        scanEventCounter++

                        val sent = wsManager?.sendBarcode(barcode) ?: false
                        if (sent) {
                            borderState = 1
                            isLastScanVisible = true
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                            history.add(0, ScanHistoryItem(barcode = barcode, time = time, format = format))
                            if (tutorialStep == TutorialStep.SCAN_BARCODE) {
                                onTutorialStepChange(TutorialStep.GOTO_HISTORY)
                            }
                        } else {
                            borderState = 2
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    }
                },
                onCameraBound = { cameraControl = it.cameraControl },
                modifier = Modifier.fillMaxSize()
            ) { }

            // Torch Toggle
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .onGloballyPositioned { coordinates -> torchIconRect = coordinates.boundsInRoot() }
            ) {
                TorchToggle(
                    on = isTorchOn,
                    onToggle = {
                        isTorchOn = !isTorchOn
                        cameraControl?.enableTorch(isTorchOn)
                    },
                    icon = if (isTorchOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Floating "Last Scan" Card
        val lastItem = history.firstOrNull()
        if (isLastScanVisible && lastItem != null) {
            AnimatedContent(
                targetState = lastItem,
                transitionSpec = {
                    // کارت جدید از پایین میاد بالا (مثل رفرنس کاربر) و کارت قبلی به‌جای رفتن به بالا،
                    // میره پایین و محو می‌شه (برخلاف ویدیوی رفرنس که کارت قبلی می‌رفت بالا).
                    (slideInVertically { it } + fadeIn()).togetherWith(slideOutVertically { it } + fadeOut())
                },
                label = "last_scan_anim"
            ) { item ->
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)) {
                    LastScanCard(
                        payload = item.barcode,
                        state = ScanResultState.Sent,
                        onResend = {
                            if (wsManager?.isConnected() == true) {
                                val sent = wsManager.sendBarcode(item.barcode)
                                if (sent) {
                                    Toast.makeText(context, s.resent, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, s.notConnected, Toast.LENGTH_SHORT).show()
                            }
                        },
                        elevated = true
                    )
                    IconButton(
                        onClick = { isLastScanVisible = false },
                        modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(24.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(16.dp), tint = NocturneTextMuted)
                    }
                }
            }
        } else if (lastItem == null) {
            Text(s.emptyHistory, color = NocturneTextMuted, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.weight(1f))
    }

    // --- Guided tutorial overlay ---
    // مرحله‌ی SETTINGS_BUTTON اینجا رندر نمی‌شه چون تنظیمات توی تب «پنل کاربری» هست، نه این
    // صفحه؛ اما SWITCH_BUTTON حالا همین‌جاست چون دکمه‌ی تغییر سیستم به این تب منتقل شد.
    val blockingStep = tutorialStep == TutorialStep.TORCH_BUTTON || tutorialStep == TutorialStep.SWITCH_BUTTON
    if (tutorialStep != TutorialStep.NONE) {
        val anchorRect = when (tutorialStep) {
            TutorialStep.TORCH_BUTTON -> torchIconRect
            TutorialStep.SWITCH_BUTTON -> switchIconRect
            else -> null
        }
        // افکت سوراخ‌نور: کل صفحه تاریک، فقط دکمه‌ی موردنظر داخل هاله‌ی روشن با حلقه‌ی آبی
        TutorialSpotlightScrim(holeRect = anchorRect, blockTouches = blockingStep)
        val dismissTutorial = {
            sharedPrefs.edit().putBoolean("tutorial_completed", true).apply()
            onTutorialStepChange(TutorialStep.NONE)
        }
        TutorialOverlayBox(anchorRect = anchorRect, gapPx = tutorialGapPx) {
            when (tutorialStep) {
                TutorialStep.TORCH_BUTTON -> TutorialCard(
                    title = s.tutorialTorchTitle,
                    description = s.tutorialTorchDesc,
                    nextLabel = s.next,
                    onNext = { onTutorialStepChange(TutorialStep.SCAN_BARCODE) },
                    onDismissTutorial = dismissTutorial
                )
                TutorialStep.SCAN_BARCODE -> TutorialCard(
                    title = s.tutorialScanTitle,
                    description = s.tutorialScanDesc,
                    onSkip = { onTutorialStepChange(TutorialStep.GOTO_HISTORY) },
                    onDismissTutorial = dismissTutorial
                )
                TutorialStep.GOTO_HISTORY -> TutorialCard(
                    title = s.tutorialGotoHistoryTitle,
                    description = s.tutorialGotoHistoryDesc,
                    onSkip = { onTutorialStepChange(TutorialStep.NONE) },
                    onDismissTutorial = dismissTutorial
                )
                TutorialStep.SWITCH_BUTTON -> TutorialCard(
                    title = s.tutorialSwitchTitle,
                    description = s.tutorialSwitchDesc,
                    nextLabel = s.gotIt,
                    onNext = { onTutorialStepChange(TutorialStep.NONE) },
                    onDismissTutorial = dismissTutorial
                )
                else -> {}
            }
        }
    }
    }
}

// برچسب کوتاه فرمت بارکد برای نمایش تو تاریخچه (فقط نمایشی، منطق اسکن/شناسایی دست‌نخورده‌ست)
private fun barcodeFormatLabel(format: Int): String = when (format) {
    Barcode.FORMAT_EAN_13 -> "EAN-13"
    Barcode.FORMAT_EAN_8 -> "EAN-8"
    Barcode.FORMAT_UPC_A -> "UPC-A"
    Barcode.FORMAT_UPC_E -> "UPC-E"
    Barcode.FORMAT_CODE_128 -> "CODE-128"
    Barcode.FORMAT_CODE_39 -> "CODE-39"
    Barcode.FORMAT_QR_CODE -> "QR"
    Barcode.FORMAT_DATA_MATRIX -> "Data Matrix"
    Barcode.FORMAT_PDF417 -> "PDF417"
    else -> "---"
}

@Composable
fun HistoryScreen(
    s: Strings,
    history: MutableList<ScanHistoryItem>,
    onBack: () -> Unit,
    wsManager: WebSocketManager?,
    tutorialStep: TutorialStep = TutorialStep.NONE,
    onTutorialStepChange: (TutorialStep) -> Unit = {}
) {
    val context = LocalContext.current

    BackHandler { onBack() }

    val todayCount = remember(history.size) {
        val cal = java.util.Calendar.getInstance()
        cal.set(java.util.Calendar.HOUR_OF_DAY, 0)
        cal.set(java.util.Calendar.MINUTE, 0)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val startOfDay = cal.timeInMillis
        history.count { it.timestamp >= startOfDay }
    }
    val totalCount = history.size

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(s.history, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)

            var showClearDialog by remember { mutableStateOf(false) }

            IconActionButton(
                icon = Icons.Default.DeleteSweep,
                onClick = { if (history.isNotEmpty()) showClearDialog = true },
                contentDescription = s.clearHistory
            )

            if (showClearDialog) {
                AlertDialog(
                    onDismissRequest = { showClearDialog = false },
                    title = { Text(s.clearHistory) },
                    text = { Text("آیا از پاک کردن تمام موارد اسکن شده اطمینان دارید؟") },
                    confirmButton = {
                        TextButton(onClick = {
                            history.clear()
                            showClearDialog = false
                            Toast.makeText(context, s.resent, Toast.LENGTH_SHORT).show()
                        }) { Text(s.delete) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showClearDialog = false }) { Text("انصراف") }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatTile(value = todayCount.toString(), label = s.todayScans, modifier = Modifier.weight(1f))
            StatTile(value = totalCount.toString(), label = s.totalScans, modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(64.dp), tint = NocturneTextMuted)
                    Spacer(Modifier.height(16.dp))
                    Text(s.emptyHistory, color = NocturneTextMuted, style = MaterialTheme.typography.bodyLarge)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(history, key = { it.id }) { item ->
                    var isSuccess by remember { mutableStateOf(false) }

                    LaunchedEffect(isSuccess) {
                        if (isSuccess) {
                            kotlinx.coroutines.delay(800)
                            isSuccess = false
                        }
                    }

                    ScanRow(
                        time = item.time,
                        format = barcodeFormatLabel(item.format),
                        payload = item.barcode,
                        confirmed = isSuccess,
                        onResend = {
                            if (wsManager?.isConnected() == true) {
                                val sent = wsManager.sendBarcode(item.barcode)
                                if (sent) {
                                    isSuccess = true
                                    Toast.makeText(context, s.resent, Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(context, s.notConnected, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }
            }
        }
    }

    if (tutorialStep == TutorialStep.FINISH) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {}
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            TutorialCard(
                title = s.tutorialFinishTitle,
                description = s.tutorialFinishDesc,
                nextLabel = s.next,
                onNext = {
                    // خود برنامه (توی MainApp) با تغییر این مرحله، خودکار به تب «پنل کاربری»
                    // می‌ره تا توضیح «تغییر سیستم» رو اونجا نشون بده.
                    onTutorialStepChange(TutorialStep.SWITCH_BUTTON)
                },
                onDismissTutorial = { onTutorialStepChange(TutorialStep.NONE) }
            )
        }
    }
    }
}

// آدرسی که برنامه برای بررسی «پیام‌های آپدیت» بهش سر می‌زنه. باید یه فایل JSON با این ساختار
// اونجا آپلود بشه: {"version": "1.1.0", "message": "توضیح آپدیت", "url": "لینک دانلود یا صفحه‌ی دانلود"}
// اگه نسخه‌ی داخل این فایل با نسخه‌ی نصب‌شده‌ی برنامه فرق کنه، یه پیام جدید توی پنل کاربری نشون داده می‌شه.
const val UPDATE_CHECK_URL = "https://scanbridge.ir/app/update.json"

data class UpdateMessage(
    val version: String,
    val message: String,
    val url: String
)

// صفحه‌ی راهنمای کامل برنامه — مشابه راهنمای سایت اما بومی و با جای اسکرین‌شات.
// اسکرین‌شات‌ها بعداً به‌صورت فایل‌های guide_connect / guide_scan / ... در پوشه‌ی
// drawable-nodpi اضافه می‌شوند؛ تا آن موقع جای هر کدام کادر «به‌زودی» نمایش داده می‌شود
// (بدون نیاز به تغییر کد، چون با getIdentifier پویا پیدا می‌شوند).
private data class GuideSection(
    val title: String,
    val description: String,
    val drawableName: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
private fun GuideScreenContent(s: Strings) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sections = listOf(
        GuideSection("۱. اتصال به سیستم", "در تب اسکن، کارت «اتصال به سیستم» را بزنید و QR نمایش‌داده‌شده روی ویندوز را با دوربین گوشی اسکن کنید. گوشی باید در همان وای‌فای سیستم باشد.", "guide_connect", Icons.Default.QrCode2),
        GuideSection("۲. اسکن بارکد", "دوربین را روی بارکد بگیرید؛ ثبت خودکار انجام می‌شود، نتیجه در کارت «آخرین اسکن» می‌آید و برای سیستم فرستاده می‌شود.", "guide_scan", Icons.Default.CenterFocusStrong),
        GuideSection("۳. چراغ‌غشو", "در محیط کم‌نور، آیکون فلاش کنار دوربین را بزنید تا نور روشن شود؛ دوباره بزنید خاموش می‌شود.", "guide_torch", Icons.Default.FlashlightOn),
        GuideSection("۴. تاریخچه و ارسال مجدد", "تب «تاریخچه» همه‌ی اسکن‌ها را با ساعت و نوع نشان می‌دهد. با دکمه‌ی ارسال مجدد می‌توانید هر بارکد را دوباره برای سیستم بفرستید.", "guide_history", Icons.Default.History),
        GuideSection("۵. پنل کاربری", "تغییر نام سیستم، تغییر زبان، بررسی بروزرسانی، پیام‌ها و همین راهنما — همه در تب «پنل کاربری» جمع شده‌اند.", "guide_panel", Icons.Default.Person),
        GuideSection("۶. حالت داروخانه", "اینترنت گوشی را به‌کلی قطع می‌کند و فقط ارتباط با سیستم روی شبکه‌ی محلی باز می‌ماند — مناسب داروخانه‌هایی که نمی‌خواهند پرسنل اینترنت داشته باشند.", "guide_pharmacy", Icons.Default.WifiOff),
        GuideSection("۷. تغییر سیستم و خروج", "با دکمه‌ی نارنجی بالا-چپِ تب اسکن، گوشی را به سیستم دیگری وصل کنید. دکمه‌ی قرمز کنارش هم از برنامه خارج می‌شود.", "guide_switch", Icons.Default.SwapHoriz),
        GuideSection("۸. تغییر زبان", "از پنل کاربری، بخش «زبان»، بین فارسی و English انتخاب کنید — همه‌ی متن‌های برنامه همان لحظه عوض می‌شوند.", "guide_language", Icons.Default.Language)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        sections.forEach { section ->
            Spacer(Modifier.height(14.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        shape = RoundedCornerShape(20.dp)
                        clip = true
                        shadowElevation = 8.dp.toPx()
                        ambientShadowColor = NocturneAccent.copy(alpha = 0.18f)
                        spotShadowColor = NocturneAccent.copy(alpha = 0.28f)
                    }
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Brush.linearGradient(listOf(GradientNavy, NocturneAccent))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(section.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Text(section.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = NocturneText)
                }
                Spacer(Modifier.height(8.dp))
                Text(section.description, style = MaterialTheme.typography.bodySmall, color = NocturneTextMuted)

                Spacer(Modifier.height(12.dp))
                val resId = remember(section.drawableName) {
                    context.resources.getIdentifier(section.drawableName, "drawable", context.packageName)
                }
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = section.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, NocturneDivider, RoundedCornerShape(14.dp))
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(NocturneAccentTint)
                            .border(1.dp, NocturneDivider, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(s.guideScreenshotSoon, style = MaterialTheme.typography.bodySmall, color = NocturneAccentPale)
                    }
                }
            }
        }
        Spacer(Modifier.height(28.dp))
    }
}

// صفحه‌ی «پنل کاربری» - همه‌چیزهایی که قبلاً توی دیالوگ تنظیمات و کارت‌های پراکنده‌ی صفحه‌ی
// اسکنر بودن (تغییر نام سیستم، زبان، بروزرسانی) الان همگی اینجا جمع شدن. دکمه‌ی «تغییر سیستم»
// و «خروج» طبق درخواست به بالای تب اسکن منتقل شدن.
@Composable
fun UserPanelScreen(
    s: Strings,
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    onBack: () -> Unit,
    tutorialStep: TutorialStep = TutorialStep.NONE,
    onTutorialStepChange: (TutorialStep) -> Unit = {}
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("ScanBridgePrefs", Context.MODE_PRIVATE) }

    var customComputerName by remember { mutableStateOf(sharedPrefs.getString("custom_computer_name", "---") ?: "---") }
    var showEditNameDialog by remember { mutableStateOf(false) }

    // --- بررسی بروزرسانی (شبیه‌سازی‌شده) ---
    var updateCheckCounter by remember { mutableIntStateOf(0) }
    var isCheckingUpdate by remember { mutableStateOf(false) }
    var updateResultText by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(updateCheckCounter) {
        if (updateCheckCounter > 0) {
            isCheckingUpdate = true
            updateResultText = null
            kotlinx.coroutines.delay(1000)
            isCheckingUpdate = false
            updateResultText = s.latestVersionMsg
        }
    }

    // --- بخش «پیام‌ها»: وقتی نسخه‌ی جدید روی سایت گذاشته بشه، اینجا نمایش داده می‌شه ---
    var showMessagesScreen by remember { mutableStateOf(false) }
    var showGuideScreen by remember { mutableStateOf(false) }
    var updateMessage by remember { mutableStateOf<UpdateMessage?>(null) }
    var isLoadingMessages by remember { mutableStateOf(false) }
    val currentVersionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }
    val hasNewMessage = updateMessage != null && updateMessage?.version != currentVersionName

    fun fetchUpdateMessage() {
        isLoadingMessages = true
        val client = OkHttpClient()
        val request = Request.Builder().url(UPDATE_CHECK_URL).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: java.io.IOException) {
                isLoadingMessages = false
            }
            override fun onResponse(call: Call, response: Response) {
                try {
                    val body = response.body?.string() ?: ""
                    val json = JSONObject(body)
                    updateMessage = UpdateMessage(
                        version = json.optString("version", ""),
                        message = json.optString("message", ""),
                        url = json.optString("url", "")
                    )
                } catch (e: Exception) {
                    Log.e("ScanBridge", "Update check parse error", e)
                } finally {
                    isLoadingMessages = false
                }
            }
        })
    }

    LaunchedEffect(Unit) { fetchUpdateMessage() }

    BackHandler {
        if (showGuideScreen) showGuideScreen = false
        else if (showMessagesScreen) showMessagesScreen = false else onBack()
    }

    // این مرحله از آموزش (توضیح تنظیمات) به‌جای دیالوگ و اسکرین مسدودکننده، یه کارت راهنمای
    // درون‌صفحه‌ای با دکمه‌های خودش (بعدی/رد کردن) داره؛ برای همین دکمه‌ی ثابتِ رد کردن پایین
    // این تابع، توی همین مرحله مخفی می‌شه تا تکراری نباشه.
    val isSettingsTutorial = tutorialStep == TutorialStep.SETTINGS_BUTTON

    Box(modifier = Modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(NocturneAccentTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Person, contentDescription = null, tint = NocturneAccentLight, modifier = Modifier.size(30.dp))
            }
            Spacer(Modifier.width(14.dp))
            Text(s.userPanel, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(Modifier.height(20.dp))

        // کارت راهنمای درون‌صفحه‌ای مرحله‌ی تنظیمات - جایگزین دیالوگ قبلی
        if (isSettingsTutorial) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NocturneAccent),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(s.tutorialSettingsTitle, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Spacer(Modifier.height(4.dp))
                            Text(s.tutorialSettingsDesc, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { onTutorialStepChange(TutorialStep.NONE) }) {
                            Text(s.skipTutorial, color = Color.White.copy(alpha = 0.75f))
                        }
                        Spacer(Modifier.width(8.dp))
                        Button(
                            onClick = { onTutorialStepChange(TutorialStep.TORCH_BUTTON) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = NocturneAccent)
                        ) { Text(s.next) }
                    }
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        // نام سیستم
        SettingRow(
            icon = Icons.Default.Computer,
            title = customComputerName,
            subtitle = s.renameTitle,
            onClick = { showEditNameDialog = true },
            trailing = {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = s.editSystemName,
                    tint = NocturneTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        )

        Spacer(Modifier.height(16.dp))

        // زبان
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NocturneSurface, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(NocturneAccentTint, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = NocturneAccentLight, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Text(s.language, style = MaterialTheme.typography.titleSmall, color = NocturneText)
            }
            Spacer(Modifier.height(12.dp))
            LanguageChoice(
                options = listOf("فارسی", "English"),
                selected = if (currentLanguage == AppLanguage.FA) "فارسی" else "English",
                onSelect = { label -> onLanguageChange(if (label == "فارسی") AppLanguage.FA else AppLanguage.EN) }
            )
        }

        Spacer(Modifier.height(16.dp))

        // حالت داروخانه (فقط شبکه محلی) — قطع کامل اینترنت گوشی با VPN محلی، بدون روت
        var lanOnlyChecked by remember { mutableStateOf(LanOnlyVpnService.isRunning(context)) }
        val vpnConsentLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == android.app.Activity.RESULT_OK) {
                LanOnlyVpnService.start(context)
                lanOnlyChecked = true
            }
        }
        // اگر کاربر از تنظیمات سریع اندروید VPN را خاموش کرد، وضعیت کلید هم به‌روز شود
        LaunchedEffect(Unit) {
            while (true) {
                kotlinx.coroutines.delay(2000)
                lanOnlyChecked = LanOnlyVpnService.isRunning(context)
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NocturneSurface, RoundedCornerShape(14.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).background(NocturneAccentTint, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.WifiOff, contentDescription = null, tint = NocturneAccentLight, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(s.pharmacyMode, style = MaterialTheme.typography.titleSmall, color = NocturneText)
                    Text(
                        s.pharmacyModeDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = NocturneTextMuted
                    )
                }
                Spacer(Modifier.width(8.dp))
                Switch(
                    checked = lanOnlyChecked,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            val prepareIntent = VpnService.prepare(context)
                            if (prepareIntent != null) {
                                vpnConsentLauncher.launch(prepareIntent)
                            } else {
                                LanOnlyVpnService.start(context)
                                lanOnlyChecked = true
                            }
                        } else {
                            LanOnlyVpnService.stop(context)
                            lanOnlyChecked = false
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // نسخه و بروزرسانی
        SettingRow(
            icon = Icons.Default.Update,
            title = "1.0.0" + if (updateResultText != null) " · $updateResultText" else "",
            subtitle = s.version,
            trailing = {
                if (isCheckingUpdate) {
                    Text(s.checking, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
                } else {
                    IconActionButton(
                        icon = Icons.Default.Refresh,
                        onClick = { updateCheckCounter++ },
                        contentDescription = s.checkUpdate
                    )
                }
            }
        )

        Spacer(Modifier.height(16.dp))

        // پیام‌ها
        SettingRow(
            icon = Icons.Default.Notifications,
            title = s.messages,
            subtitle = if (hasNewMessage) s.newUpdateAvailable else s.noNewMessages,
            onClick = { showMessagesScreen = true },
            showUnreadBadge = hasNewMessage
        )

        Spacer(Modifier.height(16.dp))

        // راهنمای کامل برنامه
        SettingRow(
            icon = Icons.Default.MenuBook,
            title = s.guideTitle,
            subtitle = s.guideSubtitle,
            onClick = { showGuideScreen = true }
        )

        Spacer(Modifier.height(16.dp))

        // وبسایت
        SettingRow(
            icon = Icons.Default.Public,
            title = s.website,
            subtitle = "scanbridge.ir",
            onClick = {
                try {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://scanbridge.ir")))
                } catch (e: Exception) {
                    Log.e("ScanBridge", "Could not open website", e)
                }
            }
        )

        Spacer(Modifier.height(24.dp))
    }

    // --- صفحه‌ی «راهنما» - کامل، اسکرول‌شونده، با جای اسکرین‌شات هر بخش ---
    if (showGuideScreen) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showGuideScreen = false }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = NocturneText)
                }
                Column {
                    Text(s.guideTitle, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = NocturneText)
                    Text(s.guideSubtitle, style = MaterialTheme.typography.bodySmall, color = NocturneTextMuted)
                }
            }
            GuideScreenContent(s = s)
        }
    }

    // --- صفحه‌ی «پیام‌ها» - روی همین تب، به‌جای رفتن به یه مسیر جداگونه توی برنامه ---
    if (showMessagesScreen) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconActionButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    onClick = { showMessagesScreen = false },
                    contentDescription = null
                )
                Spacer(Modifier.width(4.dp))
                Text(s.messages, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(20.dp))
            when {
                isLoadingMessages -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = NocturneAccent)
                    }
                }
                updateMessage == null -> {
                    Text(s.noNewMessages, color = NocturneTextMuted, style = MaterialTheme.typography.bodyMedium)
                }
                else -> {
                    val msg = updateMessage!!
                    UpdateCard(
                        version = if (hasNewMessage) msg.version else currentVersionName,
                        note = if (msg.message.isNotEmpty()) msg.message else (if (hasNewMessage) s.newUpdateAvailable else s.latestVersionMsg),
                        available = hasNewMessage,
                        onUpdate = if (hasNewMessage && msg.url.isNotEmpty()) {
                            {
                                try {
                                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(msg.url)))
                                } catch (e: Exception) {
                                    Log.e("ScanBridge", "Could not open update url", e)
                                }
                            }
                        } else null
                    )
                }
            }
        }
    }

    // دکمه‌ی ثابتِ رد کردن آموزش - توی مرحله‌ی تنظیمات مخفیه چون اون کارت خودش دکمه‌ی
    // «رد کردن»/«بعدی» مخصوص خودش رو داره.
    if (tutorialStep != TutorialStep.NONE && !isSettingsTutorial) {
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 28.dp, end = 12.dp),
            shape = RoundedCornerShape(50),
            color = Color.Black.copy(alpha = 0.55f)
        ) {
            TextButton(
                onClick = { onTutorialStepChange(TutorialStep.NONE) },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text(s.skipTutorial, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodySmall, color = Color.White)
            }
        }
    }
    }

    if (showEditNameDialog) {
        var editedName by remember { mutableStateOf(customComputerName) }
        NamingDialog(
            value = editedName,
            onValueChange = { editedName = it },
            onConfirm = {
                val computerId = sharedPrefs.getString("computer_id", null)
                val editor = sharedPrefs.edit().putString("custom_computer_name", editedName)
                if (computerId != null) {
                    editor.putString("custom_name_$computerId", editedName)
                }
                editor.apply()
                customComputerName = editedName
                showEditNameDialog = false
            },
            onCancel = { showEditNameDialog = false },
            title = s.editSystemName,
            confirmLabel = s.confirm,
            cancelLabel = s.cancel,
            requireNonBlank = false
        )
    }
}
