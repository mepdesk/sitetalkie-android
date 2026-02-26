package com.bitchat.android.ui

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitchat.android.core.ui.component.sheet.BitchatBottomSheet

private const val PREFS_NAME = "bitchat_prefs"
private const val KEY_FLOOR = "currentFloorNumber"
private const val FLOOR_MIN = -3
private const val FLOOR_MAX = 50

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteAlertComposerSheet(
    isPresented: Boolean,
    onDismiss: () -> Unit,
    onSendAlert: (String) -> Unit
) {
    if (!isPresented) return

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) }

    var selectedType by remember { mutableStateOf<SiteAlertType?>(null) }
    var currentFloor by remember { mutableIntStateOf(prefs.getInt(KEY_FLOOR, 1)) }
    var detailText by remember { mutableStateOf("") }
    var showConfirmDialog by remember { mutableStateOf(false) }

    BitchatBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Filled.Campaign,
                    contentDescription = "Site Alert",
                    modifier = Modifier.size(28.dp),
                    tint = Color(0xFFE8960C)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Site Alert",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 24.sp
                    ),
                    color = Color(0xFFF0F0F0)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Warning text
            Text(
                text = "This will alert everyone on the mesh",
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 14.sp
                ),
                color = Color(0xFFE5484D)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 grid: FIRE, WARNING / MEDICAL, CRANE
            val topRow = listOf(SiteAlertType.FIRE, SiteAlertType.WARNING)
            val bottomRow = listOf(SiteAlertType.MEDICAL, SiteAlertType.CRANE)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                topRow.forEach { type ->
                    AlertTypeCard(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { selectedType = type },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                bottomRow.forEach { type ->
                    AlertTypeCard(
                        type = type,
                        isSelected = selectedType == type,
                        onClick = { selectedType = type },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ALL_CLEAR full-width
            AlertTypeCard(
                type = SiteAlertType.ALL_CLEAR,
                isSelected = selectedType == SiteAlertType.ALL_CLEAR,
                onClick = { selectedType = SiteAlertType.ALL_CLEAR },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Floor selector
            Text(
                text = "Your floor",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF8A8E96)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        if (currentFloor > FLOOR_MIN) {
                            currentFloor--
                            prefs.edit().putInt(KEY_FLOOR, currentFloor).apply()
                        }
                    },
                    enabled = currentFloor > FLOOR_MIN
                ) {
                    Icon(
                        imageVector = Icons.Filled.Remove,
                        contentDescription = "Decrease floor",
                        tint = Color(0xFFE8960C)
                    )
                }

                val floorDisplay = when {
                    currentFloor < 0 -> "B${-currentFloor}"
                    currentFloor == 0 -> "G"
                    else -> "F$currentFloor"
                }
                Text(
                    text = floorDisplay,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = Color(0xFFF0F0F0),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.widthIn(min = 60.dp)
                )

                IconButton(
                    onClick = {
                        if (currentFloor < FLOOR_MAX) {
                            currentFloor++
                            prefs.edit().putInt(KEY_FLOOR, currentFloor).apply()
                        }
                    },
                    enabled = currentFloor < FLOOR_MAX
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Increase floor",
                        tint = Color(0xFFE8960C)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Detail text field
            OutlinedTextField(
                value = detailText,
                onValueChange = { detailText = it },
                placeholder = {
                    Text(
                        text = "Add details (optional)...",
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF8A8E96)
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 80.dp),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFF0F0F0)
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFE8960C),
                    unfocusedBorderColor = Color(0xFF8A8E96).copy(alpha = 0.5f),
                    cursorColor = Color(0xFFE8960C)
                ),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Send button
            val buttonColor = selectedType?.color ?: Color(0xFF8A8E96)
            Button(
                onClick = { showConfirmDialog = true },
                enabled = selectedType != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonColor,
                    disabledContainerColor = Color(0xFF8A8E96).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "SEND ALERT",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Cancel
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Cancel",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color(0xFF8A8E96)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Confirmation dialog
    if (showConfirmDialog && selectedType != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    text = "Send ${selectedType!!.label}?",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color(0xFFF0F0F0)
                )
            },
            text = {
                Text(
                    text = "This cannot be undone. All nearby devices will be alerted.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = Color(0xFF8A8E96)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        val message = formatSiteAlert(selectedType!!, currentFloor, detailText.trim())
                        onSendAlert(message)
                        onDismiss()
                    }
                ) {
                    Text(
                        text = "Send",
                        color = selectedType!!.color,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(
                        text = "Cancel",
                        color = Color(0xFF8A8E96),
                        fontFamily = FontFamily.Monospace
                    )
                }
            },
            containerColor = Color(0xFF1A1C20),
            tonalElevation = 8.dp
        )
    }
}

@Composable
private fun AlertTypeCard(
    type: SiteAlertType,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) type.color else Color.Transparent
    val bgColor = if (isSelected) type.color.copy(alpha = 0.10f) else Color(0xFF1A1C20)

    Surface(
        modifier = modifier
            .height(72.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = bgColor,
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = type.icon,
                contentDescription = type.label,
                modifier = Modifier.size(24.dp),
                tint = type.color
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = type.label,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp
                ),
                color = Color(0xFFF0F0F0),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
