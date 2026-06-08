package com.helwa.lifemanager.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.helwa.lifemanager.data.repo.TaskRepository
import com.helwa.lifemanager.notification.NotificationHelper
import com.helwa.lifemanager.settings.SettingsRepository
import com.helwa.lifemanager.util.DateUtils
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

/** Worker بيبعت إشعار الملخص الصباحي بمهام اليوم */
class DailySummaryWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val ctx = applicationContext
        val settings = SettingsRepository.get(ctx).settings.first()
        val tasks = TaskRepository.get(ctx).allTasks.first()

        val todayTasks = tasks.filter { it.repeatType.name == "DAILY" || DateUtils.isToday(it.reminderTime) }
        val total = todayTasks.size
        val remaining = todayTasks.count { !it.isCompleted }

        NotificationHelper.ensureChannels(ctx, settings.soundEnabled, settings.vibrationEnabled)
        NotificationHelper.showDailySummary(ctx, total, remaining, settings.userName)

        // إعادة الجدولة لليوم اللي بعده بنفس الوقت
        schedule(ctx, settings.morningHour, settings.morningMinute)
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "daily_summary"

        /** يجدول الملخص الصباحي عند الساعة المحددة (مرة واحدة تعيد جدولة نفسها يوميًا) */
        fun schedule(context: Context, hour: Int, minute: Int) {
            val next = DateUtils.nextOccurrenceAt(hour, minute)
            val delay = next - System.currentTimeMillis()

            // PeriodicWork أدق للتكرار اليومي مع مراعاة وقت البداية
            val request = PeriodicWorkRequestBuilder<DailySummaryWorker>(1, TimeUnit.DAYS)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request
            )
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}
