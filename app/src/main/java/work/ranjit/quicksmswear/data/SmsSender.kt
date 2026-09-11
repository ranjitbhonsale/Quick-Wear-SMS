package work.ranjit.quicksmswear.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.telephony.SmsManager
import androidx.core.content.ContextCompat

sealed class SmsResult {
    object Success : SmsResult()
    data class Error(val message: String) : SmsResult()
}

class SmsSender(private val context: Context) {

    fun hasSmsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun sendSms(phoneNumber: String, message: String): SmsResult {
        if (!hasSmsPermission()) {
            return SmsResult.Error("SMS permission not granted.")
        }

        if (phoneNumber.isBlank()) {
            return SmsResult.Error("Phone number cannot be empty.")
        }

        try {
            val smsManager: SmsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                context.getSystemService(SmsManager::class.java)
            } else {
                @Suppress("DEPRECATION")
                SmsManager.getDefault()
            }

            val parts = smsManager.divideMessage(message)
            if (parts.size > 1) {
                smsManager.sendMultipartTextMessage(
                    phoneNumber,
                    null,
                    parts,
                    null,
                    null
                )
            } else {
                smsManager.sendTextMessage(
                    phoneNumber,
                    null,
                    message,
                    null,
                    null
                )
            }

            vibrateFeedback(success = true)
            return SmsResult.Success
        } catch (e: Exception) {
            e.printStackTrace()
            vibrateFeedback(success = false)
            return SmsResult.Error(e.localizedMessage ?: "Failed to send SMS.")
        }
    }

    fun vibrateFeedback(success: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                val timings = if (success) longArrayOf(0, 80, 50, 80) else longArrayOf(0, 200, 100, 200)
                vibrator.vibrate(VibrationEffect.createWaveform(timings, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (success) {
                    vibrator.vibrate(100)
                } else {
                    vibrator.vibrate(300)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
