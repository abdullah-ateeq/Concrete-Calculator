package com.example.concretecalculator.ui.util

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

class FeedbackController(private val context: Context) {

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    private val audioManager: AudioManager? by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    }

    fun performLightHaptic(view: View? = null, hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        try {
            if (view != null && view.hasWindowFocus()) {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(20)
                }
            }
        } catch (e: Exception) {
            // Ignore if vibration permissions or hardware unavailable
        }
    }

    fun performWarningHaptic(hapticsEnabled: Boolean) {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Short double pulse for warning
                val timings = longArrayOf(0, 30, 40, 30)
                val amplitudes = intArrayOf(0, 180, 0, 180)
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(60)
            }
        } catch (e: Exception) {
            // Ignore if vibration unavailable
        }
    }

    fun playSubtleSound(soundEnabled: Boolean) {
        if (!soundEnabled) return
        try {
            audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 0.4f)
        } catch (e: Exception) {
            // Ignore if audio unavailable
        }
    }
}
