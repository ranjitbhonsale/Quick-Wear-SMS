package work.ranjit.quicksmswear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import work.ranjit.quicksmswear.data.Contact
import work.ranjit.quicksmswear.data.SmsTemplate

@Composable
fun ManageContactsScreen(
    contacts: List<Contact>,
    templates: List<SmsTemplate>,
    onPickFromPhonebook: () -> Unit,
    onAddContact: (String, String, String) -> Unit,
    onUpdateContact: (Contact) -> Unit,
    onDeleteContact: (String) -> Unit,
    onAddTemplate: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedContactForEdit by remember { mutableStateOf<Contact?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        text = "⚙️ Contact Settings",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 1. Pick directly from Phone contacts on Wear OS
            item {
                Chip(
                    onClick = onPickFromPhonebook,
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    label = {
                        Text(
                            text = "📇 Pick from Device Contacts",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "Select from watch phonebook",
                            fontSize = 10.sp,
                            color = Color.Black.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            // 2. Add preset target
            item {
                Chip(
                    onClick = { showAddDialog = true },
                    colors = ChipDefaults.secondaryChipColors(),
                    label = {
                        Text(
                            text = "+ Add Preset Contact",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            item {
                Text(
                    text = "SAVED CONTACTS (TAP TO EDIT)",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(contacts) { contact ->
                Chip(
                    onClick = { selectedContactForEdit = contact },
                    colors = ChipDefaults.secondaryChipColors(),
                    label = {
                        Text(
                            text = contact.name,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    secondaryLabel = {
                        Text(
                            text = "${contact.phoneNumber} • \"${contact.defaultMessage}\"",
                            fontSize = 10.sp,
                            color = Color.LightGray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Chip(
                    onClick = onBack,
                    colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray),
                    label = {
                        Text("← Back to Main", color = Color.White)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }
        }

        if (showAddDialog) {
            QuickAddContactOverlay(
                onSave = { name, phone, message ->
                    onAddContact(name, phone, message)
                    showAddDialog = false
                },
                onDismiss = { showAddDialog = false }
            )
        }

        selectedContactForEdit?.let { contact ->
            EditContactOverlay(
                contact = contact,
                templates = templates,
                onUpdateMessage = { newMsg ->
                    onUpdateContact(contact.copy(defaultMessage = newMsg))
                    selectedContactForEdit = null
                },
                onDelete = {
                    onDeleteContact(contact.id)
                    selectedContactForEdit = null
                },
                onDismiss = { selectedContactForEdit = null }
            )
        }
    }
}

@Composable
fun EditContactOverlay(
    contact: Contact,
    templates: List<SmsTemplate>,
    onUpdateMessage: (String) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                ListHeader {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = contact.name,
                            style = MaterialTheme.typography.title3,
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = contact.phoneNumber,
                            style = MaterialTheme.typography.caption1,
                            color = Color.LightGray
                        )
                    }
                }
            }

            item {
                Text(
                    text = "CHANGE DEFAULT MESSAGE",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                )
            }

            items(templates) { template ->
                Chip(
                    onClick = { onUpdateMessage(template.text) },
                    colors = ChipDefaults.secondaryChipColors(),
                    label = {
                        Text(
                            text = template.text,
                            style = MaterialTheme.typography.body2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Chip(
                    onClick = onDelete,
                    colors = ChipDefaults.chipColors(backgroundColor = Color(0xFFD32F2F)),
                    label = {
                        Text("🗑️ Delete Contact", color = Color.White, fontWeight = FontWeight.Bold)
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            item {
                Chip(
                    onClick = onDismiss,
                    colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray),
                    label = { Text("Cancel", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
fun QuickAddContactOverlay(
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    val presets = listOf(
        Triple("Spouse / Partner", "+15550111", "Heading back home!"),
        Triple("Best Friend", "+15550222", "Let's catch up!"),
        Triple("Doctor / Medical", "+15550333", "Requesting callback."),
        Triple("Work Contact", "+15550444", "In a meeting right now.")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        text = "Add Quick Preset",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            items(presets) { (name, phone, msg) ->
                Chip(
                    onClick = { onSave(name, phone, msg) },
                    colors = ChipDefaults.secondaryChipColors(),
                    label = { Text(name, fontWeight = FontWeight.Bold) },
                    secondaryLabel = { Text(phone, fontSize = 10.sp, color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                )
            }

            item {
                Chip(
                    onClick = onDismiss,
                    colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray),
                    label = { Text("Cancel", color = Color.White) },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                )
            }
        }
    }
}
