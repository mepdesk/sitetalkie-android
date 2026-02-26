package com.bitchat.android.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class SiteAlertType(
    val label: String,
    val color: Color,
    val icon: ImageVector,
    val protocolName: String
) {
    FIRE(
        label = "Fire / Evacuation",
        color = Color(0xFFE5484D),
        icon = Icons.Filled.LocalFireDepartment,
        protocolName = "FIRE"
    ),
    WARNING(
        label = "General Warning",
        color = Color(0xFFE8960C),
        icon = Icons.Filled.Warning,
        protocolName = "WARNING"
    ),
    MEDICAL(
        label = "Medical Emergency",
        color = Color(0xFF3B82F6),
        icon = Icons.Filled.LocalHospital,
        protocolName = "MEDICAL"
    ),
    CRANE(
        label = "Crane / Lifting Op",
        color = Color(0xFFEAB308),
        icon = Icons.Filled.SwapVert,
        protocolName = "CRANE"
    ),
    ALL_CLEAR(
        label = "All Clear",
        color = Color(0xFF34C759),
        icon = Icons.Filled.VerifiedUser,
        protocolName = "ALL_CLEAR"
    );

    companion object {
        fun fromProtocolName(name: String): SiteAlertType? =
            entries.find { it.protocolName == name }
    }
}

data class SiteAlert(
    val type: SiteAlertType,
    val floor: String,
    val detail: String,
    val senderName: String = ""
)

private val SITE_ALERT_REGEX = Regex("""\[SITE_ALERT:(\w+):(F-?\d+|B\d+)]\s?(.*)""")

fun parseSiteAlert(messageContent: String): SiteAlert? {
    val match = SITE_ALERT_REGEX.find(messageContent) ?: return null
    val typeName = match.groupValues[1]
    val floor = match.groupValues[2]
    val detail = match.groupValues[3].trim()
    val type = SiteAlertType.fromProtocolName(typeName) ?: return null
    return SiteAlert(type = type, floor = floor, detail = detail)
}

fun formatSiteAlert(type: SiteAlertType, floorNumber: Int, detail: String): String {
    val floorStr = if (floorNumber < 0) "B${-floorNumber}" else "F$floorNumber"
    val detailPart = if (detail.isNotBlank()) " $detail" else ""
    return "[SITE_ALERT:${type.protocolName}:$floorStr]$detailPart"
}

fun formatFloorDisplay(floor: String): String {
    return when {
        floor.startsWith("B") -> {
            val num = floor.removePrefix("B").toIntOrNull() ?: 0
            "Basement $num"
        }
        floor.startsWith("F") -> {
            val num = floor.removePrefix("F").toIntOrNull() ?: 0
            if (num == 0) "Ground Floor" else "Floor $num"
        }
        else -> floor
    }
}
