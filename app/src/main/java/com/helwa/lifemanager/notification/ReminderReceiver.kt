package com.helwa.lifemanager.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.helwa.lifemanager.data.entity.RepeatType
import com.helwa.lifemanager.data.repo.TaskRepository
import com.helwa.lifemanager.settings.SettingsRepository
import com.helwa.lifemanager.util.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

/** يستقبل تنبيهات AlarmManager وأزرار الإشعار (تم / تأجيل) */
class ReminderReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId < 0) return

        val appContext = context.applicationContext
        val repo = TaskRepository.get(appContext)
        val settingsRepo = SettingsRepository.get(appContext)
        val pending = goAsync()

        scope.launch {
            try {
                val task = repo.getById(taskId) ?: return@launch
                val settings = settingsRepo.settings.first()

                when (intent.action) {
                    ACTION_FIRE -> {
                        NotificationHelper.ensureChannels(
                            appContext, settings.soundEnabled, settings.vibrationEnabled
                        )
                        NotificationHelper.showTaskReminder(
                            appContext, task, settings.soundEnabled, settings.vibrationEnabled
                        )
                        rescheduleIfRepeating(appContext, taskId, task.reminderTime, task.repeatType)
                    }

                    ACTION_DONE -> {
                        repo.setCompleted(taskId, true)
                        NotificationHelper.cancel(appContext, taskId.toInt())
                    }

                    ACTION_SNOOZE -> {
                        val newTime = DateUtils.afterMinutes(15)
                        repo.updateReminderTime(taskId, newTime)
                        ReminderScheduler.scheduleAt(appContext, taskId, newTime)
                        NotificationHelper.cancel(appContext, taskId.toInt())
                    }
                }
            } finally {
                pending.finish()
            }
        }
    }

    private suspend fun rescheduleIfRepeating(
        context: Context,
        taskId: Long,
        currentTime: Long,
        repeat: RepeatType
    ) {
        if (repeat == RepeatType.ONCE) return
        val c = Calendar.getInstance().apply { timeInMillis = currentTime }
        when (repeat) {
            RepeatType.DAILY -> c.add(Calendar.DAY_OF_YEAR, 1)
            RepeatType.WEEKLY -> c.add(Calendar.WEEK_OF_YEAR, 1)
            else -> return
        }
        val repo = TaskRepository.get(context)
        repo.updateReminderTime(taskId, c.timeInMillis)
        ReminderScheduler.scheduleAt(context, taskId, c.timeInMillis)
    }

    companion object {
        const val ACTION_FIRE = "com.helwa.lifemanager.FIRE"
        const val ACTION_DONE = "com.helwa.lifemanager.DONE"
        const val ACTION_SNOOZE = "com.helwa.lifemanager.SNOOZE"
        const val EXTRA_TASK_ID = "task_id"
    }
}
