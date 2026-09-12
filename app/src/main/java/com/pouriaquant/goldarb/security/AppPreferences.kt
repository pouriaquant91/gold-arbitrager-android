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

private const val DATASTORE_FILE_NAME = "rasad_appearance"
private const val LEGACY_FILE_NAME = "zararb_preferences"
private const val KEY_THEME_MODE = "theme_mode"
private const val KEY_VISUAL_STYLE = "visual_style"

private val Context.appearanceDataStore by preferencesDataStore(
    name = DATASTORE_FILE_NAME,
    corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    produceMigrations = { context ->
        listOf(
            SharedPreferencesMigration(
                context,
                LEGACY_FILE_NAME,
                setOf(KEY_THEME_MODE, KEY_VISUAL_STYLE),
            ),
        )
    },
)

class AppPreferences(context: Context) {
    private val appContext = context.applicationContext
    private val preferences = appContext.getSharedPreferences(LEGACY_FILE_NAME, Context.MODE_PRIVATE)
    private val themeModeKey = stringPreferencesKey(KEY_THEME_MODE)
    private val visualStyleKey = stringPreferencesKey(KEY_VISUAL_STYLE)

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

    suspend fun setThemeMode(value: AppThemeMode) {
        appContext.appearanceDataStore.edit { values -> values[themeModeKey] = value.name }
    }

    suspend fun setVisualStyle(value: AppVisualStyle) {
        appContext.appearanceDataStore.edit { values -> values[visualStyleKey] = value.name }
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
