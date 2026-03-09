package com.sharipov.mynotificationmanager.ui.bottombarcomponent

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.utils.Constants

@Composable
fun BottomBar(
    route: String,
    navigateToApplications: () -> Unit = {},
    navigateToAllNotifications: () -> Unit = {},
    navigateToSettings: () -> Unit = {},
    navigateToFavorite: () -> Unit = {},
    navigateToNotificationManagement: () -> Unit = {},
) {
    val items = listOf(
        Constants.Screens.FAVORITE_SCREEN to painterResource(id = R.drawable.ic_archive),
        Constants.Screens.APPLICATION_SCREEN to painterResource(id = R.drawable.ic_apps),
        Constants.Screens.ALL_NOTIFICATIONS_SCREEN to painterResource(id = R.drawable.ic_notifications),
        Constants.Screens.NOTIFICATION_MANAGEMENT_SCREEN to painterResource(id = R.drawable.ic_rule),
        Constants.Screens.SETTINGS_SCREEN to painterResource(id = R.drawable.ic_settings),
    )
    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(16.dp),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        items.forEach { data ->
            NavigationBarItem(
                selected = route == data.first,
                onClick = {
                    if(route != data.first) {
                        when (data.first) {
                            items[0].first -> navigateToFavorite()
                            items[1].first -> navigateToApplications()
                            items[2].first -> navigateToAllNotifications()
                            items[3].first -> navigateToNotificationManagement()
                            items[4].first -> navigateToSettings()
                        }
                    }
                },
                icon = {
                    Icon(
                        painter = data.second,
                        modifier = Modifier.size(32.dp),
                        contentDescription = "Favorites"
                    )
                }
            )

        }
    }
}
