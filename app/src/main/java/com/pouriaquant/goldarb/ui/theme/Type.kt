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
    bodyLarge = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Normal, fontSize = 15.sp, lineHeight = 25.sp),
    bodyMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Normal, fontSize = 13.sp, lineHeight = 22.sp),
    labelLarge = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = PersianSans, fontWeight = FontWeight.Medium, fontSize = 11.sp, lineHeight = 18.sp),
)

