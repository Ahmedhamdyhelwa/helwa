package com.helwa.lifemanager

import android.app.Application
import com.helwa.lifemanager.notification.NotificationHelper

class LifeManagerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // إنشاء قنوات الإشعارات مبكرًا
        NotificationHelper.ensureChannels(this)
    }
}
