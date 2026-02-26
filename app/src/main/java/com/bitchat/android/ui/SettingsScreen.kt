package com.bitchat.android.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SettingsBackground = Color(0xFF0E1012)
private val CardBackground = Color(0xFF1A1C20)
private val CardBorder = Color(0xFF2A2C30)
private val AmberAccent = Color(0xFFE8960C)
private val GreenAccent = Color(0xFF34C759)
private val RedAccent = Color(0xFFE5484D)
private val PrimaryText = Color(0xFFF0F0F0)
private val SecondaryText = Color(0xFF8A8E96)
private val FooterText = Color(0xFF5A5E66)

private const val PREFS_NAME = "sitetalkie_prefs"

private fun getSiteTalkiePrefs(context: Context) =
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

private fun getBitchatPrefs(context: Context) =
    context.getSharedPreferences("bitchat_prefs", Context.MODE_PRIVATE)

@Composable
fun SettingsScreen() {
    val context = LocalContext.current
    var showTradePickerSheet by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) }
    var isEditingName by remember { mutableStateOf(false) }
    var editingNameValue by remember { mutableStateOf("") }

    // Load persisted state
    val prefs = remember { getSiteTalkiePrefs(context) }
    val bitchatPrefs = remember { getBitchatPrefs(context) }

    var displayName by remember { mutableStateOf(bitchatPrefs.getString("nickname", "") ?: "") }
    var currentTrade by remember { mutableStateOf(prefs.getString("com.sitetalkie.user.trade", null)) }
    var dmNotifications by remember { mutableStateOf(prefs.getBoolean("dmNotificationsEnabled", true)) }
    var channelNotifications by remember { mutableStateOf(prefs.getBoolean("channelNotificationsEnabled", true)) }
    var privateChannelNotifications by remember { mutableStateOf(prefs.getBoolean("privateChannelNotificationsEnabled", true)) }
    var currentFloor by remember { mutableIntStateOf(prefs.getInt("currentFloorNumber", 0)) }
    var ghostMode by remember { mutableStateOf(prefs.getBoolean("ghostModeEnabled", false)) }
    var showFloorPicker by remember { mutableStateOf(false) }

    // Get version name
    val versionName = remember {
        try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) { "1.0" }
    }

    // Connected peer count (read-only)
    val peerCount = 0 // Will be wired up to mesh service later

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(SettingsBackground)
            .statusBarsPadding()
    ) {
        item {
            Text(
                text = "Settings",
                color = PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 16.dp)
            )
        }

        // SECTION: PROFILE
        item { SectionHeader("PROFILE") }
        item {
            SettingsCard {
                // Display Name
                if (isEditingName) {
                    SettingsRowEditing(
                        icon = Icons.Default.Person,
                        iconColor = AmberAccent,
                        value = editingNameValue,
                        onValueChange = { if (it.length <= 15) editingNameValue = it },
                        onDone = {
                            if (editingNameValue.isNotBlank()) {
                                displayName = editingNameValue.trim()
                                bitchatPrefs.edit().putString("nickname", displayName).apply()
                            }
                            isEditingName = false
                        }
                    )
                } else {
                    SettingsRow(
                        icon = Icons.Default.Person,
                        iconColor = AmberAccent,
                        title = "Display Name",
                        value = displayName.ifEmpty { "Not set" },
                        onClick = {
                            editingNameValue = displayName
                            isEditingName = true
                        }
                    )
                }
                SettingsDivider()
                // Trade
                SettingsRow(
                    icon = Icons.Default.Build,
                    iconColor = AmberAccent,
                    title = "Trade",
                    value = currentTrade ?: "Not set",
                    onClick = { showTradePickerSheet = true }
                )
            }
        }

        // SECTION: NOTIFICATIONS
        item { SectionHeader("NOTIFICATIONS") }
        item {
            SettingsCard {
                SettingsToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    iconColor = AmberAccent,
                    title = "DM Notifications",
                    subtitle = "Get notified for direct messages",
                    checked = dmNotifications,
                    onCheckedChange = {
                        dmNotifications = it
                        prefs.edit().putBoolean("dmNotificationsEnabled", it).apply()
                    }
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    iconColor = AmberAccent,
                    title = "Channel Notifications",
                    subtitle = "Get notified for public messages",
                    checked = channelNotifications,
                    onCheckedChange = {
                        channelNotifications = it
                        prefs.edit().putBoolean("channelNotificationsEnabled", it).apply()
                    }
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon = Icons.Default.NotificationsNone,
                    iconColor = AmberAccent,
                    title = "Private Channel Notifications",
                    subtitle = "Get notified for private channel messages",
                    checked = privateChannelNotifications,
                    onCheckedChange = {
                        privateChannelNotifications = it
                        prefs.edit().putBoolean("privateChannelNotificationsEnabled", it).apply()
                    }
                )
            }
        }

        // SECTION: MESH
        item { SectionHeader("MESH") }
        item {
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.CellTower,
                    iconColor = AmberAccent,
                    title = "Nearby Users",
                    value = "$peerCount",
                    onClick = null
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Business,
                    iconColor = AmberAccent,
                    title = "Current Floor",
                    value = if (currentFloor == 0) "Ground" else "Floor $currentFloor",
                    onClick = { showFloorPicker = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.AccountTree,
                    iconColor = AmberAccent,
                    title = "Message Range",
                    value = "Up to 7 hops",
                    onClick = null
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Lock,
                    iconColor = GreenAccent,
                    title = "Encryption",
                    value = "AES-256-GCM",
                    onClick = null
                )
                SettingsDivider()
                SettingsToggleRow(
                    icon = Icons.Default.VisibilityOff,
                    iconColor = AmberAccent,
                    title = "Ghost Mode",
                    subtitle = "Hide yourself from other people's radar",
                    checked = ghostMode,
                    onCheckedChange = {
                        ghostMode = it
                        prefs.edit().putBoolean("ghostModeEnabled", it).apply()
                    }
                )
            }
        }

        // SECTION: ABOUT
        item { SectionHeader("ABOUT") }
        item {
            SettingsCard {
                SettingsRow(
                    icon = Icons.Default.Info,
                    iconColor = AmberAccent,
                    title = "About SiteTalkie",
                    onClick = { showAboutDialog = true }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    iconColor = AmberAccent,
                    title = "Help & FAQ",
                    subtitle = "How to use SiteTalkie",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sitetalkie.com/help")))
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Email,
                    iconColor = AmberAccent,
                    title = "Contact Support",
                    subtitle = "Get help with SiteTalkie",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@sitetalkie.com")))
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Build,
                    iconColor = AmberAccent,
                    title = "From the makers of MEP Desk",
                    subtitle = "Engineering tools for building services",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://mepdesk.app")))
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.Star,
                    iconColor = AmberAccent,
                    title = "Rate SiteTalkie",
                    onClick = {
                        try {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
                        } catch (e: Exception) {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}")))
                        }
                    }
                )
                SettingsDivider()
                SettingsRow(
                    icon = Icons.Default.PrivacyTip,
                    iconColor = AmberAccent,
                    title = "Privacy Policy",
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sitetalkie.com/privacy")))
                    }
                )
            }
        }

        // Footer
        item {
            Text(
                text = "SiteTalkie v$versionName",
                color = FooterText,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            )
        }

        // Bottom spacing for navigation bar
        item { Spacer(modifier = Modifier.height(16.dp)) }
    }

    // Trade picker sheet
    if (showTradePickerSheet) {
        TradePickerSheet(
            currentTrade = currentTrade,
            onTradeSelected = { trade ->
                currentTrade = trade
                if (trade != null) {
                    prefs.edit().putString("com.sitetalkie.user.trade", trade).apply()
                } else {
                    prefs.edit().remove("com.sitetalkie.user.trade").apply()
                }
                showTradePickerSheet = false
            },
            onDismiss = { showTradePickerSheet = false }
        )
    }

    // Floor picker dialog
    if (showFloorPicker) {
        FloorPickerDialog(
            currentFloor = currentFloor,
            onFloorSelected = { floor ->
                currentFloor = floor
                prefs.edit().putInt("currentFloorNumber", floor).apply()
                showFloorPicker = false
            },
            onDismiss = { showFloorPicker = false }
        )
    }

    // About dialog
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            containerColor = CardBackground,
            title = {
                Text("SiteTalkie", color = PrimaryText, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "Offline BLE mesh messaging for construction sites.\n\nNo internet required. Messages travel peer-to-peer via Bluetooth Low Energy, up to 7 hops across the mesh network.\n\nEnd-to-end encrypted with AES-256-GCM.",
                    color = SecondaryText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK", color = AmberAccent)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = SecondaryText,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.5.sp,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

@Composable
private fun SettingsCard(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(CardBackground)
    ) {
        content()
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 52.dp),
        thickness = 0.5.dp,
        color = CardBorder
    )
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    value: String? = null,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 16.dp, vertical = if (subtitle != null) 14.dp else 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = PrimaryText,
                fontSize = 16.sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = SecondaryText,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        if (value != null) {
            Text(
                text = value,
                color = SecondaryText,
                fontSize = 15.sp
            )
        }
        if (onClick != null) {
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = SecondaryText,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsRowEditing(
    icon: ImageVector,
    iconColor: Color,
    value: String,
    onValueChange: (String) -> Unit,
    onDone: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                color = PrimaryText,
                fontSize = 16.sp
            ),
            cursorBrush = SolidColor(AmberAccent),
            singleLine = true,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onDone) {
            Text("Done", color = AmberAccent, fontSize = 14.sp)
        }
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    iconColor: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp, top = 14.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = PrimaryText,
                fontSize = 16.sp
            )
            Text(
                text = subtitle,
                color = SecondaryText,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AmberAccent,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFF3A3C40)
            )
        )
    }
}

@Composable
private fun FloorPickerDialog(
    currentFloor: Int,
    onFloorSelected: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var floor by remember { mutableIntStateOf(currentFloor) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackground,
        title = {
            Text("Select Floor", color = PrimaryText, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (floor == 0) "Ground" else "Floor $floor",
                    color = PrimaryText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledIconButton(
                        onClick = { if (floor > -3) floor-- },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AmberAccent,
                            contentColor = Color.Black
                        ),
                        enabled = floor > -3
                    ) {
                        Text("−", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    FilledIconButton(
                        onClick = { if (floor < 50) floor++ },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = AmberAccent,
                            contentColor = Color.Black
                        ),
                        enabled = floor < 50
                    ) {
                        Text("+", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onFloorSelected(floor) }) {
                Text("Done", color = AmberAccent)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SecondaryText)
            }
        }
    )
}
