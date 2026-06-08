package com.helwa.lifemanager.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.helwa.lifemanager.settings.ThemeMode
import com.helwa.lifemanager.ui.components.TimePickerDialog
import com.helwa.lifemanager.ui.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf(settings.userName) }
    var nameLoaded by remember { mutableStateOf(false) }
    var showTime by remember { mutableStateOf(false) }

    LaunchedEffect(settings.userName) {
        if (!nameLoaded) {
            name = settings.userName
            nameLoaded = true
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            "الإعدادات",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        SettingsSection("الملف الشخصي") {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    viewModel.setUserName(it)
                },
                label = { Text("اسمك (للتحية الشخصية)") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }

        SettingsSection("التذكير الصباحي") {
            OutlinedButton(
                onClick = { showTime = true },
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Filled.Schedule, null)
                Spacer(Modifier.height(0.dp))
                Text(
                    "  وقت الملخص الصباحي:  %02d:%02d".format(settings.morningHour, settings.morningMinute),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        SettingsSection("التنبيهات") {
            ToggleRow(
                title = "الاهتزاز",
                checked = settings.vibrationEnabled,
                onCheckedChange = { viewModel.setVibration(it) }
            )
            Spacer(Modifier.height(8.dp))
            ToggleRow(
                title = "الصوت",
                checked = settings.soundEnabled,
                onCheckedChange = { viewModel.setSound(it) }
            )
        }

        SettingsSection("المظهر") {
            val options = listOf(ThemeMode.LIGHT to "فاتح", ThemeMode.DARK to "داكن", ThemeMode.SYSTEM to "تلقائي")
            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                options.forEachIndexed { index, (mode, label) ->
                    SegmentedButton(
                        selected = settings.themeMode == mode,
                        onClick = { viewModel.setTheme(mode) },
                        shape = SegmentedButtonDefaults.itemShape(index, options.size)
                    ) { Text(label) }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            "تطبيق إدارة الحياة • نسخة 1.0",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(Modifier.height(80.dp))
    }

    if (showTime) {
        val timeState = androidx.compose.material3.rememberTimePickerState(
            initialHour = settings.morningHour,
            initialMinute = settings.morningMinute,
            is24Hour = false
        )
        TimePickerDialog(
            state = timeState,
            title = "وقت التذكير الصباحي",
            onConfirm = {
                viewModel.setMorningTime(timeState.hour, timeState.minute)
                showTime = false
            },
            onDismiss = { showTime = false }
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 14.dp, bottom = 8.dp)
    )
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) { content() }
    }
}

@Composable
private fun ToggleRow(title: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
