package com.sharipov.mynotificationmanager.ui.conversations

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.ui.topbarscomponent.TopBarContent
import com.sharipov.mynotificationmanager.ui.conversations.component.UserItem
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel

@SuppressLint("FlowOperatorInvokedInComposition", "UnusedMaterial3ScaffoldPaddingParameter", "SuspiciousIndentation",
    "RememberReturnType", "UnrememberedMutableState"
)
@Composable
fun ConversationsScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
    packageName: String,
) {
    val context = LocalContext.current
    val userNamesFlow = homeViewModel.getApplicationUserNames(packageName)
    val usersListState by userNamesFlow.collectAsState(initial = listOf())
    val allChats = (usersListState.filter { it.group != "not_group" }.distinctBy { it.group }) +
            (usersListState.filter { it.group == "not_group" })

    TransparentSystemBars()

    // get app icon
    val appIconDrawable : Drawable? = try {
        context.packageManager.getApplicationIcon(packageName)
    } catch (e: PackageManager.NameNotFoundException) {
        ContextCompat.getDrawable(context, R.drawable.ic_android)
    }

    // get app name
    val appName = try {
        context.packageManager.getApplicationLabel(
            context.packageManager.getApplicationInfo(packageName, 0)
        ).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        stringResource(id = R.string.unknown)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBarContent(
                title =  appName,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                appIcon = appIconDrawable,
                onNavigationClick = { navController.navigateUp() }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item { Spacer(modifier = Modifier.height(8.dp)) }

                items(allChats) { userGroup ->
                    val modifier = Modifier.fillMaxSize().padding(16.dp, 16.dp, 16.dp)
                    val userName = userGroup.user
                    val group = userGroup.group

                    UserItem(
                        homeViewModel = homeViewModel,
                        navController = navController,
                        group = group,
                        userName = userName,
                        packageName = packageName,
                        userCount = usersListState.size - 1,
                        modifier = modifier
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
