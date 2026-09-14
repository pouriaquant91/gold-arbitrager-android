package com.pouriaquant.goldarb.security

import android.content.Context
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

enum class AppVisualStyle { GRAPHITE, AURORA, PAPER }

enum class AppBrightness { SYSTEM, LOW, MEDIUM, HIGH }

enum class AppFontScale(val multiplier: Float) {
    SMALL(0.90f),
    NORMAL(1.00f),
    LARGE(1.15f),
}

private const val DATASTORE_FILE_NAME = "rasad_appearance"
private const val LEGACY_FILE_NAME = "zararb_preferences"
private const val KEY_THEME_MODE = "theme_mode"
private const val KEY_VISUAL_STYLE = "visual_style"
private const val KEY_BRIGHTNESS = "brightness"
private const val KEY_FONT_SCALE = "font_scale"

private val Context.appearanceDataStore by preferencesDataStore(
    name = DATASTORE_FILE_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context,
                LEGACY_FILE_NAME,
                setOf(KEY_THEME_MODE, KEY_VISUAL_STYLE, KEY_BRIGHTNESS, KEY_FONT_SCALE),
            ),
        )
    },
)

class AppPreferences(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(LEGACY_FILE_NAME, Context.MODE_PRIVATE)
    private val themeModeKey = stringPreferencesKey(KEY_THEME_MODE)
    private val visualStyleKey = stringPreferencesKey(KEY_VISUAL_STYLE)
    private val brightnessKey = stringPreferencesKey(KEY_BRIGHTNESS)
    private val fontScaleKey = stringPreferencesKey(KEY_FONT_SCALE)

    val themeModeFlow = appContext.appearanceDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            runCatching { AppThemeMode.valueOf(values[themeModeKey].orEmpty()) }
                .getOrDefault(AppThemeMode.SYSTEM)
        }

    val visualStyleFlow = appContext.appearanceDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            when (val stored = values[visualStyleKey].orEmpty()) {
                "OBSIDIAN_CHAMPAGNE" -> AppVisualStyle.GRAPHITE
                "MIDNIGHT_NAVY_WARM_GOLD", "NAVY_BANKING" -> AppVisualStyle.AURORA
                else -> runCatching { AppVisualStyle.valueOf(stored) }
                    .getOrDefault(AppVisualStyle.GRAPHITE)
            }
        }

    val brightnessFlow = appContext.appearanceDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            runCatching { AppBrightness.valueOf(values[brightnessKey].orEmpty()) }
                .getOrDefault(AppBrightness.SYSTEM)
        }

    val fontScaleFlow = appContext.appearanceDataStore.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            runCatching { AppFontScale.valueOf(values[fontScaleKey].orEmpty()) }
                .getOrDefault(AppFontScale.NORMAL)
        }

    suspend fun setThemeMode(value: AppThemeMode) {
        appContext.appearanceDataStore.edit { values -> values[themeModeKey] = value.name }
    }

    suspend fun setVisualStyle(value: AppVisualStyle) {
        appContext.appearanceDataStore.edit { values -> values[visualStyleKey] = value.name }
    }

    suspend fun setBrightness(value: AppBrightness) {
        appContext.appearanceDataStore.edit { values -> values[brightnessKey] = value.name }
    }

    suspend fun setFontScale(value: AppFontScale) {
        appContext.appearanceDataStore.edit { values -> values[fontScaleKey] = value.name }
    }

    var biometricLockEnabled: Boolean
        get() = preferences.getBoolean(KEY_BIOMETRIC_LOCK, false)
        set(value) = preferences.edit().putBoolean(KEY_BIOMETRIC_LOCK, value).apply()

    var sessionToken: String?
        get() = preferences.getString(KEY_SESSION_TOKEN, null)
        set(value) {
            if (value == null) preferences.edit().remove(KEY_SESSION_TOKEN).apply()
            else preferences.edit().putString(KEY_SESSION_TOKEN, value).apply()
        }

    private companion object {
        const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        const val KEY_SESSION_TOKEN = "session_token"
    }
}
