package com.helwa.lifemanager.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.helwa.lifemanager.R
import com.helwa.lifemanager.data.entity.Task

object NotificationHelper {

    const val CHANNEL_REMINDERS = "reminders"
    const val CHANNEL_SUMMARY = "daily_summary"

    const val SUMMARY_NOTIFICATION_ID = 100000

    fun ensureChannels(context: Context, sound: Boolean = true, vibration: Boolean = true) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val nm = context.getSystemService(NotificationManager::class.java)

        val reminders = NotificationChannel(
            CHANNEL_REMINDERS,
            "تذكيرات المهام",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "تنبيهات المهام في وقتها"
            enableVibration(vibration)
            if (sound) {
                setSound(
                    RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                    null
                )
            } else {
                setSound(null, null)
            }
            enableLights(true)
        }

        val summary = NotificationChannel(
            CHANNEL_SUMMARY,
            "الملخص الصباحي",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "ملخص مهام اليوم كل صباح"
        }

        nm.createNotificationChannel(reminders)
        nm.createNotificationChannel(summary)
    }

    /** يبني ويعرض إشعار تذكير بمهمة مع زراري (تم / تأجيل) */
    fun showTaskReminder(context: Context, task: Task, sound: Boolean, vibration: Boolean) {
        val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val openPending = PendingIntent.getActivity(
            context, task.id.toInt(), openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val donePending = PendingIntent.getBroadcast(
            context, (task.id + 1_000_000).toInt(),
            Intent(context, ReminderReceiver::class.java).apply {
                action = ReminderReceiver.ACTION_DONE
                putExtra(ReminderReceiver.EXTRA_TASK_ID, task.id)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val snoozePending = PendingIntent.getBroadcast(
            context, (task.id + 2_000_000).toInt(),
            Intent(context, ReminderReceiver::class.java).apply {
                action = ReminderReceiver.ACTION_SNOOZE
                putExtra(ReminderReceiver.EXTRA_TASK_ID, task.id)
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(task.title)
            .setContentText(task.description ?: "حان وقت المهمة")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openPending)
            .addAction(0, "تم ✓", donePending)
            .addAction(0, "تأجيل 15 دقيقة", snoozePending)

        if (vibration) builder.setVibrate(longArrayOf(0, 400, 200, 400))
        if (sound) builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

        safeNotify(context, task.id.toInt(), builder)
    }

    fun showDailySummary(context: Context, total: Int, remaining: Int, userName: String) {
        val greeting = if (userName.isNotBlank()) "صباح الخير يا $userName 🌞" else "صباح الخير 🌞"
        val text = if (total == 0) {
            "مفيش مهام النهاردة، يوم هادئ 🌿"
        } else {
            "عندك $total مهمة النهاردة، باقي منها $remaining 💪"
        }

        val openIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val openPending = PendingIntent.getActivity(
            context, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_SUMMARY)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(greeting)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(openPending)

        safeNotify(context, SUMMARY_NOTIFICATION_ID, builder)
    }

    fun cancel(context: Context, id: Int) {
        NotificationManagerCompat.from(context).cancel(id)
    }

    private fun safeNotify(context: Context, id: Int, builder: NotificationCompat.Builder) {
        try {
            NotificationManagerCompat.from(context).notify(id, builder.build())
        } catch (_: SecurityException) {
            // المستخدم رافض صلاحية الإشعارات
        }
    }
}
