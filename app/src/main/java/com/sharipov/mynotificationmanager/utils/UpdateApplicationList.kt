package com.sharipov.mynotificationmanager.utils

import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.room.Transaction
import com.sharipov.mynotificationmanager.model.ExcludedAppEntity
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.flow.first

@Composable
fun UpdateApplicationList(settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    LaunchedEffect(Unit) {
        val apps = packageManager.getInstalledPackages(0)
        val appListFromSource = mutableListOf<ExcludedAppEntity>()

        for (i in apps) {
            val appName =
                packageManager.getApplicationLabel(
                    packageManager.getApplicationInfo(i.packageName, PackageManager.GET_META_DATA)
                ).toString()

            val app = ExcludedAppEntity(
                packageName = i.packageName.toString(),
                appName = appName,
                isExcluded = true,
                isBlocked = false
            )
            appListFromSource.add(app)
        }
        syncAppList(appListFromSource, settingsViewModel)
    }
}

@Transaction
suspend fun syncAppList(appListFromSource: List<ExcludedAppEntity>, settingsViewModel: SettingsViewModel) {
    val appListFromDatabase = settingsViewModel.getAllExcludedApps().first()
    val missingApps = appListFromSource.filter { app ->
        appListFromDatabase.none { it.packageName == app.packageName }
    }
    if (missingApps.isNotEmpty()) {
        for(i in missingApps)
            settingsViewModel.addExcludedApp(i)
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