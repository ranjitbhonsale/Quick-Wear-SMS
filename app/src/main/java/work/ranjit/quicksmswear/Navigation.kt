package work.ranjit.quicksmswear

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import work.ranjit.quicksmswear.ui.ConfirmationOverlay
import work.ranjit.quicksmswear.ui.MainSmsScreen
import work.ranjit.quicksmswear.ui.ManageContactsScreen
import work.ranjit.quicksmswear.ui.QuickSmsViewModel
import work.ranjit.quicksmswear.ui.WatchNumPadScreen

enum class Screen {
    Main,
    ManageContacts,
    NumPad
}

@Composable
fun AppNavigation(
    onRequestPermission: () -> Unit,
    viewModel: QuickSmsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            Screen.Main -> {
                MainSmsScreen(
                    uiState = uiState,
                    onRequestPermission = onRequestPermission,
                    onContactClick = { contact ->
                        if (uiState.hasPermission) {
                            viewModel.initiateSend(contact)
                        } else {
                            onRequestPermission()
                        }
                    },
                    onContactLongClick = { contact ->
                        viewModel.initiateSend(contact)
                    },
                    onManageContactsClick = {
                        currentScreen = Screen.ManageContacts
                    }
                )
            }
            Screen.ManageContacts -> {
                ManageContactsScreen(
                    contacts = uiState.contacts,
                    templates = uiState.templates,
                    onOpenNumPad = {
                        currentScreen = Screen.NumPad
                    },
                    onAddContact = { name, phone, msg ->
                        viewModel.addContact(name, phone, msg)
                    },
                    onUpdateContact = { contact ->
                        viewModel.updateContact(contact)
                    },
                    onDeleteContact = { id ->
                        viewModel.deleteContact(id)
                    },
                    onAddTemplate = { text ->
                        viewModel.addTemplate(text)
                    },
                    onBack = {
                        currentScreen = Screen.Main
                    }
                )
            }
            Screen.NumPad -> {
                WatchNumPadScreen(
                    onSaveContact = { name, number, defaultMsg ->
                        viewModel.addContact(name, number, defaultMsg)
                    },
                    onBack = {
                        currentScreen = Screen.ManageContacts
                    }
                )
            }
        }

        // Overlay confirmation countdown / status alert over any screen
        ConfirmationOverlay(
            state = uiState.confirmationState,
            onConfirmNow = { viewModel.confirmSendNow() },
            onCancel = { viewModel.cancelConfirmation() },
            onDismiss = { viewModel.dismissConfirmation() }
        )
    }
}
