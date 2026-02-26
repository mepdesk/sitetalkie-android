package com.bitchat.android.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

private val SidebarBackground = Color(0xFF0E1012)
private val SectionLabelColor = Color(0xFF8A8E96)
private val PrimaryText = Color(0xFFF0F0F0)
private val AmberAccent = Color(0xFFE8960C)
private val CardBackground = Color(0xFF1A1C20)
private val FooterTextColor = Color(0xFF5A5E66)
private val LockGreen = Color(0xFF4CAF50)
private val DimOverlay = Color(0x80000000)

@Composable
fun ChannelSidebarSheet(
    isVisible: Boolean,
    currentChannel: String?,
    onChannelSelected: (String?) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Dim overlay
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.zIndex(10f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DimOverlay)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) { onDismiss() }
        )
    }

    // Sidebar panel
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it }),
        modifier = Modifier.zIndex(11f)
    ) {
        Column(
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight()
                .background(SidebarBackground)
                .padding(top = 48.dp) // status bar clearance
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Channels",
                    color = PrimaryText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = "Close",
                        tint = SectionLabelColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // PUBLIC section
            Text(
                text = "PUBLIC",
                color = SectionLabelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // #site row
            val isSiteSelected = currentChannel == null
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChannelSelected(null) }
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "#site",
                    color = PrimaryText,
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                // Public badge pill
                Box(
                    modifier = Modifier
                        .background(SectionLabelColor, RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Public",
                        color = PrimaryText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                if (isSiteSelected) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Selected",
                        tint = AmberAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // YOUR CHANNELS section
            Text(
                text = "YOUR CHANNELS",
                color = SectionLabelColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Pro channels
            val proChannels = listOf("#general", "#defects", "#deliveries")
            proChannels.forEach { channel ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            Toast
                                .makeText(context, "Pro feature coming soon", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = SectionLabelColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = channel,
                        color = PrimaryText,
                        fontSize = 15.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Pro badge pill
                    Box(
                        modifier = Modifier
                            .background(AmberAccent, RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "Pro",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Browse Channels button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(CardBackground, RoundedCornerShape(12.dp))
                    .clickable {
                        Toast
                            .makeText(context, "Pro feature coming soon", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = AmberAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Browse Channels",
                    color = PrimaryText,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = null,
                    tint = LockGreen,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Channels are end-to-end encrypted",
                    color = FooterTextColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
