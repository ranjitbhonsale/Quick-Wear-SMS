package work.ranjit.quicksmswear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
    onAddContact: (String, String, String) -> Unit,
    onDeleteContact: (String) -> Unit,
    onAddTemplate: (String) -> Unit,
    onBack: () -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp)
        ) {
            item {
                ListHeader {
                    Text(
                        text = "⚙️ Settings",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            item {
                Chip(
                    onClick = { showAddDialog = true },
                    colors = ChipDefaults.primaryChipColors(
                        backgroundColor = MaterialTheme.colors.primary
                    ),
                    label = {
                        Text(
                            text = "+ Add Quick Contact",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                )
            }

            item {
                Text(
                    text = "SAVED CONTACTS",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )
            }

            items(contacts) { contact ->
                Chip(
                    onClick = { onDeleteContact(contact.id) },
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
                            text = "${contact.phoneNumber} (Tap to delete)",
                            fontSize = 10.sp,
                            color = Color.Red,
                            maxLines = 1
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
    }
}

@Composable
fun QuickAddContactOverlay(
    onSave: (String, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    // Quick preset options for Wear OS input convenience
    val presets = listOf(
        Triple("Wife/Husband", "+15550111", "Heading back home!"),
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
                        text = "Add Preset Contact",
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
