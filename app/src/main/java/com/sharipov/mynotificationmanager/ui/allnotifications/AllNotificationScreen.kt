package com.sharipov.mynotificationmanager.ui.allnotifications

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.model.NotificationEntity
import com.sharipov.mynotificationmanager.navigation.Screens
import com.sharipov.mynotificationmanager.ui.allnotifications.component.FilterSheet
import com.sharipov.mynotificationmanager.ui.allnotifications.component.MyFloatingActionButton
import com.sharipov.mynotificationmanager.ui.allnotifications.component.NotificationItem
import com.sharipov.mynotificationmanager.ui.bottombarcomponent.BottomBar
import com.sharipov.mynotificationmanager.ui.topbarscomponent.SearchTopBarContent
import com.sharipov.mynotificationmanager.utils.Constants
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars
import com.sharipov.mynotificationmanager.utils.dateConverter
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter", "RememberReturnType",
    "UnrememberedMutableState"
)
@Composable
fun AllNotificationScreen(
    homeViewModel: HomeViewModel,
    navController: NavController,
) {
    val context = LocalContext.current

    var showSearch by remember { mutableStateOf(false) }
    var showFilters by remember { mutableStateOf(false) }
    var showStatistic by remember { mutableStateOf(false) }
    var fromDate by remember { mutableStateOf("") }
    var toDate by remember { mutableStateOf("") }
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute =
        currentNavBackStackEntry?.destination?.route ?: Constants.Screens.APPLICATION_SCREEN
    var searchText by remember { mutableStateOf("") }
    val allNotifications by homeViewModel.notificationListFlow.collectAsState(emptyList())
    val selectedNotificationsFlow = remember(homeViewModel, searchText, fromDate, toDate) {
        when {
            searchText.isNotBlank() -> homeViewModel.searchNotifications(searchText)
            fromDate.isNotBlank() || toDate.isNotBlank() -> {
                val fromDateLongValue = dateConverter("from", fromDate)
                val toDateLongValue = dateConverter("to", toDate)
                homeViewModel.getNotificationsFromData(fromDateLongValue, toDateLongValue)
            }
            else -> homeViewModel.notificationListFlow
        }
    }
    val notificationFlow by selectedNotificationsFlow.collectAsState(emptyList())
    val dates = remember { getWeekDays() }
    val weeklyData = remember(allNotifications) { getWeeklyCounts(allNotifications) }


    TransparentSystemBars()

    Scaffold(
        topBar = {
            SearchTopBarContent(
                title = stringResource(id = R.string.all_notification),
                onSearchClick = { showSearch = !showSearch },
                searchVisible = showSearch,
                searchText = searchText,
                onSearchTextChange = { searchText = it }
            )
        },
        bottomBar = {
            BottomBar(
                route = currentRoute,
                navigateToApplications = { navController.navigate(Screens.Applications.route) },
                navigateToAllNotifications = { navController.navigate(Screens.AllNotifications.route) },
                navigateToSettings = { navController.navigate(Screens.Settings.route) },
                navigateToFavorite = { navController.navigate(Screens.Favorite.route) },
                navigateToNotificationManagement = { navController.navigate(Screens.NotificationManagement.route) },
            )
        },
        floatingActionButton = {
            MyFloatingActionButton(
                onFiltersClick = { showFilters = !showFilters }
            )
        },
        floatingActionButtonPosition = FabPosition.End
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!showSearch) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                        .clickable { showStatistic = !showStatistic },
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 10.dp
                    )
                ) {
                    val statisticText =
                        "${stringResource(id = R.string.count_of_your_notification)} ${
                            if (!showStatistic) notificationFlow.size else weeklyData.sum()
                        }"
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = statisticText,
                            modifier = Modifier
                                .padding(8.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                        AnimatedVisibility(
                            visible = showStatistic
                        ) {
                            BarChart(dates, weeklyData)
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = !showSearch || searchText.isNotEmpty(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = notificationFlow,
                        key = { notification ->
                            notification.id ?: "${notification.packageName}_${notification.time}_${notification.text.hashCode()}"
                        }
                    ) { notification ->
                        NotificationItem(
                            homeViewModel = homeViewModel,
                            notification = notification,
                            navController = navController,
                            context = context,
                        )
                    }
                    item { Spacer(modifier = Modifier.height(24.dp)) }
                }
            }
        }
    }

    FilterSheet(
        showFilters = showFilters,
        fromDate = fromDate,
        toDate = toDate,
        onDismiss = { showFilters = false },
        onDatesChanged = { newFromDate, newToDate ->
            fromDate = newFromDate
            toDate = newToDate
        }
    )
}

//@Composable
//fun getApplications(homeViewModel: HomeViewModel): MutableState<List<AppInfo>> {
//    val applicationListState = homeViewModel.getApplications().collectAsState(initial = listOf())
//
//    return remember(applicationListState.value) {
//        mutableStateOf(
//            applicationListState.value.map { application ->
//                AppInfo(packageName = application.packageName, appName = application.appName, isActive = true)
//            }
//        )
//    }
//}

fun getWeeklyCounts(notifications: List<NotificationEntity>): List<Int> {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val countsByDay = notifications
        .groupingBy { notification -> dateFormat.format(Date(notification.time)) }
        .eachCount()
    val calendar = Calendar.getInstance().apply { add(Calendar.DAY_OF_MONTH, -6) }

    return List(7) {
        val day = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        countsByDay[day] ?: 0
    }
}

fun getWeekDays(): List<String> {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EE", Locale.getDefault())
    return (0 until 7).map {
        val date = dateFormat.format(calendar.time)
        calendar.add(Calendar.DAY_OF_WEEK, -1)
        date
    }.reversed()
}

@Composable
fun BarChart(dates: List<String>, data: List<Int>, modifier: Modifier = Modifier) {
    var maxCount by remember { mutableIntStateOf(0) }
    val color = MaterialTheme.colorScheme.onBackground
    val columnColor = MaterialTheme.colorScheme.primary

    data.forEach { count ->
        if (count > maxCount) {
            maxCount = count
        }
    }

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            data.forEachIndexed { _, date ->
                Text(
                    text = date.toString(),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .height(300.dp)
        ) {
            val barWidth = size.width / (data.size * 2)
            val spaceBetweenBars = size.width / (data.size * 4)

            data.forEachIndexed { index, count ->
                val barHeight = size.height * count / maxCount
                val startX = (index * (barWidth * 2)) + spaceBetweenBars
                val startY = size.height - barHeight
                drawRect(
                    color = columnColor,
                    size = Size(barWidth, barHeight),
                    topLeft = Offset(startX, startY),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            dates.forEachIndexed { _, date ->
                Text(
                    text = date,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = color
                )
            }
        }
    }
}
