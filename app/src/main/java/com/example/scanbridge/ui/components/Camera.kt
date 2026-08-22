package com.example.scanbridge.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.NocturneAccent
import com.example.scanbridge.ui.theme.NocturneAccentLight
import com.example.scanbridge.ui.theme.NocturneCameraGround
import com.example.scanbridge.ui.theme.NocturneText
import com.example.scanbridge.ui.theme.NocturneTextMuted

/** حالت‌های CameraViewport طبق بخش ۲.۲ اسپک: در حال اسکن / دسترسی رد شده / متوقف. */
enum class CameraViewportState { Scanning, PermissionDenied, Paused }

/** چیدمان جعبه‌ای (Faithful) یا تمام‌صفحه (Overlay) طبق بخش ۲.۱ اسپک. */
enum class CameraViewportMode { Boxed, Fill }

/**
 * CameraViewport — قاب دوربین. خودِ پیش‌نمایش دوربین (CameraX) رو از بیرون به‌عنوان [content]
 * می‌گیره؛ این کامپوننت فقط قاب/حالت خطا/اورلی‌ها رو مدیریت می‌کنه، منطق دوربین بیرون از اینجاست.
 */
@Composable
fun CameraViewport(
    state: CameraViewportState,
    modifier: Modifier = Modifier,
    mode: CameraViewportMode = CameraViewportMode.Boxed,
    onOpenSettings: (() -> Unit)? = null,
    overlaySlots: @Composable (androidx.compose.foundation.layout.BoxScope.() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val shape = if (mode == CameraViewportMode.Boxed) RoundedCornerShape(18.dp) else RoundedCornerShape(0.dp)
    Box(
        modifier = modifier
            .then(if (mode == CameraViewportMode.Boxed) Modifier.aspectRatio(1f) else Modifier.fillMaxSize())
            .clip(shape)
            .background(NocturneCameraGround)
    ) {
        when (state) {
            CameraViewportState.Scanning, CameraViewportState.Paused -> {
                content()
                overlaySlots?.invoke(this)
            }
            CameraViewportState.PermissionDenied -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
                ) {
                    Text(
                        text = "دسترسی به دوربین رد شده",
                        style = MaterialTheme.typography.titleSmall,
                        color = NocturneText
                    )
                    Text(
                        text = "برای اسکن بارکد باید دسترسی دوربین رو فعال کنی.",
                        style = MaterialTheme.typography.bodySmall,
                        color = NocturneTextMuted,
                        modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                    )
                    if (onOpenSettings != null) {
                        SecondaryButton(label = "باز کردن تنظیمات", onClick = onOpenSettings)
                    }
                }
            }
        }
    }
}

/** شکل ScanReticle: مربع (بارکد/QR عمومی) یا کشیده (بارکد خطی). */
enum class ReticleShape { Square, Wide }

/**
 * ScanReticle — کادر نشونه‌گیری روی دوربین. idle = ثابت، reading = خط اسکن در حال چرخش،
 * hit = فلاش لحظه‌ای حاشیه به رنگ اکسنت (طبق اسپک، حلقه‌ی حرکت ۲٫۴ثانیه‌ست).
 */
@Composable
fun ScanReticle(
    modifier: Modifier = Modifier,
    shape: ReticleShape = ReticleShape.Square,
    reading: Boolean = true,
    hit: Boolean = false
) {
    val transition = rememberInfiniteTransition(label = "scanLine")
    val lineProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanLineProgress"
    )
    val borderColor = if (hit) NocturneAccentLight else NocturneAccent.copy(alpha = 0.85f)
    val aspect = if (shape == ReticleShape.Wide) 1.6f else 1f

    Box(
        modifier = modifier
            .fillMaxWidth(0.72f)
            .aspectRatio(aspect)
    ) {
        // چهار گوشه‌ی reticle
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                val corner = size.minDimension * 0.12f
                val strokeWidth = 4f
                val path = androidx.compose.ui.graphics.Path()
                // بالا-چپ
                path.moveTo(0f, corner); path.lineTo(0f, 0f); path.lineTo(corner, 0f)
                // بالا-راست
                path.moveTo(size.width - corner, 0f); path.lineTo(size.width, 0f); path.lineTo(size.width, corner)
                // پایین-راست
                path.moveTo(size.width, size.height - corner); path.lineTo(size.width, size.height); path.lineTo(size.width - corner, size.height)
                // پایین-چپ
                path.moveTo(corner, size.height); path.lineTo(0f, size.height); path.lineTo(0f, size.height - corner)
                drawPath(path, color = borderColor, style = Stroke(width = strokeWidth))

                if (reading && !hit) {
                    val y = size.height * lineProgress
                    drawLine(
                        color = borderColor.copy(alpha = 0.9f),
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = strokeWidth
                    )
                }
            }
        }
    }
}

/**
 * GlassChip — تراشه‌ی شیشه‌ای روی دوربین (مود Overlay). چون افکت بلور واقعی نیاز به کتابخونه‌ی
 * جدید داره، اینجا با یه سطح نیمه‌شفاف تیره + حاشیه‌ی کم‌رنگ شبیه‌سازی شده.
 */
@Composable
fun GlassChip(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.Black.copy(alpha = 0.38f),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.14f))
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            content()
        }
    }
}
