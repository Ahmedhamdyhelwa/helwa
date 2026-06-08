package com.helwa.lifemanager.settings

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** أوضاع الثيم المتاحة */
enum class ThemeMode { LIGHT, DARK, SYSTEM }

/** كل إعدادات المستخدم في مكان واحد */
data class UserSettings(
    val userName: String = "",
    val morningHour: Int = 8,
    val morningMinute: Int = 0,
    val vibrationEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

private val Context.dataStore by preferencesDataStore(name = "user_settings")

class SettingsRepository(private val context: Context) {

    private object Keys {
        val NAME = stringPreferencesKey("user_name")
        val MORNING_HOUR = intPreferencesKey("morning_hour")
        val MORNING_MINUTE = intPreferencesKey("morning_minute")
        val VIBRATION = booleanPreferencesKey("vibration")
        val SOUND = booleanPreferencesKey("sound")
        val THEME = stringPreferencesKey("theme_mode")
    }

    val settings: Flow<UserSettings> = context.dataStore.data.map { p ->
        UserSettings(
            userName = p[Keys.NAME] ?: "",
            morningHour = p[Keys.MORNING_HOUR] ?: 8,
            morningMinute = p[Keys.MORNING_MINUTE] ?: 0,
            vibrationEnabled = p[Keys.VIBRATION] ?: true,
            soundEnabled = p[Keys.SOUND] ?: true,
            themeMode = runCatching { ThemeMode.valueOf(p[Keys.THEME] ?: "SYSTEM") }
                .getOrDefault(ThemeMode.SYSTEM)
        )
    }

    suspend fun setUserName(value: String) =
        context.dataStore.edit { it[Keys.NAME] = value }

    suspend fun setMorningTime(hour: Int, minute: Int) =
        context.dataStore.edit {
            it[Keys.MORNING_HOUR] = hour
            it[Keys.MORNING_MINUTE] = minute
        }

    suspend fun setVibration(enabled: Boolean) =
        context.dataStore.edit { it[Keys.VIBRATION] = enabled }

    suspend fun setSound(enabled: Boolean) =
        context.dataStore.edit { it[Keys.SOUND] = enabled }

    suspend fun setTheme(mode: ThemeMode) =
        context.dataStore.edit { it[Keys.THEME] = mode.name }

    companion object {
        @Volatile
        private var INSTANCE: SettingsRepository? = null

        fun get(context: Context): SettingsRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: SettingsRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
