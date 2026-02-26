package com.bitchat.android.ui

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val SetupBackground = Color(0xFF0E1012)
private val CardBackground = Color(0xFF1A1C20)
private val AmberAccent = Color(0xFFE8960C)
private val SubtitleColor = Color(0xFF8A8E96)
private val PrimaryText = Color(0xFFF0F0F0)

@Composable
fun SetupScreen(
    onSetupComplete: () -> Unit
) {
    val context = LocalContext.current
    var displayName by remember { mutableStateOf("") }
    val isValid = displayName.isNotBlank() && displayName.length <= 15

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SetupBackground)
            .padding(horizontal = 24.dp)
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        Text(
            text = "Welcome to SiteTalkie",
            color = PrimaryText,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Set your display name so your site knows who you are",
            color = SubtitleColor,
            fontSize = 15.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Display name input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            if (displayName.isEmpty()) {
                Text(
                    text = "Your name",
                    color = SubtitleColor,
                    fontSize = 16.sp
                )
            }
            BasicTextField(
                value = displayName,
                onValueChange = { if (it.length <= 15) displayName = it },
                textStyle = TextStyle(
                    color = PrimaryText,
                    fontSize = 16.sp
                ),
                cursorBrush = SolidColor(AmberAccent),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Get Started button
        Button(
            onClick = {
                if (isValid) {
                    // Save name to existing DataManager prefs
                    context.getSharedPreferences("bitchat_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putString("nickname", displayName.trim())
                        .apply()
                    setSetupCompleted(context)
                    onSetupComplete()
                }
            },
            enabled = isValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AmberAccent,
                contentColor = Color.Black,
                disabledContainerColor = AmberAccent.copy(alpha = 0.3f),
                disabledContentColor = Color.Black.copy(alpha = 0.3f)
            )
        ) {
            Text(
                text = "Get Started",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
