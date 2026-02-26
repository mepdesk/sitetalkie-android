package com.bitchat.android.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val GradientStart = Color(0xFFE8960C)
private val GradientEnd = Color(0xFFD4870B)

@Composable
fun MessageLimitBanner(
    onUpgradeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(listOf(GradientStart, GradientEnd))
            )
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "You've reached the free message limit",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "Upgrade to Pro for unlimited messages",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(
            onClick = onUpgradeClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = GradientStart
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 4.dp)
        ) {
            Text(
                text = "Upgrade",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
