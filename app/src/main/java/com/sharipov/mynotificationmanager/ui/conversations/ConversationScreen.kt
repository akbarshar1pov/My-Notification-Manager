package com.sharipov.mynotificationmanager.ui.conversations

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sharipov.mynotificationmanager.navigation.Screens
import com.sharipov.mynotificationmanager.ui.conversations.component.ApplicationItem
import com.sharipov.mynotificationmanager.ui.conversations.component.UserItem
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@SuppressLint("FlowOperatorInvokedInComposition")
@Composable
fun ConversationsScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
    packageName: String,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
) {


    val notificationsFlow = homeViewModel.notificationListFlow
//    val notifications = notificationsFlow.map { notifications ->
//        notifications.firstOrNull { it.packageName == packageName }
//    }
//    val notificationState  = notifications.collectAsState(initial = null)
    val usersListState =notificationsFlow
        .map { list -> list.distinctBy { it.user } }
        .collectAsState(initial = listOf())


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Conversations",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigateUp()
                        }) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Menu"
                        )
                    }
                },
            )
        },
        content = {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(48.dp))

                LazyColumn(modifier = Modifier.fillMaxSize()) {

                    item { Spacer(modifier = Modifier.height(8.dp)) }

                    items(usersListState.value.size) { index ->
                        val notification = usersListState.value[index]
                        if (packageName == notification.packageName) {
                            val userName = notification.user
                            UserItem(userName = userName,
                                modifier = Modifier.fillMaxSize().padding(16.dp, 16.dp, 16.dp)
                                    .clickable {
                                        navController.navigate(
                                            Screens.Chat.route +
                                                    "/${userName}"
                                        )
                                    }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }

                }
            }
        }
    )
}