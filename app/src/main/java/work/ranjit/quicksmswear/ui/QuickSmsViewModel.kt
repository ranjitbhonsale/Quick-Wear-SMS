package work.ranjit.quicksmswear.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import work.ranjit.quicksmswear.data.Contact
import work.ranjit.quicksmswear.data.ContactRepository
import work.ranjit.quicksmswear.data.SmsResult
import work.ranjit.quicksmswear.data.SmsSender
import work.ranjit.quicksmswear.data.SmsTemplate
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ConfirmationUiState {
    object Idle : ConfirmationUiState()
    data class Countdown(
        val contact: Contact,
        val message: String,
        val secondsRemaining: Int
    ) : ConfirmationUiState()
    object Sending : ConfirmationUiState()
    data class Success(val contactName: String) : ConfirmationUiState()
    data class Error(val errorMessage: String) : ConfirmationUiState()
}

data class QuickSmsUiState(
    val contacts: List<Contact> = emptyList(),
    val templates: List<SmsTemplate> = emptyList(),
    val confirmationState: ConfirmationUiState = ConfirmationUiState.Idle,
    val hasPermission: Boolean = false
)

class QuickSmsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ContactRepository(application)
    private val smsSender = SmsSender(application)

    private val _uiState = MutableStateFlow(QuickSmsUiState())
    val uiState: StateFlow<QuickSmsUiState> = _uiState.asStateFlow()

    private var countdownJob: Job? = null

    init {
        loadData()
        checkPermission()
    }

    fun checkPermission() {
        _uiState.update { it.copy(hasPermission = smsSender.hasSmsPermission()) }
    }

    fun loadData() {
        val contacts = repository.getContacts()
        val templates = repository.getTemplates()
        _uiState.update { it.copy(contacts = contacts, templates = templates) }
    }

    fun initiateSend(contact: Contact, customMessage: String? = null) {
        val message = customMessage ?: contact.defaultMessage
        countdownJob?.cancel()

        _uiState.update {
            it.copy(
                confirmationState = ConfirmationUiState.Countdown(
                    contact = contact,
                    message = message,
                    secondsRemaining = 3
                )
            )
        }

        countdownJob = viewModelScope.launch {
            for (sec in 3 downTo 1) {
                _uiState.update { state ->
                    val current = state.confirmationState
                    if (current is ConfirmationUiState.Countdown) {
                        state.copy(confirmationState = current.copy(secondsRemaining = sec))
                    } else state
                }
                delay(1000)
            }

            // Time reached 0, execute send automatically
            executeSend(contact, message)
        }
    }

    fun confirmSendNow() {
        val current = _uiState.value.confirmationState
        if (current is ConfirmationUiState.Countdown) {
            countdownJob?.cancel()
            executeSend(current.contact, current.message)
        }
    }

    fun cancelConfirmation() {
        countdownJob?.cancel()
        _uiState.update { it.copy(confirmationState = ConfirmationUiState.Idle) }
    }

    private fun executeSend(contact: Contact, message: String) {
        _uiState.update { it.copy(confirmationState = ConfirmationUiState.Sending) }

        viewModelScope.launch {
            val result = smsSender.sendSms(contact.phoneNumber, message)
            when (result) {
                is SmsResult.Success -> {
                    _uiState.update {
                        it.copy(confirmationState = ConfirmationUiState.Success(contact.name))
                    }
                    delay(2500)
                    _uiState.update { it.copy(confirmationState = ConfirmationUiState.Idle) }
                }
                is SmsResult.Error -> {
                    _uiState.update {
                        it.copy(confirmationState = ConfirmationUiState.Error(result.message))
                    }
                    delay(3000)
                    _uiState.update { it.copy(confirmationState = ConfirmationUiState.Idle) }
                }
            }
        }
    }

    fun addContact(name: String, phoneNumber: String, defaultMessage: String) {
        val newContact = Contact(
            name = name,
            phoneNumber = phoneNumber,
            defaultMessage = defaultMessage
        )
        repository.addContact(newContact)
        loadData()
    }

    fun updateContact(contact: Contact) {
        repository.updateContact(contact)
        loadData()
    }

    fun deleteContact(contactId: String) {
        repository.deleteContact(contactId)
        loadData()
    }

    fun addTemplate(text: String) {
        val newTemplate = SmsTemplate(text = text)
        val current = repository.getTemplates().toMutableList()
        current.add(newTemplate)
        repository.saveTemplates(current)
        loadData()
    }

    fun dismissConfirmation() {
        countdownJob?.cancel()
        _uiState.update { it.copy(confirmationState = ConfirmationUiState.Idle) }
    }
}
