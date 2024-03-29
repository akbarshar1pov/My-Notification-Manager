package com.sharipov.mynotificationmanager.ui.allnotifications.component

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "SuspiciousIndentation"
)
@Composable
fun NotificationItem(
    homeViewModel: HomeViewModel,
    notification: NotificationEntity,
    navController: NavController,
    context: Context
) {
    val showNotification = remember { mutableStateOf(false) }

    val modifier = Modifier
        .fillMaxSize()
        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
        .combinedClickable(
            onClick = {
                showNotification.value = !showNotification.value
            },
            onLongClick = {
                updateNotification(notification, homeViewModel, context)
            }
        )
        .background(MaterialTheme.colorScheme.background)

        Card(
            modifier = modifier,
            elevation = CardDefaults.cardElevation(
                defaultElevation = 10.dp
            )
        ) {
            NotificationItemContext(homeViewModel, notification)
        }


    NotificationDetailsBottomSheet(
        showNotification = showNotification.value,
        homeViewModel = homeViewModel,
        navController = navController,
        notificationId = notification.id.toString()
    ) { showNotification.value = !showNotification.value }
}

@Composable
fun NotificationItemContext(
    homeViewModel: HomeViewModel,
    notification: NotificationEntity,
) {
    val context = LocalContext.current
    val icon: ImageVector
    val color: Color
    val dataTime = formatNotificationTime(notification.time)

    val appIconDrawable : Drawable? = try {
        context.packageManager.getApplicationIcon(notification.packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        ContextCompat.getDrawable(context, R.drawable.ic_android)
    }

    if (notification.favorite) {
        icon = Icons.Default.Star
        color = MaterialTheme.colorScheme.primary
    } else {
        icon = Icons.Outlined.Star
        color = Color.Gray
    }
    Column {
        Row(modifier = Modifier.padding(16.dp)) {
            Image(
                painter = rememberDrawablePainter(appIconDrawable),
                contentDescription = "App icon",
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(56.dp)
                    .clip(CircleShape),
            )
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        notification.appName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1
                    )
                    Image(
                        imageVector = icon,
                        contentDescription = "",
                        modifier = Modifier
                            .size(32.dp)
                            .clickable {
                                updateNotification(notification, homeViewModel, context)
                            },
                        colorFilter = ColorFilter.tint(color)
                    )
                }
                Text(notification.user, style = MaterialTheme.typography.bodyLarge, maxLines = 1)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        notification.text,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(2f),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1
                    )
                    Text(
                        text = dataTime,
                        modifier = Modifier.weight(0.5f),
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.End
                    )
                }
            }
        }
    }
}

fun formatNotificationTime(timeInMillis: Long): String {
    val currentTimeMillis = System.currentTimeMillis()
    val differenceInMillis = currentTimeMillis - timeInMillis

    val oneDayInMillis = 24 * 60 * 60 * 1000
    val oneWeekInMillis = 6 * oneDayInMillis

    return when {
        differenceInMillis < oneDayInMillis -> {
            val simpleDateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val date = Date(timeInMillis)
            simpleDateFormat.format(date)
        }
        differenceInMillis < oneWeekInMillis -> {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeInMillis
            val shortDayOfWeek = SimpleDateFormat("EE.", Locale.getDefault()).format(calendar.time)
            shortDayOfWeek
        }
        else -> {
            val simpleDateFormat = SimpleDateFormat("dd MMM", Locale.getDefault())
            val date = Date(timeInMillis)
            simpleDateFormat.format(date)
        }
    }
}

fun updateNotification(
    notificationEntity: NotificationEntity,
    homeViewModel: HomeViewModel,
    context: Context
) {
    val notification = NotificationEntity(
        id = notificationEntity.id,
        appName = notificationEntity.appName,
        packageName = notificationEntity.packageName,
        group = notificationEntity.group,
        user = notificationEntity.user,
        text = notificationEntity.text,
        time = notificationEntity.time,
        favorite = !notificationEntity.favorite
    )

    homeViewModel.upgradeNotification(notification = notification)

    val msg = getMessage(context = context, notification.favorite)
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
}

fun getMessage(
    context: Context,
    isFavorite: Boolean
): String {
    val resourceId = if (isFavorite) {
        R.string.notification_added_to_favorites
    } else {
        R.string.notification_removed_from_favorites
    }
    return context.getString(resourceId)
}