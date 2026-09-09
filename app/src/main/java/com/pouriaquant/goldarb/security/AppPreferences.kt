package com.pouriaquant.goldarb.security

import android.content.Context
import com.pouriaquant.goldarb.data.DEFAULT_VENUE_TOMAN_BALANCE
import com.pouriaquant.goldarb.data.MarketCatalog
import com.pouriaquant.goldarb.data.VenuePosition
import org.json.JSONObject
import java.time.Instant

enum class AppThemeMode { SYSTEM, LIGHT, DARK }

enum class AppVisualStyle { OBSIDIAN_CHAMPAGNE, MIDNIGHT_NAVY_WARM_GOLD }

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
        get() {
            val stored = preferences.getString(KEY_VISUAL_STYLE, AppVisualStyle.OBSIDIAN_CHAMPAGNE.name).orEmpty()
            if (stored == "NAVY_BANKING") return AppVisualStyle.MIDNIGHT_NAVY_WARM_GOLD
            return runCatching { AppVisualStyle.valueOf(stored) }
                .getOrDefault(AppVisualStyle.OBSIDIAN_CHAMPAGNE)
        }
        set(value) = preferences.edit().putString(KEY_VISUAL_STYLE, value.name).apply()

    var minimumNetProfitRate: Double
        get() = preferences.getFloat(KEY_MINIMUM_PROFIT_RATE, 0.05f).toDouble()
        set(value) = preferences.edit().putFloat(KEY_MINIMUM_PROFIT_RATE, value.coerceIn(0.0, 1.0).toFloat()).apply()

    fun loadPositions(): Map<String, VenuePosition> {
        val now = Instant.now().toString()
        val stored = runCatching { JSONObject(preferences.getString(KEY_POSITIONS, "{}") ?: "{}") }.getOrDefault(JSONObject())
        return MarketCatalog.entries.associate { entry ->
            val row = stored.optJSONObject(entry.id)
            entry.id to VenuePosition(
                venueId = entry.id,
                tomanBalance = row?.optDouble("toman", DEFAULT_VENUE_TOMAN_BALANCE) ?: DEFAULT_VENUE_TOMAN_BALANCE,
                goldBalanceGram = row?.optDouble("gold", 0.0) ?: 0.0,
                updatedAt = row?.optString("updatedAt")?.takeIf(String::isNotBlank) ?: now,
            )
        }
    }

    fun savePositions(positions: Map<String, VenuePosition>) {
        val root = JSONObject()
        positions.forEach { (id, position) ->
            root.put(id, JSONObject().put("toman", position.tomanBalance).put("gold", position.goldBalanceGram).put("updatedAt", position.updatedAt))
        }
        preferences.edit().putString(KEY_POSITIONS, root.toString()).apply()
    }

    fun resetPositions() = preferences.edit().remove(KEY_POSITIONS).apply()

    private companion object {
        const val FILE_NAME = "zararb_preferences"
        const val KEY_BIOMETRIC_LOCK = "biometric_lock"
        const val KEY_THEME_MODE = "theme_mode"
        const val KEY_VISUAL_STYLE = "visual_style"
        const val KEY_MINIMUM_PROFIT_RATE = "minimum_profit_rate_v1"
        const val KEY_POSITIONS = "venue_positions_v1"
    }
}
