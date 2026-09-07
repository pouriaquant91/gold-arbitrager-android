package com.pouriaquant.goldarb.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val PersianSans = FontFamily.SansSerif

val GoldArbTypography = Typography(
    displaySmall = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 42.sp),
    headlineMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 34.sp),
    titleLarge = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 28.sp),
    titleMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
    bodyLarge = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp),
    bodyMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 23.sp),
    labelLarge = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 21.sp),
    labelMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 19.sp),
)
