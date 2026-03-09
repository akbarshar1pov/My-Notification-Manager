package com.sharipov.mynotificationmanager.services

import android.app.Notification
import android.content.pm.PackageManager
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.sharipov.mynotificationmanager.data.ExcludedAppDao
import com.sharipov.mynotificationmanager.data.NotificationDao
import com.sharipov.mynotificationmanager.data.PreferencesManager
import com.sharipov.mynotificationmanager.model.NotificationEntity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var notificationDao: NotificationDao

    @Inject
    lateinit var excludedAppDao: ExcludedAppDao

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val packageName = sbn.packageName
        val appContext = applicationContext

        serviceScope.launch {
            try {
                val excludedApp = excludedAppDao.getExcludedAppByPackageName(packageName)
                if (PreferencesManager.getBlockNotification(appContext)) {
                    cancelNotification(sbn.key)
                    return@launch
                }

                if (excludedApp == null) {
                    return@launch
                }

                if (excludedApp.isBlocked) {
                    cancelNotification(sbn.key)
                }

                if (!excludedApp.isExcluded) {
                    return@launch
                }

                val appName = runCatching {
                    val packageManager = appContext.packageManager
                    packageManager.getApplicationLabel(
                        packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                    ).toString()
                }.getOrDefault(packageName)

                val extras = sbn.notification.extras
                val (user, group) = getUserAndGroup(appName, extras)
                val text = getText(extras)

                if (text.isBlank() || group.isBlank()) {
                    return@launch
                }

                val count = notificationDao.checkNotificationExists(user, text, packageName, appName)
                if (count == 0) {
                    notificationDao.insert(
                        NotificationEntity(
                            id = null,
                            appName = appName,
                            packageName = packageName,
                            group = group,
                            user = user,
                            text = text,
                            time = System.currentTimeMillis(),
                            favorite = false
                        )
                    )
                }
            } catch (_: Exception) {
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}

fun getText(extras: Bundle): String {
    var text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
    val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
    if (text.isBlank()) {
        text = bigText
    } else if (bigText.isNotBlank()) {
        text = bigText
    }
    return text.trim()
}

fun getUserAndGroup(appName: String, extras: Bundle): Pair<String, String> {
    val instagramMods = listOf("Instagram", "Instander")
    var group = extras.getString(Notification.EXTRA_CONVERSATION_TITLE) ?: "not_group"
    var user = extras.getString(Notification.EXTRA_TITLE)?.replace("/", "-") ?: "Unknown"

    if (user.isBlank()) {
        user = "Unknown"
    }

    // We change '/' to '-' because the app uses the value in a navigation path.
    if (appName in instagramMods) {
        user = if (user.split(":").size == 2) {
            user.split(":")[1].trim()
        } else {
            user.trim()
        }

        if (group != "not_group") {
            group = group.replace(group.split(" ")[0], "").trim()
        }
    } else if (group != "not_group") {
        user = user.replace("$group:", "").trim()
    }

    if (group == user) {
        group = "not_group"
    }
    return Pair(user, group)
}
