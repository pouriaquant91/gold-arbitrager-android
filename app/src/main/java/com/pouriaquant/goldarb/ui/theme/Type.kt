package com.pouriaquant.goldarb.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pouriaquant.goldarb.security.AppFontFamily

private fun AppFontFamily.safeFamily(): FontFamily = when (this) {
    AppFontFamily.SYSTEM -> FontFamily.Default
    // Font assets are intentionally not claimed as bundled. These choices persist now and
    // safely fall back to the platform Persian sans-serif until licensed local files are added.
    AppFontFamily.VAZIRMATN,
    AppFontFamily.ESTEDAD,
    AppFontFamily.SAHEL,
    -> FontFamily.SansSerif
}

fun rasadTypography(fontFamily: AppFontFamily): Typography {
    val persianSans = fontFamily.safeFamily()
    return Typography(
        displaySmall = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, lineHeight = 42.sp),
        headlineMedium = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 34.sp),
        titleLarge = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.Bold, fontSize = 19.sp, lineHeight = 28.sp),
        titleMedium = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, lineHeight = 24.sp),
        bodyLarge = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.Normal, fontSize = 16.sp, lineHeight = 26.sp),
        bodyMedium = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.Normal, fontSize = 14.sp, lineHeight = 23.sp),
        labelLarge = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, lineHeight = 21.sp),
        labelMedium = TextStyle(fontFamily = persianSans, fontWeight = FontWeight.Medium, fontSize = 12.sp, lineHeight = 19.sp),
    )
}
