package com.sharipov.mynotificationmanager.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

suspend fun exportDatabase(context: Context, homeViewModel: HomeViewModel): Pair<Boolean, String> {
    return withContext(Dispatchers.IO) {
        val backupDir = getBackupDirectory(context)
            ?: return@withContext Pair(false, context.getString(R.string.external_storage_not_available))

        val timeStamp = SimpleDateFormat("yyyy.MM.dd_HH.mm.ss", Locale.getDefault()).format(Date())
        val fileName = "${timeStamp}_backup.json"
        val exportFile = File(backupDir, fileName)

        val notifications = homeViewModel.notificationListFlow.first()
        val jsonString = Gson().toJson(notifications)

        exportFile.writeText(jsonString)
        Pair(true, fileName)
    }
}

private fun getBackupDirectory(context: Context): File? {
    val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) ?: return null
    return File(documentsDir, "backups").apply {
        mkdirs()
    }
}


suspend fun importDatabase(context: Context, homeViewModel: HomeViewModel, uri: Uri?): Pair<Boolean, String> {
    val contentResolver: ContentResolver = context.contentResolver
    return withContext(Dispatchers.IO) {
        try {
            if (uri == null) {
                return@withContext Pair(false, context.getString(R.string.file_not_selected))
            }

            val jsonString = contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).readText()
            } ?: return@withContext Pair(false, context.getString(R.string.error_the_file_is_corrupted))

            val gson = Gson()
            val listType = object : TypeToken<List<NotificationEntity>>() {}.type
            val notifications: List<NotificationEntity> = gson.fromJson(jsonString, listType) ?: emptyList()

            if (notifications.isEmpty()) {
                return@withContext Pair(false, context.getString(R.string.error_the_file_is_corrupted))
            }

            homeViewModel.importNotifications(notifications)
            Pair(true, context.getString(R.string.notification_imported))
        } catch (_: Exception) {
            Pair(false, context.getString(R.string.error_the_file_is_corrupted))
        }
    }
}

fun shareFile(context: Context, fileName: String) {
    val fileProviderAuthority = "com.sharipov.mynotificationmanager.fileprovider"
    val backupDir = getBackupDirectory(context) ?: return
    val exportFile = File(backupDir, fileName)
    if (!exportFile.exists()) {
        return
    }
    val fileUri = FileProvider.getUriForFile(context, fileProviderAuthority, exportFile)
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "application/json"
        putExtra(Intent.EXTRA_STREAM, fileUri)
        addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    }
    val chooserIntent = Intent.createChooser(shareIntent, null)
    chooserIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION)
    context.startActivity(chooserIntent)
}
