package com.ash.studios.musify.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build

class App : Application() {
    var currentContext: Context? = null
    override fun onCreate() {
        super.onCreate()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Musify", NotificationManager.IMPORTANCE_NONE)
            channel.setSound(null, null)
            channel.enableLights(false)
            channel.lightColor = Color.BLUE
            channel.enableVibration(false)
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "Musify"
    }
}