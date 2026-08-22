package com.example.scanbridge.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.NocturneAccent
import com.example.scanbridge.ui.theme.NocturneAccentLight
import com.example.scanbridge.ui.theme.NocturneAccentPale
import com.example.scanbridge.ui.theme.NocturneDivider
import com.example.scanbridge.ui.theme.NocturneDividerAccent
import com.example.scanbridge.ui.theme.NocturneSurface
import com.example.scanbridge.ui.theme.NocturneText
import com.example.scanbridge.ui.theme.NocturneTextMuted

/**
 * ConnectionCard — کارت وضعیت اتصال روی صفحه‌ی اسکنر. طبق بخش ۲.۲ اسپک: حاشیه در حالت
 * وصل به رنگ اکسنت ۳۰٪ می‌شه، در غیر این‌صورت رنگ دیوایدر معمولی.
 */
@Composable
fun ConnectionCard(
    systemName: String,
    state: ConnectionState,
    scanSpeed: String,
    onSwitch: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    statusLabel: String? = null
) {
    val borderColor = if (state == ConnectionState.Live) NocturneDividerAccent else NocturneDivider
    val resolvedStatusLabel = statusLabel ?: when (state) {
        ConnectionState.Live -> "متصل"
        ConnectionState.Connecting -> "در حال اتصال…"
        ConnectionState.Offline -> "قطع است"
    }
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NocturneSurface,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = systemName, style = MaterialTheme.typography.titleMedium, color = NocturneText)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusDot(state)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 6.dp))
                    Text(text = resolvedStatusLabel, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
                    androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 8.dp))
                    Text(text = scanSpeed, style = MaterialTheme.typography.labelMedium, color = NocturneAccentPale)
                }
            }
            if (onSwitch != null) {
                SecondaryButton(label = "تعویض", onClick = onSwitch)
            }
        }
    }
}

/**
 * StatTile — کاشی آماری کوچیک (تعداد اسکن امروز، سرعت، درصد تحویل...). sparkline اختیاریه و
 * فعلاً فقط جای رزرو شده (پیاده‌سازی نمودار در مراحل بعدی که واقعاً لازم شد).
 */
@Composable
fun StatTile(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    accent: Boolean = false
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = NocturneSurface,
        border = BorderStroke(1.dp, NocturneDivider)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = if (accent) NocturneAccentLight else NocturneText
            )
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
        }
    }
}

/** وضعیت‌های LastScanCard طبق بخش ۲.۲ اسپک. */
enum class ScanResultState { Identified, Sent, Queued, Failed }

/**
 * LastScanCard — کارت آخرین اسکن، زیر دوربین. دکمه‌ی Resend همیشه اکشن اصلیه؛ حتی حالت
 * failed هم قرمز نیست (طبق اسپک، تگ خنثی).
 */
@Composable
fun LastScanCard(
    payload: String,
    state: ScanResultState,
    onResend: () -> Unit,
    modifier: Modifier = Modifier,
    elevated: Boolean = false
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NocturneSurface,
        border = BorderStroke(1.dp, if (elevated) NocturneDividerAccent else NocturneDivider),
        tonalElevation = if (elevated) 4.dp else 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = payload,
                    style = MaterialTheme.typography.titleSmall,
                    color = NocturneText,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 4.dp))
                val (tagText, tagVariant) = when (state) {
                    ScanResultState.Identified -> "شناسایی شد" to TagVariant.Outline
                    ScanResultState.Sent -> "ارسال شد" to TagVariant.Accent
                    ScanResultState.Queued -> "در صف" to TagVariant.Outline
                    ScanResultState.Failed -> "ناموفق" to TagVariant.Neutral
                }
                Tag(text = tagText, variant = tagVariant)
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 10.dp))
            IconActionButton(
                icon = Icons.Default.Refresh,
                onClick = onResend,
                tone = IconActionTone.Accent,
                contentDescription = "ارسال دوباره"
            )
        }
    }
}

/**
 * UpdateCard — کارت آپدیت تو صفحه‌ی پیام‌ها. available: حاشیه‌ی اکسنت، up-to-date: ساده.
 */
@Composable
fun UpdateCard(
    version: String,
    note: String,
    modifier: Modifier = Modifier,
    available: Boolean = true,
    onUpdate: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = NocturneSurface,
        border = BorderStroke(1.dp, if (available) NocturneAccent.copy(alpha = 0.4f) else NocturneDivider)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Tag(text = version, variant = if (available) TagVariant.Accent else TagVariant.Neutral)
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 8.dp))
            Text(text = note, style = MaterialTheme.typography.bodyMedium, color = NocturneTextMuted)
            if (available && onUpdate != null) {
                androidx.compose.foundation.layout.Spacer(Modifier.padding(top = 12.dp))
                PrimaryButton(label = "بروزرسانی", onClick = onUpdate)
            }
        }
    }
}
