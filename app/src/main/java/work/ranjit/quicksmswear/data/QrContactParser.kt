package work.ranjit.quicksmswear.data

import android.net.Uri
import org.json.JSONObject

object QrContactParser {

    fun parsePayload(rawPayload: String): Contact? {
        if (rawPayload.isBlank()) return null

        val trimmed = rawPayload.trim()

        // 1. JSON Payload
        if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
            return try {
                val json = JSONObject(trimmed)
                val name = json.optString("name", "Quick Contact")
                val phone = json.optString("phone", json.optString("phoneNumber", ""))
                val msg = json.optString("msg", json.optString("defaultMessage", "I'm sending a quick message from my watch!"))
                if (phone.isNotBlank()) {
                    Contact(name = name, phoneNumber = phone, defaultMessage = msg)
                } else null
            } catch (e: Exception) {
                null
            }
        }

        // 2. Deep link URI: quicksms://add?name=Mom&phone=+123456789&msg=Heading+home
        if (trimmed.startsWith("quicksms://", ignoreCase = true) || trimmed.contains("phone=")) {
            return try {
                val uri = Uri.parse(trimmed)
                val name = uri.getQueryParameter("name") ?: "Quick Contact"
                val phone = uri.getQueryParameter("phone") ?: uri.getQueryParameter("number") ?: ""
                val msg = uri.getQueryParameter("msg") ?: uri.getQueryParameter("message") ?: "I'm sending a quick message from my watch!"
                if (phone.isNotBlank()) {
                    Contact(name = name, phoneNumber = phone, defaultMessage = msg)
                } else null
            } catch (e: Exception) {
                null
            }
        }

        // 3. SMSTO format: smsto:+15550199:Heading home!
        if (trimmed.startsWith("smsto:", ignoreCase = true) || trimmed.startsWith("sms:", ignoreCase = true)) {
            val parts = trimmed.substringAfter(":").split(":")
            val phone = parts.firstOrNull() ?: ""
            val msg = if (parts.size > 1) parts[1] else "I'm sending a quick message from my watch!"
            if (phone.isNotBlank()) {
                return Contact(name = "Quick Contact", phoneNumber = phone, defaultMessage = msg)
            }
        }

        // 4. Tel URI: tel:+15550199
        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val phone = trimmed.substringAfter(":")
            if (phone.isNotBlank()) {
                return Contact(name = "Quick Contact", phoneNumber = phone, defaultMessage = "I'm sending a quick message from my watch!")
            }
        }

        // 5. Raw Phone Number (e.g. +1234567890)
        val cleanDigits = trimmed.replace(Regex("[^0-9+]"), "")
        if (cleanDigits.length >= 7) {
            return Contact(name = "Quick Contact", phoneNumber = cleanDigits, defaultMessage = "I'm sending a quick message from my watch!")
        }

        return null
    }
}
