package work.ranjit.quicksmswear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun WatchNumPadScreen(
    onSaveContact: (name: String, number: String, defaultMsg: String) -> Unit,
    onBack: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Quick Contact") }
    var customMsg by remember { mutableStateOf("I'm sending a quick message from my watch!") }

    val roles = listOf("Mom", "Dad", "Home", "Emergency", "Work", "Doctor", "Friend", "Quick Contact")

    ScalingLazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 16.dp)
    ) {
        item {
            ListHeader {
                Text(
                    text = "⌨️ Dial / Add Number",
                    style = MaterialTheme.typography.title3,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Display Box for Phone Number
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (phoneNumber.isEmpty()) "Tap buttons below" else phoneNumber,
                    style = MaterialTheme.typography.title2,
                    color = if (phoneNumber.isEmpty()) Color.Gray else MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }

        // Selected Label / Role
        item {
            Text(
                text = "LABEL: $selectedRole",
                style = MaterialTheme.typography.caption2,
                color = Color.LightGray,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Role Selector Chips
        item {
            ScalingLazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(roles) { role ->
                    Chip(
                        onClick = { selectedRole = role },
                        colors = ChipDefaults.chipColors(
                            backgroundColor = if (selectedRole == role) MaterialTheme.colors.primary else Color.DarkGray
                        ),
                        label = {
                            Text(
                                role,
                                fontSize = 10.sp,
                                color = if (selectedRole == role) Color.Black else Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }

        // 3x4 Dialpad Buttons
        val rows = listOf(
            listOf("1", "2", "3"),
            listOf("4", "5", "6"),
            listOf("7", "8", "9"),
            listOf("+", "0", "⌫")
        )

        items(rows) { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp)
            ) {
                row.forEach { digit ->
                    Button(
                        onClick = {
                            if (digit == "⌫") {
                                if (phoneNumber.isNotEmpty()) {
                                    phoneNumber = phoneNumber.dropLast(1)
                                }
                            } else {
                                if (phoneNumber.length < 15) {
                                    phoneNumber += digit
                                }
                            }
                        },
                        colors = ButtonDefaults.secondaryButtonColors(),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Text(
                            text = digit,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (digit == "⌫") Color.Red else Color.White
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Save Button
        item {
            Chip(
                onClick = {
                    if (phoneNumber.isNotBlank()) {
                        onSaveContact(selectedRole, phoneNumber, customMsg)
                        onBack()
                    }
                },
                enabled = phoneNumber.isNotBlank(),
                colors = ChipDefaults.primaryChipColors(
                    backgroundColor = if (phoneNumber.isNotBlank()) MaterialTheme.colors.primary else Color.Gray
                ),
                label = {
                    Text(
                        text = "💾 Save Contact",
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
            )
        }

        item {
            Chip(
                onClick = onBack,
                colors = ChipDefaults.chipColors(backgroundColor = Color.DarkGray),
                label = { Text("Cancel", color = Color.White) },
                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
            )
        }
    }
}
