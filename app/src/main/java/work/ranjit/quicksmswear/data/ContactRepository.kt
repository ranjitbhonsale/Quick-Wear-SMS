package work.ranjit.quicksmswear.data

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.core.content.ContextCompat
import org.json.JSONArray
import org.json.JSONObject

class ContactRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("quick_sms_wear_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONTACTS = "key_contacts"
        private const val KEY_TEMPLATES = "key_templates"
        private const val KEY_INITIALIZED = "key_initialized"
    }

    fun getContacts(): List<Contact> {
        val isInitialized = prefs.getBoolean(KEY_INITIALIZED, false)
        if (!isInitialized) {
            val deviceContacts = fetchDeviceContacts()
            if (deviceContacts.isNotEmpty()) {
                saveContacts(deviceContacts)
                prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
                return deviceContacts
            }
        }

        val jsonString = prefs.getString(KEY_CONTACTS, null)
        if (jsonString.isNullOrEmpty()) {
            val deviceContacts = fetchDeviceContacts()
            if (deviceContacts.isNotEmpty()) {
                saveContacts(deviceContacts)
                return deviceContacts
            }
            return emptyList()
        }

        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<Contact>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    Contact(
                        id = obj.optString("id"),
                        name = obj.optString("name"),
                        phoneNumber = obj.optString("phoneNumber"),
                        defaultMessage = obj.optString("defaultMessage")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            fetchDeviceContacts()
        }
    }

    fun syncDeviceContacts(): List<Contact> {
        val deviceContacts = fetchDeviceContacts()
        if (deviceContacts.isNotEmpty()) {
            val current = getContacts().toMutableList()
            val existingNumbers = current.map { it.phoneNumber.replace("\\s".toRegex(), "") }.toSet()
            
            deviceContacts.forEach { newContact ->
                val cleanNum = newContact.phoneNumber.replace("\\s".toRegex(), "")
                if (!existingNumbers.contains(cleanNum)) {
                    current.add(newContact)
                }
            }
            saveContacts(current)
            prefs.edit().putBoolean(KEY_INITIALIZED, true).apply()
            return current
        }
        return getContacts()
    }

    fun fetchDeviceContacts(): List<Contact> {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission) return emptyList()

        val list = mutableListOf<Contact>()
        val seenNumbers = mutableSetOf<String>()

        try {
            val projection = arrayOf(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val sortOrder = "${ContactsContract.CommonDataKinds.Phone.STARRED} DESC, ${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC"

            context.contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
            )?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

                while (cursor.moveToNext() && list.size < 15) {
                    val name = if (nameIndex >= 0) cursor.getString(nameIndex) ?: "Contact" else "Contact"
                    val rawNumber = if (numberIndex >= 0) cursor.getString(numberIndex) ?: "" else ""
                    val cleanNumber = rawNumber.replace("\\s".toRegex(), "")

                    if (cleanNumber.isNotBlank() && !seenNumbers.contains(cleanNumber)) {
                        seenNumbers.add(cleanNumber)
                        list.add(
                            Contact(
                                name = name,
                                phoneNumber = rawNumber,
                                defaultMessage = "I'm sending a quick message from my watch!"
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return list
    }

    fun saveContacts(contacts: List<Contact>) {
        val array = JSONArray()
        contacts.forEach { contact ->
            val obj = JSONObject().apply {
                put("id", contact.id)
                put("name", contact.name)
                put("phoneNumber", contact.phoneNumber)
                put("defaultMessage", contact.defaultMessage)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_CONTACTS, array.toString()).apply()
    }

    fun addContact(contact: Contact) {
        val current = getContacts().toMutableList()
        current.add(contact)
        saveContacts(current)
    }

    fun updateContact(contact: Contact) {
        val current = getContacts().map { if (it.id == contact.id) contact else it }
        saveContacts(current)
    }

    fun deleteContact(contactId: String) {
        val current = getContacts().filterNot { it.id == contactId }
        saveContacts(current)
    }

    fun getTemplates(): List<SmsTemplate> {
        val jsonString = prefs.getString(KEY_TEMPLATES, null)
        if (jsonString.isNullOrEmpty()) {
            val defaults = getDefaultTemplates()
            saveTemplates(defaults)
            return defaults
        }

        return try {
            val array = JSONArray(jsonString)
            val list = mutableListOf<SmsTemplate>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    SmsTemplate(
                        id = obj.optString("id"),
                        text = obj.optString("text")
                    )
                )
            }
            list
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultTemplates()
        }
    }

    fun saveTemplates(templates: List<SmsTemplate>) {
        val array = JSONArray()
        templates.forEach { template ->
            val obj = JSONObject().apply {
                put("id", template.id)
                put("text", template.text)
            }
            array.put(obj)
        }
        prefs.edit().putString(KEY_TEMPLATES, array.toString()).apply()
    }

    private fun getDefaultTemplates(): List<SmsTemplate> {
        return listOf(
            SmsTemplate(text = "I'm on my way!"),
            SmsTemplate(text = "Call me when you are free."),
            SmsTemplate(text = "Arrived safely."),
            SmsTemplate(text = "In a meeting, talk soon."),
            SmsTemplate(text = "Need quick assistance!")
        )
    }
}
