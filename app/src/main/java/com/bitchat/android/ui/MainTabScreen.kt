package com.bitchat.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private enum class MainTab(
    val label: String,
    val icon: ImageVector
) {
    CHAT("Chat", Icons.Default.ChatBubble),
    NEARBY("Nearby", Icons.Default.CellTower),
    PEOPLE("People", Icons.Default.People),
    SETTINGS("Settings", Icons.Default.Settings)
}

private val TabBarBackground = Color(0xFF1A1C20)
private val TabBarBorder = Color(0xFF2A2C30)
private val ActiveTabColor = Color(0xFFE8960C)
private val InactiveTabColor = Color(0xFF5A5E66)
private val ActivePillColor = Color(0x1AE8960C)
private val PlaceholderTextColor = Color(0xFF8A8E96)

@Composable
fun MainTabScreen(viewModel: ChatViewModel) {
    var selectedTab by remember { mutableStateOf(MainTab.CHAT) }
    val activeSiteAlert by viewModel.activeSiteAlert.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Content area
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    MainTab.CHAT -> ChatScreen(viewModel = viewModel)
                    MainTab.NEARBY -> NearbyPlaceholder()
                    MainTab.PEOPLE -> PeoplePlaceholder()
                    MainTab.SETTINGS -> SettingsScreen()
                }
            }

            // Bottom tab bar
            BottomTabBar(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )
        }

        // Site Alert Overlay — shown on top of ALL tabs regardless of which is active
        val currentAlert = activeSiteAlert
        if (currentAlert != null) {
            SiteAlertOverlay(
                alert = currentAlert,
                onDismiss = { viewModel.dismissSiteAlert() }
            )
        }
    }
}

@Composable
private fun BottomTabBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit
) {
    val borderColor = TabBarBorder
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawLine(
                    color = borderColor,
                    start = Offset(0f, 0f),
                    end = Offset(size.width, 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .background(TabBarBackground)
            .navigationBarsPadding()
            .padding(top = 8.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MainTab.entries.forEach { tab ->
            val isSelected = tab == selectedTab
            TabItem(
                tab = tab,
                isSelected = isSelected,
                onClick = { onTabSelected(tab) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    tab: MainTab,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (isSelected) ActiveTabColor else InactiveTabColor

    Column(
        modifier = modifier
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            color = if (isSelected) ActivePillColor else Color.Transparent
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = tab.icon,
                    contentDescription = tab.label,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = tab.label,
                    color = color,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun NearbyPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Nearby",
            color = Color(0xFFF0F0F0),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Radar coming soon",
                color = PlaceholderTextColor,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
private fun PeoplePlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "People",
            color = Color(0xFFF0F0F0),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 48.dp, bottom = 24.dp)
        )
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No one nearby",
                color = PlaceholderTextColor,
                fontSize = 16.sp
            )
        }
    }
}
