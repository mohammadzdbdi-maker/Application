package com.example.scanbridge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.GradientNavy
import com.example.scanbridge.ui.theme.NocturneAccent
import com.example.scanbridge.ui.theme.NocturneAccentContainer
import com.example.scanbridge.ui.theme.NocturneAccentLight
import com.example.scanbridge.ui.theme.NocturneAccentTint
import com.example.scanbridge.ui.theme.NocturneBackground
import com.example.scanbridge.ui.theme.NocturneNeutral
import com.example.scanbridge.ui.theme.NocturneText

/**
 * PrimaryButton — طبق بخش ۳ سند طراحی (ScanBridge UI Spec).
 * حالت‌ها: default / pressed (accent روشن‌تر) / disabled (۴۵٪ شفافیت). فقط داده + callback می‌گیره،
 * منطقی داخلش نیست.
 */
@Composable
fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // حس لوکس: گرادیان آبی (همان دکمه‌های راهنمای سایت)، سایه‌ی رنگی که موقع فشار جمع
    // می‌شود و کمی جمع‌شدگی لمسی — دکمه واقعاً «برجسته» به نظر می‌رسد.
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "primaryBtnScale")
    val elevation by animateDpAsState(if (pressed) 3.dp else 10.dp, label = "primaryBtnElev")
    val brush = when {
        !enabled -> Brush.linearGradient(
            listOf(NocturneAccent.copy(alpha = 0.38f), NocturneAccent.copy(alpha = 0.38f))
        )
        pressed -> Brush.linearGradient(listOf(NocturneAccent, NocturneAccentLight))
        else -> Brush.linearGradient(listOf(GradientNavy, NocturneAccent))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shape = CircleShape
                clip = true
                shadowElevation = elevation.toPx()
                ambientShadowColor = NocturneAccent
                spotShadowColor = NocturneAccent
            }
            .background(brush, CircleShape)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            }
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * SecondaryButton — دکمه‌ی کم‌رنگ‌تر (پنل، دیالوگ‌ها، هدر). حاشیه‌ی اکسنت روی سطح.
 */
@Composable
fun SecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    // دکمه‌ی ثانویه‌ی رنگی: گرادیان آبی یخی + متن آبی پررنگ + سایه‌ی نرم — جذاب اما
    // هم‌ردیفِ سلسله‌مراتب دکمه‌ی اصلی (اصلی پررنگ، ثانویه ملایم‌تر).
    val scale by animateFloatAsState(if (pressed) 0.97f else 1f, label = "secondaryBtnScale")
    val elevation by animateDpAsState(if (pressed) 2.dp else 6.dp, label = "secondaryBtnElev")

    Box(
        modifier = modifier
            .height(48.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shape = CircleShape
                clip = true
                shadowElevation = elevation.toPx()
                ambientShadowColor = NocturneAccent.copy(alpha = 0.35f)
                spotShadowColor = NocturneAccent.copy(alpha = 0.45f)
            }
            .background(
                Brush.linearGradient(listOf(NocturneAccentContainer, NocturneAccentTint)),
                CircleShape
            )
            .border(BorderStroke(1.dp, NocturneAccent.copy(alpha = 0.45f)), CircleShape)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
            .padding(horizontal = 18.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = NocturneAccent, modifier = Modifier.size(16.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
            }
            Text(
                text = label,
                color = NocturneAccentPale,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

/** رنگ‌بندی IconActionButton: accent (پررنگ) یا neutral (خنثی). */
enum class IconActionTone { Accent, Neutral }

/**
 * IconActionButton — دکمه‌ی دایره‌ای آیکون‌دار (هدر اسکنر، اورلی دوربین، History).
 * glass=true یعنی روی دوربین قرار می‌گیره و باید نیمه‌شفاف باشه.
 */
@Composable
fun IconActionButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tone: IconActionTone = IconActionTone.Neutral,
    glass: Boolean = false,
    contentDescription: String? = null
) {
    val bg = when {
        glass -> Color.Black.copy(alpha = 0.35f)
        tone == IconActionTone.Accent -> NocturneAccentContainer
        else -> NocturneNeutral.copy(alpha = 0.5f)
    }
    val tint = if (tone == IconActionTone.Accent) NocturneAccentLight else NocturneText

    // دکمه‌های آیکونی (تغییر سیستم، خروج، ارسال مجدد و...) حالا رنگی و برجسته‌اند:
    // اکسنت = چیپ گرادیان آبی با آیکون سفید؛ خنثی = زمینه‌ی آبی یخی با آیکون آبی؛
    // حالت شیشه‌ای روی دوربین دست‌نخورده می‌ماند.
    if (glass) {
        Surface(
            onClick = onClick,
            modifier = modifier.size(48.dp),
            shape = CircleShape,
            color = bg,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = contentDescription, tint = Color.White, modifier = Modifier.size(20.dp))
            }
        }
    } else {
        val accent = tone == IconActionTone.Accent
        val interactionSource = remember { MutableInteractionSource() }
        val pressed by interactionSource.collectIsPressedAsState()
        val scale by animateFloatAsState(if (pressed) 0.92f else 1f, label = "iconActionScale")
        val elevation by animateDpAsState(if (pressed) 2.dp else if (accent) 8.dp else 5.dp, label = "iconActionElev")

        Box(
            modifier = modifier
                .size(48.dp)
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    shape = CircleShape
                    clip = true
                    shadowElevation = elevation.toPx()
                    ambientShadowColor = NocturneAccent.copy(alpha = 0.35f)
                    spotShadowColor = NocturneAccent.copy(alpha = 0.45f)
                }
                .background(
                    if (accent) Brush.linearGradient(listOf(GradientNavy, NocturneAccent))
                    else Brush.linearGradient(listOf(NocturneAccentContainer, NocturneAccentTint)),
                    CircleShape
                )
                .border(
                    BorderStroke(1.dp, if (accent) Color.Transparent else NocturneAccent.copy(alpha = 0.45f)),
                    CircleShape
                )
                .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = contentDescription,
                tint = if (accent) Color.White else NocturneAccent,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * TorchToggle — دکمه‌ی فلاش روی دوربین. off = شیشه‌ای خنثی، on = پر شده با تینت اکسنت + حاشیه اکسنت.
 */
@Composable
fun TorchToggle(
    on: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector
) {
    Surface(
        onClick = onToggle,
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        color = if (on) NocturneAccentTint else Color.Black.copy(alpha = 0.35f),
        border = BorderStroke(1.dp, if (on) NocturneAccent else Color.White.copy(alpha = 0.12f))
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                icon,
                contentDescription = "torch",
                tint = if (on) NocturneAccentLight else NocturneText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
