package com.helwa.lifemanager.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.helwa.lifemanager.data.entity.Task

/** مسؤول عن جدولة/إلغاء تنبيهات المهام عبر AlarmManager */
object ReminderScheduler {

    private fun alarmManager(context: Context): AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private fun pendingIntent(context: Context, taskId: Long): PendingIntent {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ReminderReceiver.ACTION_FIRE
            putExtra(ReminderReceiver.EXTRA_TASK_ID, taskId)
        }
        return PendingIntent.getBroadcast(
            context, taskId.toInt(), intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    fun schedule(context: Context, task: Task) {
        if (!task.hasReminder || task.isCompleted) return
        if (task.reminderTime <= System.currentTimeMillis()) return

        val am = alarmManager(context)
        val pi = pendingIntent(context, task.id)

        val canExact = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            am.canScheduleExactAlarms()
        } else true

        if (canExact) {
            am.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, task.reminderTime, pi
            )
        } else {
            // مفيش صلاحية تنبيه دقيق، نستخدم تنبيه عادي
            am.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, task.reminderTime, pi
            )
        }
    }

    fun scheduleAt(context: Context, taskId: Long, time: Long) {
        val am = alarmManager(context)
        val pi = pendingIntent(context, taskId)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pi)
    }

    fun cancel(context: Context, taskId: Long) {
        alarmManager(context).cancel(pendingIntent(context, taskId))
    }
}
