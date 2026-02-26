package com.bitchat.android.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

private val SplashBackground = Color(0xFF0E1012)
private val AmberAccent = Color(0xFFE8960C)
private val SubtitleColor = Color(0xFF8A8E96)

private const val PREFS_NAME = "sitetalkie_prefs"
private const val KEY_HAS_COMPLETED_SETUP = "sitetalkie.hasCompletedSetup"

fun hasCompletedSetup(context: Context): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(KEY_HAS_COMPLETED_SETUP, false)
}

fun setSetupCompleted(context: Context) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(KEY_HAS_COMPLETED_SETUP, true)
        .apply()
}

enum class SplashDestination {
    SETUP,
    MAIN
}

@Composable
fun SplashScreen(
    onNavigate: (SplashDestination) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2000)
        if (hasCompletedSetup(context)) {
            onNavigate(SplashDestination.MAIN)
        } else {
            onNavigate(SplashDestination.SETUP)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SplashBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "SiteTalkie",
                color = AmberAccent,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Scanning for nearby users",
                color = SubtitleColor,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            CircularProgressIndicator(
                color = AmberAccent,
                strokeWidth = 2.dp,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
