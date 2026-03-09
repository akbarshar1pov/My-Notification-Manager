package com.sharipov.mynotificationmanager.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.room.Transaction
import com.sharipov.mynotificationmanager.model.ExcludedAppEntity
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

suspend fun updateApplicationList(
    context: Context,
    settingsViewModel: SettingsViewModel
) = withContext(Dispatchers.IO) {
    val packageManager = context.packageManager
    val apps = packageManager.getInstalledPackages(0)
    val appListFromSource = buildList {
        apps.forEach { packageInfo ->
            val appName = runCatching {
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(packageInfo.packageName, PackageManager.GET_META_DATA)
                ).toString()
            }.getOrDefault(packageInfo.packageName)

            add(
                ExcludedAppEntity(
                    packageName = packageInfo.packageName,
                    appName = appName,
                    isExcluded = true,
                    isBlocked = false
                )
            )
        }
    }

    syncAppList(appListFromSource, settingsViewModel)
}

@Transaction
suspend fun syncAppList(appListFromSource: List<ExcludedAppEntity>, settingsViewModel: SettingsViewModel) {
    val appListFromDatabase = settingsViewModel.getAllExcludedApps().first()
    val missingApps = appListFromSource.filter { app ->
        appListFromDatabase.none { it.packageName == app.packageName }
    }
    if (missingApps.isNotEmpty()) {
        for (i in missingApps) {
            settingsViewModel.addExcludedApp(i)
        }
    }
    val appsToRemove = appListFromDatabase.filter { app ->
        appListFromSource.none { it.packageName == app.packageName }
    }
    if (appsToRemove.isNotEmpty()) {
        for (i in appsToRemove) {
            settingsViewModel.deleteExcludedAppByPackageName(i.packageName)
        }
    }
}
