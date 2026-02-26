package com.bitchat.android.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.location.LocationServices
import java.text.SimpleDateFormat
import java.util.*

private val AmberAccent = Color(0xFFE8960C)
private val ScreenBg = Color(0xFF0E1012)
private val CardBg = Color(0xFF1A1C20)
private val PrimaryText = Color(0xFFF0F0F0)
private val SecondaryText = Color(0xFF8A8E96)
private val DarkText = Color(0xFF5A5E66)

private enum class SiteSection(val label: String) {
    PINS("Pins"),
    ALERTS("Alerts"),
    BULLETIN("Bulletin")
}

@Composable
fun SiteScreen(viewModel: ChatViewModel) {
    val context = LocalContext.current
    var selectedSection by remember { mutableStateOf(SiteSection.PINS) }

    val pinStore = remember { viewModel.sitePinStore }
    val pins by pinStore.pins.collectAsStateWithLifecycle()
    val geofenceManager = remember { viewModel.pinGeofenceManager }

    var showCreatePinSheet by remember { mutableStateOf(false) }
    var showSiteAlertComposer by remember { mutableStateOf(false) }
    var selectedPinForDetail by remember { mutableStateOf<SitePin?>(null) }

    // Current user name for ownership check
    val myName = remember {
        context.getSharedPreferences("bitchat_prefs", Context.MODE_PRIVATE)
            .getString("nickname", "") ?: ""
    }

    // Current location for distance
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            val fusedClient = LocationServices.getFusedLocationProviderClient(context)
            fusedClient.lastLocation.addOnSuccessListener { location ->
                currentLocation = location
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBg)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(top = 12.dp, bottom = 12.dp, start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Site",
                modifier = Modifier.size(28.dp),
                tint = AmberAccent
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Site",
                color = PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Segmented control
        SiteSegmentedControl(
            selected = selectedSection,
            onSelect = { selectedSection = it }
        )

        // Content
        when (selectedSection) {
            SiteSection.PINS -> PinsSection(
                pins = pins,
                currentLocation = currentLocation,
                myName = myName,
                onCreatePin = { showCreatePinSheet = true },
                onPinClick = { selectedPinForDetail = it },
                onDeletePin = { pin ->
                    pinStore.removePin(pin.id)
                    geofenceManager.removeGeofence(pin.id)
                }
            )
            SiteSection.ALERTS -> AlertsSection(
                viewModel = viewModel,
                onSendAlert = { showSiteAlertComposer = true }
            )
            SiteSection.BULLETIN -> BulletinSection()
        }
    }

    // Bottom sheets
    if (showCreatePinSheet) {
        CreatePinSheet(
            onDismiss = { showCreatePinSheet = false },
            onCreatePin = { pin ->
                pinStore.addPin(pin)
                geofenceManager.registerGeofence(pin)
                val message = formatSitePin(pin)
                viewModel.sendMessage(message)
                showCreatePinSheet = false
            }
        )
    }

    if (showSiteAlertComposer) {
        SiteAlertComposerSheet(
            isPresented = true,
            onDismiss = { showSiteAlertComposer = false },
            onSendAlert = { formattedMessage ->
                viewModel.sendMessage(formattedMessage)
                showSiteAlertComposer = false
            }
        )
    }

    selectedPinForDetail?.let { pin ->
        PinDetailSheet(
            pin = pin,
            myName = myName,
            onDismiss = { selectedPinForDetail = null },
            onResolve = {
                pinStore.resolvePin(pin.id)
                geofenceManager.removeGeofence(pin.id)
                selectedPinForDetail = null
            },
            onDelete = {
                pinStore.removePin(pin.id)
                geofenceManager.removeGeofence(pin.id)
                selectedPinForDetail = null
            },
            onExtend = { hours ->
                pinStore.extendPin(pin.id, hours)
                selectedPinForDetail = null
            }
        )
    }
}

@Composable
private fun SiteSegmentedControl(
    selected: SiteSection,
    onSelect: (SiteSection) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        color = CardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(3.dp)
        ) {
            SiteSection.entries.forEach { section ->
                val isSelected = section == selected
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelect(section) },
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) AmberAccent else Color.Transparent
                ) {
                    Text(
                        text = section.label,
                        color = if (isSelected) Color.White else SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }
    }
}

