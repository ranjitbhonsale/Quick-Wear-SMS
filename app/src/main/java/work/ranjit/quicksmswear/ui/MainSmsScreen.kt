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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.wear.compose.foundation.lazy.ScalingLazyColumnDefaults
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import work.ranjit.quicksmswear.data.Contact
import work.ranjit.quicksmswear.data.SmsTemplate

@Composable
fun MainSmsScreen(
    uiState: QuickSmsUiState,
    onRequestPermission: () -> Unit,
    onContactClick: (Contact) -> Unit,
    onContactLongClick: (Contact) -> Unit,
    onManageContactsClick: () -> Unit
) {
    var selectedContactForTemplate by remember { mutableStateOf<Contact?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        ScalingLazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 20.dp),
            scalingParams = ScalingLazyColumnDefaults.scalingParams()
        ) {
            item {
                ListHeader {
                    Text(
                        text = "📱 Quick SMS",
                        style = MaterialTheme.typography.title3,
                        color = MaterialTheme.colors.primary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (!uiState.hasPermission) {
                item {
                    Chip(
                        onClick = onRequestPermission,
                        colors = ChipDefaults.gradientBackgroundChipColors(
                            startBackgroundColor = Color(0xFFD32F2F),
                            endBackgroundColor = Color(0xFFC62828)
                        ),
                        label = {
                            Text(
                                text = "Grant SMS Permission",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        },
                        secondaryLabel = {
                            Text(
                                text = "Required to send texts",
                                color = Color.LightGray,
                                fontSize = 10.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                }
            }

            item {
                Text(
                    text = "SELECT CONTACT TO SEND",
                    style = MaterialTheme.typography.caption2,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )
            }

            if (uiState.contacts.isEmpty()) {
                item {
                    Text(
                        text = "No contacts saved. Add a contact below to start!",
                        style = MaterialTheme.typography.body2,
                        textAlign = TextAlign.Center,
                        color = Color.LightGray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            } else {
                items(uiState.contacts) { contact ->
                    ContactChip(
                        contact = contact,
                        onClick = { onContactClick(contact) },
                        onChooseTemplate = { selectedContactForTemplate = contact }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Chip(
                    onClick = onManageContactsClick,
                    colors = ChipDefaults.secondaryChipColors(),
                    label = {
                        Text(
                            text = "⚙️ Manage Contacts",
                            style = MaterialTheme.typography.button
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
            }
        }

        // Show Template selector bottom sheet / overlay if requested
        selectedContactForTemplate?.let { contact ->
            TemplateSelectionOverlay(
                contact = contact,
                templates = uiState.templates,
                onSelectTemplate = { templateText ->
                    selectedContactForTemplate = null
                    onContactClick(contact.copy(defaultMessage = templateText))
                },
                onDismiss = { selectedContactForTemplate = null }
            )
        }
    }
}

@Composable
fun ContactChip(
    contact: Contact,
    onClick: () -> Unit,
    onChooseTemplate: () -> Unit
) {
    val initial = contact.name.firstOrNull()?.uppercase() ?: "C"

    Chip(
        onClick = onClick,
        colors = ChipDefaults.primaryChipColors(
            backgroundColor = MaterialTheme.colors.surface
        ),
        icon = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colors.primary, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        },
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
                text = contact.defaultMessage,
                fontSize = 10.sp,
                color = Color.LightGray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Composable
fun TemplateSelectionOverlay(
    contact: Contact,
    templates: List<SmsTemplate>,
    onSelectTemplate: (String) -> Unit,
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
                            text = "Select Message",
                            style = MaterialTheme.typography.title3,
                            color = MaterialTheme.colors.primary
                        )
                        Text(
                            text = "For ${contact.name}",
                            style = MaterialTheme.typography.caption1,
                            color = Color.LightGray
                        )
                    }
                }
            }

            items(templates) { template ->
                Chip(
                    onClick = { onSelectTemplate(template.text) },
                    colors = ChipDefaults.secondaryChipColors(),
                    label = {
                        Text(
                            text = template.text,
                            style = MaterialTheme.typography.body2,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                )
            }

            item {
                Chip(
                    onClick = onDismiss,
                    colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray),
                    label = { Text("Cancel", color = Color.White) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                )
            }
        }
    }
}
