package com.example.hourchime

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.util.Calendar

class ChimeScheduler {
    companion object {
        private val TARGET_MINUTES = intArrayOf(0, 15, 30, 45)

        fun scheduleAll(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            for (min in TARGET_MINUTES) {
                val intent = Intent(context, ChimeAlarmReceiver::class.java).apply {
                    putExtra("minute", min)
                }
                // compute hour12 for this minute slot
                val cal = nextCalendar(min)
                val hour12 = to12Hour(cal.get(Calendar.HOUR_OF_DAY))
                intent.putExtra("hour12", hour12)
                val requestCode = getRequestCode(cal.get(Calendar.HOUR_OF_DAY), min)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (alarmManager.canScheduleExactAlarms()) {
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.timeInMillis,
                            pendingIntent
                        )
                    } else {
                        alarmManager.setAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            cal.timeInMillis,
                            pendingIntent
                        )
                    }
                } else {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        cal.timeInMillis,
                        pendingIntent
                    )
                }
            }
        }

        private fun nextCalendar(targetMin: Int): Calendar {
            val now = Calendar.getInstance()
            val cal = Calendar.getInstance()
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.set(Calendar.MINUTE, targetMin)
            if (cal.before(now)) {
                cal.add(Calendar.HOUR_OF_DAY, 1)
            }
            return cal
        }

        private fun to12Hour(hour24: Int): Int {
            var h = hour24 % 12
            if (h == 0) h = 12
            return h
        }

        private fun getRequestCode(hour24: Int, minute: Int): Int {
            return (hour24 * 100 + minute)
        }
    }
}
