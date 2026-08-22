package com.example.scanbridge.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.scanbridge.R

val Pinar = FontFamily(
    Font(R.font.pinar_ds1_regular, FontWeight.Normal),
    Font(R.font.pinar_ds1_regular, FontWeight.Bold) // Assuming we only have the one file, using it for both for now
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = Pinar),
    displayMedium = TextStyle(fontFamily = Pinar),
    displaySmall = TextStyle(fontFamily = Pinar),
    headlineLarge = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Pinar,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    labelLarge = TextStyle(fontFamily = Pinar),
    labelMedium = TextStyle(fontFamily = Pinar),
    labelSmall = TextStyle(fontFamily = Pinar)
)