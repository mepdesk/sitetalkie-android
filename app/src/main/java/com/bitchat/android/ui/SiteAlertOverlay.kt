package com.bitchat.android.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.NotificationCompat
import com.bitchat.android.MainActivity
import com.bitchat.android.R
import kotlinx.coroutines.delay

private const val ALERT_CHANNEL_ID = "site_alert_notifications"

@Composable
fun SiteAlertOverlay(
    alert: SiteAlert,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Play alarm sound and vibrate on first composition
    DisposableEffect(alert) {
        val mediaPlayer = try {
            val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            MediaPlayer.create(context, alarmUri)?.apply { start() }
        } catch (_: Exception) {
            null
        }

        triggerAlertVibration(context)

        onDispose {
            try {
                mediaPlayer?.stop()
                mediaPlayer?.release()
            } catch (_: Exception) { }
        }
    }

    // Auto-dismiss ALL_CLEAR after 3 seconds
    if (alert.type == SiteAlertType.ALL_CLEAR) {
        LaunchedEffect(Unit) {
            delay(3000)
            onDismiss()
        }
    }

    // Pulse animation for icon
    val infiniteTransition = rememberInfiniteTransition(label = "alertPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(alert.type.color.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))

                // Pulsing icon
                Icon(
                    imageVector = alert.type.icon,
                    contentDescription = alert.type.label,
                    modifier = Modifier
                        .size(80.dp)
                        .scale(scale),
                    tint = Color.White
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Alert type name
                Text(
                    text = when (alert.type) {
                        SiteAlertType.FIRE -> "FIRE EVACUATION"
                        SiteAlertType.ALL_CLEAR -> "ALL CLEAR"
                        else -> alert.type.label.uppercase()
                    },
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 32.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Floor
                Text(
                    text = formatFloorDisplay(alert.floor),
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 20.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                // Detail
                if (alert.detail.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = alert.detail,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 18.sp
                        ),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }

                // Sender
                if (alert.senderName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Sent by ${alert.senderName}",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace
                        ),
                        color = Color.White.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Dismiss button
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    border = BorderStroke(2.dp, Color.White),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "DISMISS",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 16.sp
                        ),
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

private fun triggerAlertVibration(context: Context) {
    val pattern = longArrayOf(0, 500, 300, 500, 300, 500)
    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(pattern, -1)
            }
        }
    } catch (_: Exception) { }
}

fun showSiteAlertNotification(context: Context, alert: SiteAlert) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Create high-importance channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            ALERT_CHANNEL_ID,
            "Site Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Emergency site alerts"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 300, 500, 300, 500)
        }
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
    }
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

    val title = when (alert.type) {
        SiteAlertType.FIRE -> "FIRE EVACUATION"
        SiteAlertType.ALL_CLEAR -> "ALL CLEAR"
        else -> alert.type.label.uppercase()
    }

    val body = buildString {
        append(formatFloorDisplay(alert.floor))
        if (alert.detail.isNotBlank()) {
            append(" — ")
            append(alert.detail)
        }
        if (alert.senderName.isNotBlank()) {
            append(" (from ")
            append(alert.senderName)
            append(")")
        }
    }

    val notification = NotificationCompat.Builder(context, ALERT_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_notification)
        .setContentTitle(title)
        .setContentText(body)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_ALARM)
        .setAutoCancel(true)
        .setContentIntent(pendingIntent)
        .setFullScreenIntent(pendingIntent, true)
        .setSound(alarmUri)
        .setVibrate(longArrayOf(0, 500, 300, 500, 300, 500))
        .build()

    try {
        notificationManager.notify(900, notification)
    } catch (_: SecurityException) { }
}
