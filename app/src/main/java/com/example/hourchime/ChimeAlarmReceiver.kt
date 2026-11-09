package com.example.hourchime

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class ChimeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val hour12 = intent.getIntExtra("hour12", 12)
        val minute = intent.getIntExtra("minute", 0)
        ChimePlayer.playChime(context, hour12, minute)
        // Reschedule next occurrences
        ChimeScheduler.scheduleAll(context)
    }
}
