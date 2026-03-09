package com.sharipov.mynotificationmanager.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavController
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.utils.Constants
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars

@Composable
fun PermissionScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val permission = getPermissionList()

    TransparentSystemBars()

    LaunchedEffect(permission) {
        if (permission.all { !it.second }) {
            navController.navigate(Constants.Screens.ALL_NOTIFICATIONS_SCREEN) {
                popUpTo(Constants.Screens.PERMISSIONS_SCREEN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.access_to_features),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            style = androidx.compose.ui.text.TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )
        )

        Spacer(modifier = Modifier.padding(32.dp))

        Text(
            text = stringResource(id = R.string.request_access_description),
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        )
        for (i in permission) {
            if(i.second) {
                when (i.first) {
                    "NotificationListener" -> PermissionItem(
                        stringResource(id = R.string.notifications),
                        stringResource(id = R.string.notifications_description),
                        painterResource(id = R.drawable.ic_notifications_2)
                    )

                    "BatteryOptimizationIgnore" -> PermissionItem(
                        stringResource(id = R.string.battery_optimization),
                        stringResource(id = R.string.battery_optimization_description),
                        painterResource(id = R.drawable.ic_battery)
                    )
                }
            }
        }

        Button(
            modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
            onClick = {
                getPermission(context)
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text(
                text = stringResource(id = R.string.give_permissions),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center),
                color = Color.White
            )
        }
    }
}

@Composable
fun getPermissionList(): List<Pair<String, Boolean>> {
    val context = LocalContext.current
    val notificationListener =
        !NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    val batteryOptimizationIgnore = !isBatteryOptimizationIgnored(context)

    return listOf(
        Pair("NotificationListener", notificationListener),
        Pair("BatteryOptimizationIgnore", batteryOptimizationIgnore)
    )
}

fun isBatteryOptimizationIgnored(context: Context): Boolean {
    val powerManager = getSystemService(context, PowerManager::class.java) as PowerManager
    return powerManager.isIgnoringBatteryOptimizations(context.packageName)
}

@SuppressLint("BatteryLife")
fun requestBatteryOptimization(context: Context) {
    val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
    intent.data = Uri.parse("package:${context.packageName}")
    context.launchSettingsIntent(intent)
}

fun getPermission(context: Context) {
    val notificationListener =
        !NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.packageName)
    val batteryOptimizationIgnore = !isBatteryOptimizationIgnored(context)

    if (notificationListener) {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        context.launchSettingsIntent(intent)
    }
    if (batteryOptimizationIgnore) {
        requestBatteryOptimization(context)
    }
}

private fun Context.launchSettingsIntent(intent: Intent) {
    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    startActivity(intent)
}



@Composable
fun PermissionItem(permission: String, description: String, icon: Painter) {
    val modifier = Modifier
        .padding(
            start = 16.dp,
            end = 16.dp,
            top = 8.dp,
            bottom = 8.dp
        )
        .fillMaxWidth()

    HorizontalDivider(
        modifier = modifier,
        thickness = 2.dp
    )
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = icon,
                modifier = Modifier.size(64.dp),
                contentDescription = null
            )
            Text(
                text = permission,
                modifier = modifier.size(32.dp),
                style = androidx.compose.ui.text.TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                )
            )
        }
        Text(
            text = description,
            modifier = modifier
        )
    }
}
