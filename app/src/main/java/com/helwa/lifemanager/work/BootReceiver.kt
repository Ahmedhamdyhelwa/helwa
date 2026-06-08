package com.helwa.lifemanager.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.helwa.lifemanager.data.repo.TaskRepository
import com.helwa.lifemanager.notification.ReminderScheduler
import com.helwa.lifemanager.settings.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** بعد إعادة تشغيل الجهاز نعيد جدولة كل التذكيرات والملخص الصباحي */
class BootReceiver : BroadcastReceiver() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != "android.intent.action.QUICKBOOT_POWERON"
        ) return

        val appContext = context.applicationContext
        val pending = goAsync()

        scope.launch {
            try {
                val tasks = TaskRepository.get(appContext).allTasks.first()
                tasks.filter { it.hasReminder && !it.isCompleted }
                    .forEach { ReminderScheduler.schedule(appContext, it) }

                val settings = SettingsRepository.get(appContext).settings.first()
                DailySummaryWorker.schedule(appContext, settings.morningHour, settings.morningMinute)
            } finally {
                pending.finish()
            }
        }
    }
}
