package com.example.hourchime

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Handler
import android.os.Looper
import java.util.concurrent.atomic.AtomicInteger

class ChimePlayer {
    companion object {
        private var soundPool: SoundPool? = null
        private var dingId: Int = 0
        private var dingLoaded = false
        private var bingId: Int = 0
        private var bingLoaded = false

        fun playChime(context: Context, hour12: Int, minute: Int) {
            initSoundPool(context)
            val count = getCount(hour12, minute)
            if (count <= 0) return
            val useBing = minute != 0
            playSequence(context, count, useBing)
        }

        private fun getCount(hour12: Int, minute: Int): Int {
            return when (minute) {
                0 -> hour12
                15 -> 1
                30 -> 2
                45 -> 4  // مطابق درخواست: سر ۴۵ دقیقه چهار تا بینگ
                else -> 0
            }
        }

        private fun initSoundPool(context: Context) {
            if (soundPool != null) return
            val attrs = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            soundPool = SoundPool.Builder()
                .setMaxStreams(4)
                .setAudioAttributes(attrs)
                .build()
            val pool = soundPool ?: return
            // Try to load built-in sounds; if not present, we'll use a simple beep fallback via tone
            try {
                dingId = pool.load(context, R.raw.ding, 1)
                dingLoaded = true
            } catch (e: Exception) {
                dingLoaded = false
            }
            try {
                bingId = pool.load(context, R.raw.bing, 1)
                bingLoaded = true
            } catch (e: Exception) {
                bingLoaded = false
            }
        }

        private fun playSequence(context: Context, count: Int, useBing: Boolean) {
            val handler = Handler(Looper.getMainLooper())
            val played = AtomicInteger(0)
            val pool = soundPool
            fun playNext() {
                val p = played.incrementAndGet()
                if (p > count) return
                if (pool != null) {
                    if (useBing && bingLoaded && bingId != 0) {
                        pool.play(bingId, 1f, 1f, 1, 0, 1f)
                    } else if (!useBing && dingLoaded && dingId != 0) {
                        pool.play(dingId, 1f, 1f, 1, 0, 1f)
                    } else {
                        // fallback: use ToneGenerator
                        try {
                            val tg = android.media.ToneGenerator(
                                android.media.AudioManager.STREAM_NOTIFICATION,
                                80
                            )
                            if (useBing) tg.startTone(android.media.ToneGenerator.TONE_PROP_BEEP2, 150)
                            else tg.startTone(android.media.ToneGenerator.TONE_PROP_BEEP, 200)
                            handler.postDelayed({ tg.release() }, 300)
                        } catch (e: Exception) {
                        }
                    }
                }
                if (p < count) {
                    handler.postDelayed({ playNext() }, 1000)
                }
            }
            playNext()
        }
    }
}