// --- PINS SECTION ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PinsSection(
    pins: List<SitePin>,
    currentLocation: Location?,
    myName: String,
    onCreatePin: () -> Unit,
    onPinClick: (SitePin) -> Unit,
    onDeletePin: (SitePin) -> Unit
) {
    val activePins = pins.filter { !it.isResolved }
    val groupedPins = activePins.sortedBy { it.type.sortOrder }.groupBy { it.type }

    if (activePins.isEmpty()) {
        // Create button + empty state
        Column(modifier = Modifier.fillMaxSize()) {
            CreatePinButton(onClick = onCreatePin)
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = DarkText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No pins yet",
                    color = PrimaryText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Create a pin to mark hazards, snags or notes on site",
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 40.dp)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                CreatePinButton(onClick = onCreatePin)
            }

            groupedPins.forEach { (type, typePins) ->
                item {
                    // Section header
                    Text(
                        text = when (type) {
                            SitePinType.HAZARD -> "HAZARDS"
                            SitePinType.SNAG -> "SNAGS"
                            SitePinType.NOTE -> "NOTES"
                            SitePinType.SAFE_ZONE -> "SAFE ZONES"
                        },
                        color = type.color,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                items(typePins, key = { it.id }) { pin ->
                    val dismissState = rememberSwipeToDismissBoxState(
                        confirmValueChange = { value ->
                            if (value == SwipeToDismissBoxValue.EndToStart && pin.createdBy == myName) {
                                onDeletePin(pin)
                                true
                            } else false
                        }
                    )

                    if (pin.createdBy == myName) {
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color(0xFFE5484D))
                                        .padding(horizontal = 20.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(Icons.Filled.Delete, "Delete", tint = Color.White)
                                }
                            },
                            enableDismissFromStartToEnd = false
                        ) {
                            PinRow(pin = pin, currentLocation = currentLocation, onClick = { onPinClick(pin) })
                        }
                    } else {
                        PinRow(pin = pin, currentLocation = currentLocation, onClick = { onPinClick(pin) })
                    }
                }
            }
        }
    }
}

@Composable
private fun CreatePinButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = AmberAccent.copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.LocationOn, null, tint = AmberAccent, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Create Pin",
                color = AmberAccent,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.Add, null, tint = AmberAccent, modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun PinRow(
    pin: SitePin,
    currentLocation: Location?,
    onClick: () -> Unit
) {
    val distanceText = remember(currentLocation, pin) {
        if (currentLocation != null) {
            val pinLoc = Location("pin").apply {
                latitude = pin.latitude
                longitude = pin.longitude
            }
            val dist = currentLocation.distanceTo(pinLoc)
            if (dist < 1000) "~${dist.toInt()}m away" else "~${"%.1f".format(dist / 1000)}km away"
        } else null
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .clickable { onClick() }
            .drawBehind {
                drawRect(
                    color = pin.type.color,
                    topLeft = Offset.Zero,
                    size = Size(4.dp.toPx(), size.height)
                )
            },
        shape = RoundedCornerShape(8.dp),
        color = CardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = pin.type.icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = pin.type.color
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pin.title,
                    color = PrimaryText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    distanceText?.let {
                        Text(it, color = SecondaryText, fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                        "${pin.createdBy} \u00b7 ${formatTimeAgo(pin.createdAt)}",
                        color = DarkText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// --- ALERTS SECTION ---

@Composable
private fun AlertsSection(
    viewModel: ChatViewModel,
    onSendAlert: () -> Unit
) {
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val alertMessages = remember(messages) {
        messages.filter { it.content.startsWith("[SITE_ALERT:") }
            .sortedByDescending { it.timestamp.time }
    }
    val timeFormatter = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }

    if (alertMessages.isEmpty()) {
        Column(modifier = Modifier.fillMaxSize()) {
            SendAlertButton(onClick = onSendAlert)
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    Icons.Filled.Campaign,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = DarkText
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "No alerts sent",
                    color = PrimaryText,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "Site alerts will appear here when sent",
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item { SendAlertButton(onClick = onSendAlert) }
            items(alertMessages, key = { it.id }) { msg ->
                val alert = parseSiteAlert(msg.content) ?: return@items
                AlertHistoryRow(
                    alert = alert,
                    senderName = msg.sender,
                    timestamp = timeFormatter.format(msg.timestamp)
                )
            }
        }
    }
}

@Composable
private fun SendAlertButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFE5484D).copy(alpha = 0.15f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Filled.Campaign, null, tint = Color(0xFFE5484D), modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Send Alert",
                color = Color(0xFFE5484D),
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.weight(1f)
            )
            Icon(Icons.Filled.Add, null, tint = Color(0xFFE5484D), modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
private fun AlertHistoryRow(
    alert: SiteAlert,
    senderName: String,
    timestamp: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp),
        shape = RoundedCornerShape(8.dp),
        color = CardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Coloured icon circle
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = alert.type.color.copy(alpha = 0.15f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = alert.type.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = alert.type.color
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = alert.type.label,
                    color = alert.type.color,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                if (alert.detail.isNotBlank()) {
                    Text(
                        text = alert.detail,
                        color = PrimaryText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "${formatFloorDisplay(alert.floor)} \u00b7 $senderName \u00b7 $timestamp",
                    color = DarkText,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            }
        }
    }
}

// --- BULLETIN SECTION ---

@Composable
private fun BulletinSection() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.AutoMirrored.Filled.Assignment,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = DarkText
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Site Bulletin",
            color = PrimaryText,
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "Post site-wide notices for your team. Coming soon.",
            color = SecondaryText,
            fontFamily = FontFamily.Monospace,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 40.dp)
        )
    }
}
