package work.ranjit.quicksmswear.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun ConfirmationOverlay(
    state: ConfirmationUiState,
    onConfirmNow: () -> Unit,
    onCancel: () -> Unit,
    onDismiss: () -> Unit
) {
    if (state is ConfirmationUiState.Idle) return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.92f))
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (state) {
            is ConfirmationUiState.Countdown -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(72.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = state.secondsRemaining / 3f,
                            modifier = Modifier.fillMaxSize(),
                            indicatorColor = MaterialTheme.colors.primary,
                            trackColor = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "${state.secondsRemaining}s",
                            style = MaterialTheme.typography.title2.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colors.primary
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Send SMS to",
                        style = MaterialTheme.typography.caption2,
                        color = Color.LightGray
                    )
                    Text(
                        text = state.contact.name,
                        style = MaterialTheme.typography.title3,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = "\"${state.message}\"",
                        style = MaterialTheme.typography.body2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Cancel button
                        Button(
                            onClick = onCancel,
                            colors = ButtonDefaults.secondaryButtonColors(),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("✕", color = Color.Red, fontWeight = FontWeight.Bold)
                        }

                        // Send Now button
                        Button(
                            onClick = onConfirmNow,
                            colors = ButtonDefaults.primaryButtonColors(),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            is ConfirmationUiState.Sending -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        indicatorColor = MaterialTheme.colors.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Sending SMS...",
                        style = MaterialTheme.typography.body1,
                        textAlign = TextAlign.Center
                    )
                }
            }

            is ConfirmationUiState.Success -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color(0xFF4CAF50), shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✓", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "SMS Sent!",
                        style = MaterialTheme.typography.title3,
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "To ${state.contactName}",
                        style = MaterialTheme.typography.caption1,
                        color = Color.LightGray
                    )
                }
            }

            is ConfirmationUiState.Error -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.Red, shape = CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("!", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Failed to Send",
                        style = MaterialTheme.typography.body1,
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = state.errorMessage,
                        style = MaterialTheme.typography.caption2,
                        textAlign = TextAlign.Center,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.secondaryButtonColors(),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("OK", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
