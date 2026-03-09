package com.sharipov.mynotificationmanager.ui.settings.components.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.model.AppSettingsEntity
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun autoRemoveDialog(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
): Boolean {
    val openDialog = remember { mutableStateOf(true) }

    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
            onDismiss()
        }
    ) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large
        ) {
            AutoRemoveDialogContent(settingsViewModel, onDismiss, onTimeSelected )
        }
    }
    return openDialog.value
}

@Composable
fun AutoRemoveDialogContent(
    settingsViewModel: SettingsViewModel,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val timeOptions = listOf(
        stringResource(R.string.never),
        stringResource(R.string.one_hour),
        stringResource(R.string.one_day),
        stringResource(R.string.one_week),
        stringResource(R.string.two_weeks),
        stringResource(R.string.one_month)
    )

    val neverText = stringResource(R.string.never)
    var selectedOption by remember { mutableStateOf(neverText) }

    LaunchedEffect(Unit) {
        val appSettings = settingsViewModel.getAppSettings()
        if (appSettings != null) {
            val selectedTimeLong = appSettings.autoDeleteTimeoutLong
            var selectedTime = appSettings.autoDeleteTimeoutString
            if (selectedTime !in timeOptions) {
                selectedTime = when (selectedTimeLong) {
                    0L -> context.getString(R.string.never)
                    3600000L -> context.getString(R.string.one_hour)
                    86400000L -> context.getString(R.string.one_day)
                    604800000L -> context.getString(R.string.one_week)
                    1209600000L -> context.getString(R.string.two_weeks)
                    2592000000L -> context.getString(R.string.one_month)
                    else -> context.getString(R.string.never)
                }
            }
            selectedOption = selectedTime
        } else {
            settingsViewModel.saveAppSettings(AppSettingsEntity(0, 0L, neverText))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.auto_delete_time),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        timeOptions.forEach { text ->
            Row(
                Modifier
                    .selectable(
                        selected = (text == selectedOption),
                        onClick = { selectedOption = text }
                    )
                    .fillMaxWidth()
                    .padding(16.dp)) {
                RadioButton(
                    selected = (text == selectedOption),
                    onClick = null // null so that the processing is only on Row
                )
                Text(
                    text = text,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val selectedTimeLong = when (selectedOption) {
                    context.getString(R.string.never) -> 0L
                    context.getString(R.string.one_hour) -> 3600000L
                    context.getString(R.string.one_day) -> 86400000L
                    context.getString(R.string.one_week) -> 604800000L
                    context.getString(R.string.two_weeks) -> 1209600000L
                    context.getString(R.string.one_month) -> 2592000000L
                    else -> 0L
                }

                scope.launch {
                    settingsViewModel.updateSettings(AppSettingsEntity(0, selectedTimeLong, selectedOption))
                    onTimeSelected(selectedOption)
                    onDismiss()
                }
            }
        ) {
            Text(stringResource(id = R.string.select), color = Color.White,)
        }
    }
}
