package com.sharipov.mynotificationmanager.ui.conversations.component

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter


@Composable
fun ApplicationItem(packageName: String, modifier: Modifier) {

    val context = LocalContext.current
    Log.d("AppName", packageName)
    // get app icon
    val appIconDrawable = try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        // handle the error, e.g. show a placeholder image
        null
    }

    // get app name
    val appName = try {
        context.packageManager.getApplicationLabel(
            context.packageManager.getApplicationInfo(packageName, 0)
        ).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        // handle the error, e.g. show a placeholder name
        "Unknown"
    }

    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.padding(8.dp, 16.dp, 16.dp, 16.dp)) {
                if (appIconDrawable != null) {
                    Image(
                        painter = rememberDrawablePainter(appIconDrawable),
                        contentDescription = "App icon",
                        modifier = Modifier.size(48.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Error icon",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Text(
                    text = appName,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(start = 8.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
            }
        }
    }
}