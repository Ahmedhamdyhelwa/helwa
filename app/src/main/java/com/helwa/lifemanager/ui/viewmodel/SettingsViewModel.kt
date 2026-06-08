package com.helwa.lifemanager.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.helwa.lifemanager.notification.NotificationHelper
import com.helwa.lifemanager.settings.SettingsRepository
import com.helwa.lifemanager.settings.ThemeMode
import com.helwa.lifemanager.settings.UserSettings
import com.helwa.lifemanager.work.DailySummaryWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = SettingsRepository.get(app)

    val settings: StateFlow<UserSettings> = repo.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    fun setUserName(name: String) = viewModelScope.launch { repo.setUserName(name) }

    fun setMorningTime(hour: Int, minute: Int) = viewModelScope.launch {
        repo.setMorningTime(hour, minute)
        DailySummaryWorker.schedule(getApplication(), hour, minute)
    }

    fun setVibration(enabled: Boolean) = viewModelScope.launch {
        repo.setVibration(enabled)
        refreshChannels(enabled, null)
    }

    fun setSound(enabled: Boolean) = viewModelScope.launch {
        repo.setSound(enabled)
        refreshChannels(null, enabled)
    }

    fun setTheme(mode: ThemeMode) = viewModelScope.launch { repo.setTheme(mode) }

    private suspend fun refreshChannels(vibration: Boolean?, sound: Boolean?) {
        val current = settings.value
        NotificationHelper.ensureChannels(
            getApplication(),
            sound ?: current.soundEnabled,
            vibration ?: current.vibrationEnabled
        )
    }
}
