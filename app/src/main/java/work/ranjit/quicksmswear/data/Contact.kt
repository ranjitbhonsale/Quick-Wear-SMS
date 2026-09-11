package work.ranjit.quicksmswear.data

import java.util.UUID

data class Contact(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val phoneNumber: String,
    val defaultMessage: String = "I'm sending a quick message from my watch!"
)

data class SmsTemplate(
    val id: String = UUID.randomUUID().toString(),
    val text: String
)
