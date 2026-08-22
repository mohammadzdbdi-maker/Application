package com.example.scanbridge.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.NocturneAccent
import com.example.scanbridge.ui.theme.NocturneAccentContainer
import com.example.scanbridge.ui.theme.NocturneAccentLight
import com.example.scanbridge.ui.theme.NocturneAccentPale
import com.example.scanbridge.ui.theme.NocturneAccentTint
import com.example.scanbridge.ui.theme.NocturneDivider
import com.example.scanbridge.ui.theme.NocturneNeutral
import com.example.scanbridge.ui.theme.NocturneSurface
import com.example.scanbridge.ui.theme.NocturneText
import com.example.scanbridge.ui.theme.NocturneTextMuted

/**
 * ScanRow — ردیف تاریخچه. طبق اسپک: زمان + فرمت + payload مونو + دکمه‌ی دایره‌ای Resend که
 * برای ۹۰۰ میلی‌ثانیه به علامت تیک تبدیل می‌شه (منطق انیمیشن رو صفحه‌ی History موقع استفاده پیاده می‌کنه).
 */
@Composable
fun ScanRow(
    time: String,
    format: String,
    payload: String,
    onResend: () -> Unit,
    modifier: Modifier = Modifier,
    confirmed: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NocturneSurface, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row {
                Text(text = time, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
                androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 8.dp))
                Tag(text = format, variant = TagVariant.Outline)
            }
            Text(
                text = payload,
                style = MaterialTheme.typography.bodyMedium,
                color = NocturneText,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
        IconActionButton(
            icon = if (confirmed) Icons.Default.Check
            else Icons.Default.Refresh,
            onClick = onResend,
            tone = if (confirmed) IconActionTone.Accent else IconActionTone.Neutral,
            contentDescription = "ارسال دوباره"
        )
    }
}

/**
 * SettingRow — ردیف تنظیمات پنل کاربری (آیکون + عنوان + زیرعنوان اختیاری + عنصر انتهایی).
 * trailing می‌تونه شورون، تگ، یا هر Composable اکشن دیگه‌ای باشه.
 */
@Composable
fun SettingRow(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    showUnreadBadge: Boolean = false,
    trailing: @Composable (() -> Unit)? = null
) {
    val rowModifier = modifier
        .fillMaxWidth()
        .then(
            if (onClick != null)
                Modifier.clickable(onClick = onClick)
            else Modifier
        )
        .background(NocturneSurface, RoundedCornerShape(14.dp))
        .padding(horizontal = 14.dp, vertical = 14.dp)
    Row(
        modifier = rowModifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(NocturneAccentTint, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = NocturneAccentLight, modifier = Modifier.size(18.dp))
            }
            androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 12.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = title, style = MaterialTheme.typography.bodyMedium, color = NocturneText)
                    if (showUnreadBadge) {
                        androidx.compose.foundation.layout.Spacer(Modifier.padding(start = 6.dp))
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .background(NocturneAccentLight, CircleShape)
                        )
                    }
                }
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
                }
            }
        }
        if (trailing != null) {
            trailing()
        } else {
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = NocturneTextMuted
            )
        }
    }
}

/**
 * LanguageChoice — دو گزینه‌ی تمام‌عرض کنار هم (فارسی/انگلیسی). انتخاب‌شده = دایره‌ی اکسنت،
 * انتخاب‌نشده = فقط حاشیه.
 */
@Composable
fun LanguageChoice(
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        options.forEach { option ->
            val isSelected = option == selected
            Surface(
                onClick = { onSelect(option) },
                modifier = Modifier
                    .weight(1f)
                    .selectable(selected = isSelected, onClick = { onSelect(option) }),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) NocturneAccentContainer else NocturneSurface,
                border = if (isSelected) null else BorderStroke(1.dp, NocturneDivider)
            ) {
                Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = option,
                        color = if (isSelected) NocturneAccentPale else NocturneText,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

/**
 * SwitchRow — سوییچ تنظیمات (اسکن پیوسته، بوق، لرزش و...). طبق اسپک: track روشن = اکسنت،
 * خاموش = خنثی.
 */
@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    hint: String? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NocturneSurface, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodyMedium, color = NocturneText)
            if (hint != null) {
                Text(text = hint, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = NocturneAccent,
                checkedThumbColor = NocturneAccentPale,
                uncheckedTrackColor = NocturneNeutral,
                uncheckedThumbColor = NocturneTextMuted
            )
        )
    }
}
