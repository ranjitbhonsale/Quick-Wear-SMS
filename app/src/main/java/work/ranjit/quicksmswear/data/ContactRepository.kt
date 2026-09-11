package work.ranjit.quicksmswear.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

class ContactRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("quick_sms_wear_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CONTACTS = "key_contacts"
        private const val KEY_TEMPLATES = "key_templates"
    }

    fun getContacts(): List<Contact> {
        val jsonString = prefs.getString(KEY_CONTACTS, null)
        if (jsonString.isNullOrEmpty()) {
            val defaults = getDefaultContacts()
            saveContacts(defaults)
            return defaults
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
            getDefaultContacts()
        }
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

    private fun getDefaultContacts(): List<Contact> {
        return listOf(
            Contact(
                name = "Mom",
                phoneNumber = "+15550199",
                defaultMessage = "I'm on my way home!"
            ),
            Contact(
                name = "Dad",
                phoneNumber = "+15550188",
                defaultMessage = "Call me when you're free."
            ),
            Contact(
                name = "Emergency Contact",
                phoneNumber = "+15550177",
                defaultMessage = "Need quick assistance! Please check on me."
            )
        )
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
