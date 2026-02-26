package com.bitchat.android.ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ScreenBackground = Color(0xFF0E1012)
private val CardBackground = Color(0xFF1A1C20)
private val AmberAccent = Color(0xFFE8960C)
private val PrimaryText = Color(0xFFF0F0F0)
private val SecondaryText = Color(0xFF8A8E96)
private val FooterText = Color(0xFF5A5E66)

private data class ProFeature(
    val icon: ImageVector,
    val title: String,
    val description: String
)

private val proFeatures = listOf(
    ProFeature(Icons.Filled.Lock, "Private Channels", "Create channels for your company"),
    ProFeature(Icons.Filled.PhotoCamera, "Photo Sharing", "Send site photos over the mesh"),
    ProFeature(Icons.Filled.Mic, "Voice Notes", "Record and send voice messages"),
    ProFeature(Icons.Filled.Schedule, "Unlimited History", "Keep all your messages forever"),
    ProFeature(Icons.Filled.CheckCircle, "Read Receipts", "Know when your message was seen"),
    ProFeature(Icons.Filled.LocationOn, "Location Pins", "Pin hazards and snags to locations"),
    ProFeature(Icons.Filled.Badge, "Role Badges", "Show your trade on site"),
    ProFeature(Icons.Filled.Description, "Document Sharing", "Share PDFs and files over the mesh")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProUpgradeSheet(
    isPresented: Boolean,
    onDismiss: () -> Unit
) {
    if (!isPresented) return

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = ScreenBackground,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        dragHandle = null
    ) {
        ProUpgradeContent(onClose = onDismiss)
    }
}

@Composable
private fun ProUpgradeContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.92f)
            .background(ScreenBackground)
    ) {
        // Close button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp, top = 12.dp, end = 12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "Close",
                tint = SecondaryText,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { onClose() }
            )
        }

        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Header
            Text(
                text = "SiteTalkie Pro",
                color = AmberAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Unlock the full site experience",
                color = SecondaryText,
                fontSize = 16.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Feature cards
            proFeatures.forEach { feature ->
                ProFeatureCard(feature)
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Pricing
            Text(
                text = "\u00A33.99/month",
                color = PrimaryText,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cancel anytime",
                color = SecondaryText,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CTA Button
            Button(
                onClick = {
                    Log.d("ProUpgrade", "Pro upgrade tapped")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberAccent,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Start Free Trial",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "7 days free, then \u00A33.99/month",
                color = SecondaryText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Footer links
            Text(
                text = "Restore Purchase",
                color = SecondaryText,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    Log.d("ProUpgrade", "Restore Purchase tapped")
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Terms \u00B7 Privacy",
                color = FooterText,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.clickable {
                    Log.d("ProUpgrade", "Terms/Privacy tapped")
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ProFeatureCard(feature: ProFeature) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = feature.icon,
            contentDescription = null,
            tint = AmberAccent,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column {
            Text(
                text = feature.title,
                color = PrimaryText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = feature.description,
                color = SecondaryText,
                fontSize = 14.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
