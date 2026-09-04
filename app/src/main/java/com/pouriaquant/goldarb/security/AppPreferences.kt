package com.pouriaquant.goldarb.security

import android.content.Context

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

enum class AppVisualStyle { EMERALD_LUXURY, NAVY_BANKING }

class AppPreferences(context: Context) {
    private val preferences = context.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

    var biometricLockEnabled: Boolean
        get() = preferences.getBoolean(KEY_BIOMETRIC_LOCK, false)
        set(value) = preferences.edit().putBoolean(KEY_BIOMETRIC_LOCK, value).apply()

    var themeMode: AppThemeMode
        get() = runCatching {
            AppThemeMode.valueOf(preferences.getString(KEY_THEME_MODE, AppThemeMode.DARK.name).orEmpty())
        }.getOrDefault(AppThemeMode.DARK)
        set(value) = preferences.edit().putString(KEY_THEME_MODE, value.name).apply()

    var visualStyle: AppVisualStyle
        get() = runCatching {
            AppVisualStyle.valueOf(
                preferences.getString(KEY_VISUAL_STYLE, AppVisualStyle.EMERALD_LUXURY.name).orEmpty(),
            )
        }.getOrDefault(AppVisualStyle.EMERALD_LUXURY)
        set(value) = preferences.edit().putString(KEY_VISUAL_STYLE, value.name).apply()

    private companion object {
        const val FILE_NAME = "zararb_preferences"
        const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_VISUAL_STYLE = "visual_style"
    }
}
