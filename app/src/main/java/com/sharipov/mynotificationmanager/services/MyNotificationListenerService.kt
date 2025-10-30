package com.sharipov.mynotificationmanager.services

import android.app.Notification
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import androidx.annotation.RequiresApi
import androidx.room.Room
import com.sharipov.mynotificationmanager.data.AppDatabase
import com.sharipov.mynotificationmanager.data.ExcludedAppDao
import com.sharipov.mynotificationmanager.data.NotificationDao
import com.sharipov.mynotificationmanager.data.PreferencesManager
import com.sharipov.mynotificationmanager.model.NotificationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch


class MyNotificationListenerService : NotificationListenerService() {

    private lateinit var notificationDao: NotificationDao
    private lateinit var excludedAppDao: ExcludedAppDao
    private lateinit var database: AppDatabase
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "notification.db"
        ).build()
        notificationDao = database.notificationDao()
        excludedAppDao = database.excludedAppDao()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        try {
            // get package Name
            val packageName = sbn.packageName
            serviceScope.launch {
                val excludedApp = excludedAppDao.getExcludedAppByPackageName(packageName)
                if(PreferencesManager.getBlockNotification(applicationContext)) {
                    cancelNotification(sbn.key)
                }
                else if (excludedApp != null) {
                    if(excludedApp.isBlocked) {
                        cancelNotification(sbn.key)
                    }
                    if (excludedApp.isExcluded) {
                        // get application name
                        val pm = applicationContext.packageManager
                        val appName =
                            pm.getApplicationLabel(
                                pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                            ).toString()
                        val extras = sbn.notification.extras

                        // get user name
                        val (user, group) = getUserAndGroup(appName, extras)

                        // get notification text
                        val text = getText(extras)

                        if (text.isNotEmpty() && group.isNotEmpty()) {
                            val count = notificationDao.checkNotificationExists(user, text, packageName, appName)
                            if (count == 0) {
                                val notificationEntity =
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
                                notificationDao.insert(notificationEntity)
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        database.close()
    }
}

fun getText(extras: Bundle): String {
    var text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
    val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString() ?: ""
    if (text.trim() == "") {
        text = bigText
    } else if (bigText.trim() != "" && text.trim() != "") {
        text = bigText
    }
    return text
}

fun getUserAndGroup(appName: String, extras: Bundle): Pair<String, String> {
    val instagramMods = listOf("Instagram", "Instander")
    var group = extras.getString(Notification.EXTRA_CONVERSATION_TITLE) ?: "not_group"
    var user = extras.getString(Notification.EXTRA_TITLE)?.replace("/", "-") ?: "Unknown"

    if (user.trim() == "") {
        user = "Unknown"
    }
    // We change '/' to '-' because the tail gives an error indicating the
    // wrong path when switching to a user whose name contains this character

    // Separating the user name from the account name because instagram is shit
    if (appName in instagramMods) {
        user = if (user.split(":").size == 2) {
            user.split(":")[1].trim()
        } else {
            user.trim()
        }

        if (group != "not_group") {
            group = group.replace(group.split(" ")[0], "")
            group = group.trim()
        }
    } else if (group != "not_group") {
        user = user.replace("$group:", "").trim()
    }

    if (group == user) {
        group = "not_group"
    }
    return Pair(user, group)
}