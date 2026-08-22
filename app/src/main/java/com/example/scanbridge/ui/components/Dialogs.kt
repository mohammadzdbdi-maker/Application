package com.example.scanbridge.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.scanbridge.ui.theme.ErrorRed
import com.example.scanbridge.ui.theme.NocturneAccent
import com.example.scanbridge.ui.theme.NocturneAccentPale
import com.example.scanbridge.ui.theme.NocturneSurface
import com.example.scanbridge.ui.theme.NocturneText
import com.example.scanbridge.ui.theme.NocturneTextMuted

/**
 * NamingDialog — دیالوگ نام‌گذاری سیستم بعد از pairing موفق. حالت error وقتی مقدار خالیه یا
 * تکراریه (تصمیم منطق با صفحه‌ی صدازننده‌ست، این کامپوننت فقط نمایش می‌ده).
 */
@Composable
fun NamingDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = "اسم این سیستم رو انتخاب کن",
    confirmLabel: String = "تایید",
    cancelLabel: String = "انصراف",
    requireNonBlank: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    AlertDialog(
        onDismissRequest = onCancel,
        modifier = modifier,
        containerColor = NocturneSurface,
        titleContentColor = NocturneText,
        textContentColor = NocturneTextMuted,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    isError = isError,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NocturneAccent,
                        unfocusedBorderColor = NocturneTextMuted.copy(alpha = 0.4f),
                        focusedTextColor = NocturneText,
                        unfocusedTextColor = NocturneText,
                        cursorColor = NocturneAccent
                    )
                )
                if (isError && errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.labelMedium,
                        color = ErrorRed,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm, enabled = (!requireNonBlank || value.isNotBlank()) && !isError) {
                Text(confirmLabel, color = NocturneAccentPale)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelLabel, color = NocturneTextMuted)
            }
        }
    )
}

/**
 * ResultSheet — باتم‌شیت نتیجه‌ی اسکن (payload خام، متادیتا، تأخیر، دکمه‌های بعدی/تاریخچه).
 * خودِ ModalBottomSheet (نمایش/مخفی‌شدن) رو صفحه‌ی صدازننده مدیریت می‌کنه؛ این فقط محتوای داخلشه.
 */
@Composable
fun ResultSheetContent(
    payload: String,
    meta: String,
    latency: String,
    onNext: () -> Unit,
    onHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(color = NocturneSurface, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "ارسال شد", style = MaterialTheme.typography.titleMedium, color = NocturneText)
            Text(
                text = payload,
                style = MaterialTheme.typography.titleSmall,
                color = NocturneAccentPale,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 10.dp)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = meta, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted)
                Text(text = latency, style = MaterialTheme.typography.labelMedium, color = NocturneTextMuted, fontFamily = FontFamily.Monospace)
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SecondaryButton(label = "تاریخچه", onClick = onHistory, modifier = Modifier.weight(1f))
                PrimaryButton(label = "اسکن بعدی", onClick = onNext, modifier = Modifier.weight(1f))
            }
        }
    }
}
