package com.sharipov.mynotificationmanager.ui.notificationmanagement.component

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.data.PreferencesManager

@Composable
fun DoNotDisturbMode(initialChecked: Boolean, context: Context) {
    var isChecked by remember { mutableStateOf(initialChecked) }

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_notifications_off),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            colorFilter = ColorFilter.tint(if (isSystemInDarkTheme()) Color.White else Color.DarkGray)
        )
        Text(
            text = stringResource(id = R.string.all_notification),
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
        )
        Switch(
            checked = isChecked,
            onCheckedChange = { newValue ->
                isChecked = newValue
                PreferencesManager.updateBlockNotificationStatus(context, newValue)
            }
        )
    }
    HorizontalDivider(color = Color.Gray, thickness = 1.dp)
}
