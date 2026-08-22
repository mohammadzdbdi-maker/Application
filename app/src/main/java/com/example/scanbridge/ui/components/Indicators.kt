package com.example.scanbridge.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.NocturneAccentContainer
import com.example.scanbridge.ui.theme.NocturneAccentLight
import com.example.scanbridge.ui.theme.NocturneAccentPale
import com.example.scanbridge.ui.theme.NocturneNeutral
import com.example.scanbridge.ui.theme.NocturneOnNeutral

/** وضعیت‌های StatusDot طبق بخش ۲.۲ اسپک: زنده/در حال اتصال/آفلاین. */
enum class ConnectionState { Live, Connecting, Offline }

/**
 * StatusDot — نقطه‌ی وضعیت اتصال. Live=درخشش ثابت، Connecting=پالس ۱٫۴ثانیه،
 * Offline=خاکستری خنثی بدون درخشش (هیچ‌وقت قرمز هشدار نیست، طبق اسپک).
 */
@Composable
fun StatusDot(state: ConnectionState, modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "statusDotPulse")
    val pulse by transition.animateFloat(
        initialValue = 0.55f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (state == ConnectionState.Connecting) 700 else 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusDotAlpha"
    )
    val color = when (state) {
        ConnectionState.Live -> NocturneAccentLight
        ConnectionState.Connecting -> NocturneAccentLight
        ConnectionState.Offline -> Color(0xFF595D6C)
    }
    val alpha = if (state == ConnectionState.Offline) 1f else pulse

    Box(
        modifier = modifier
            .size(8.dp)
            .drawBehind {
                if (state != ConnectionState.Offline) {
                    drawCircle(color = color.copy(alpha = 0.35f * alpha), radius = size.maxDimension)
                }
            }
            .background(color.copy(alpha = alpha), CircleShape)
    )
}

/** رنگ‌بندی Tag: accent (پرشده) / outline (فقط حاشیه) / neutral (خنثی، برای وضعیت‌های ناموفق). */
enum class TagVariant { Accent, Outline, Neutral }

/**
 * Tag — برچسب کوچیک (تعداد در صف، فرمت بارکد، وضعیت پیام). طبق اسپک، حتی حالت "ناموفق" هم
 * قرمز نیست، رنگ خنثی داره.
 */
@Composable
fun Tag(text: String, modifier: Modifier = Modifier, variant: TagVariant = TagVariant.Outline) {
    val (bg, fg, borderColor) = when (variant) {
        TagVariant.Accent -> Triple(NocturneAccentContainer, NocturneAccentPale, null as Color?)
        TagVariant.Outline -> Triple(Color.Transparent, NocturneAccentPale, NocturneAccentLight.copy(alpha = 0.4f) as Color?)
        TagVariant.Neutral -> Triple(NocturneNeutral, NocturneOnNeutral, null as Color?)
    }
    Box(
        modifier = modifier
            .then(
                if (borderColor != null)
                    Modifier.background(bg, RoundedCornerShape(50)).drawBehind {
                        drawRoundRect(
                            color = borderColor,
                            cornerRadius = androidx.compose.ui.geometry.CornerRadius(50f, 50f),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5f)
                        )
                    }
                else Modifier.background(bg, RoundedCornerShape(50))
            )
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, color = fg, style = MaterialTheme.typography.labelMedium)
    }
}

/**
 * SectionEyebrow — متن کوچیک حروف‌بزرگ بالای بخش‌ها (مثل "SYSTEM PAIRING").
 * از سبک تایپوگرافی Caption (labelSmall، 10sp، +letterSpacing) استفاده می‌کنه.
 */
@Composable
fun SectionEyebrow(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = NocturneOnNeutral,
        modifier = modifier
    )
}
