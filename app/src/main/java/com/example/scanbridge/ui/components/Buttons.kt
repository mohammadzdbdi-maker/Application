package com.example.scanbridge.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val background by animateColorAsState(
        targetValue = when {
            !enabled -> NocturneAccent.copy(alpha = 0.45f)
            pressed -> NocturneAccentLight
            else -> NocturneAccent
        },
        label = "primaryButtonBg"
    )

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = CircleShape,
        color = background,
        interactionSource = interactionSource
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = NocturneBackground, modifier = Modifier.size(18.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.size(8.dp))
            }
            Text(
                text = label,
                color = NocturneBackground,
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
    Surface(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        shape = CircleShape,
        color = NocturneAccentTint,
        border = BorderStroke(1.dp, NocturneAccent.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 18.dp),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, contentDescription = null, tint = NocturneAccentLight, modifier = Modifier.size(16.dp))
                androidx.compose.foundation.layout.Spacer(Modifier.size(6.dp))
            }
            Text(text = label, color = NocturneText, style = MaterialTheme.typography.labelLarge)
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

    Surface(
        onClick = onClick,
        modifier = modifier.size(48.dp),
        shape = CircleShape,
        color = bg,
        border = if (glass) BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)) else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, contentDescription = contentDescription, tint = tint, modifier = Modifier.size(20.dp))
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
