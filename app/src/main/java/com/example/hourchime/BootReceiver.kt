package com.example.hourchime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_QUICKBOOT_POWERON) {
            ChimeScheduler.scheduleAll(context)
        }
    }
}
