package com.sharipov.mynotificationmanager.ui.notificationmanagement.component

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sharipov.mynotificationmanager.model.ExcludedAppEntity
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel

@Composable
fun AppListItem(
    settingsViewModel: SettingsViewModel,
    icon: Drawable,
    whichListIsDisplayed: Boolean = true,
    appEntity: ExcludedAppEntity
) {
    var isChecked by remember(
        appEntity.id,
        whichListIsDisplayed,
        appEntity.isExcluded,
        appEntity.isBlocked
    ) {
        mutableStateOf(if (whichListIsDisplayed) appEntity.isExcluded else appEntity.isBlocked)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = rememberDrawablePainter(drawable = icon),
            contentDescription = null,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = appEntity.appName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { newValue ->
                isChecked = newValue
                val newAppEntity = if (whichListIsDisplayed) {
                    appEntity.copy(isExcluded = newValue)
                } else {
                    appEntity.copy(isBlocked = newValue)
                }
                settingsViewModel.updateExcludedApp(newAppEntity)
            }
        )
    }
}
