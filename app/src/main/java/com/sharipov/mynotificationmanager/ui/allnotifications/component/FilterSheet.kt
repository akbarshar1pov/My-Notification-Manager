package com.sharipov.mynotificationmanager.ui.allnotifications.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.maxkeppeker.sheets.core.models.base.SheetState
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.sharipov.mynotificationmanager.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSheet(
    showFilters: Boolean,
    fromDate: String,
    toDate: String,
    onDismiss: () -> Unit,
    onDatesChanged: (String, String) -> Unit,
) {
    val fromDataCalendarState = rememberSheetState()
    val toDataCalendarState = rememberSheetState()
    val fromDateState = remember(showFilters) { mutableStateOf(fromDate) }
    val toDateState = remember(showFilters) { mutableStateOf(toDate) }

    if (showFilters) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
        ) {
            BottomSheetContent(
                fromDataCalendarState = fromDataCalendarState,
                toDataCalendarState = toDataCalendarState,
                fromDate = fromDateState,
                toDate = toDateState,
                onReset = {
                    fromDateState.value = ""
                    toDateState.value = ""
                    onDatesChanged("", "")
                }
            )
        }
    }

    CalendarDialog(
        state = fromDataCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            fromDateState.value = date.toString()
            onDatesChanged(fromDateState.value, toDateState.value)
        }
    )

    CalendarDialog(
        state = toDataCalendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            toDateState.value = date.toString()
            onDatesChanged(fromDateState.value, toDateState.value)
        }
    )
}

@Composable
fun BottomSheetContent(
    fromDataCalendarState: SheetState,
    toDataCalendarState: SheetState,
    fromDate: androidx.compose.runtime.MutableState<String>,
    toDate: androidx.compose.runtime.MutableState<String>,
    onReset: () -> Unit,
){

    Column(
        modifier = Modifier
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = stringResource(id = R.string.filters),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp),
        )

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )

        DataPicker(fromDataCalendarState, toDataCalendarState, fromDate, toDate, onReset)

        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}


@Composable
fun DataPicker(
    fromDataCalendarState: SheetState,
    toDataCalendarState: SheetState,
    fromDate: MutableState<String>,
    toDate: MutableState<String>,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 36.dp, end = 36.dp, bottom = 16.dp)
    ){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Select data",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .wrapContentSize()
            )

            Button(
                modifier = Modifier
                    .wrapContentSize(),
                onClick = onReset
            ) {
                Text(stringResource(id = R.string.reset), color = Color.White)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    fromDataCalendarState.show()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${stringResource(id = R.string.from)}  ${fromDate.value}")
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clickable {
                    toDataCalendarState.show()
                },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("${stringResource(id = R.string.to)}  ${toDate.value}")
            Icon(
                modifier = Modifier.size(24.dp),
                imageVector = Icons.Default.DateRange,
                contentDescription = null
            )
        }
    }
}

//data class AppInfo(val packageName: String, val appName: String, var isActive: Boolean)

//fun setAllApplicationActiveStatus(
//    appInfoList: MutableState<List<AppInfo>>,
//    activeStatus: Boolean
//) {
//    val updatedList = appInfoList.value.map { appInfo ->
//        appInfo.copy(isActive = activeStatus)
//    }
//    appInfoList.value = updatedList
//}
//
//fun setApplicationStatus(appInfoList: MutableState<List<AppInfo>>, appInfo: AppInfo) {
//    val index = appInfoList.value.indexOf(appInfo)
//    val updatedList = appInfoList.value.toMutableList()
//    updatedList[index] = appInfo
//    appInfoList.value = updatedList
//}
//
//@OptIn(ExperimentalLayoutApi::class)
//@Composable
//fun Application(
//    appInfoList: MutableState<List<AppInfo>>,
//) {
//    var switcherState by remember { mutableStateOf(true) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(16.dp)
//            .verticalScroll(rememberScrollState())
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(16.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(
//                text = "Select Apps",
//                style = MaterialTheme.typography.labelLarge,
//                modifier = Modifier
//                    .wrapContentSize()
//            )
//            Switch(
//                checked = switcherState,
//                onCheckedChange = {
//                    switcherState = !switcherState
//                    setAllApplicationActiveStatus(appInfoList, switcherState)
//                }
//            )
//        }
//
//        FlowRow {
//            appInfoList.value.forEach { appInfo: AppInfo ->
//                if(appInfo.isActive) {
//                    AppItem(
//                        appInfo = appInfo,
//                        onAppItemClick = {
//                            appInfo.isActive = it
//                            setApplicationStatus(appInfoList, appInfo)
//                        }
//                    )
//                }
//            }
//        }
//        // CRUTCH/КОСТЫЛЬ
//        // Без этой хрени цвета итемок не меняется когда нажимаещь на них
//        FlowRow {
//            appInfoList.value.forEach { appInfo: AppInfo ->
//                if(!appInfo.isActive) {
//                    AppItem(
//                        appInfo = appInfo,
//                        onAppItemClick = {
//                            appInfo.isActive = it
//                            setApplicationStatus(appInfoList, appInfo)
//                        }
//                    )
//                }
//            }
//        }
//        // CRUTCH/КОСТЫЛЬ
//    }
//}
//
//@Composable
//fun AppItem(
//    appInfo: AppInfo,
//    onAppItemClick: (Boolean) -> Unit
//) {
//    val context = LocalContext.current
//
//    val appIconDrawable: Drawable? = try {
//        context.packageManager.getApplicationIcon(appInfo.packageName)
//    } catch (e: PackageManager.NameNotFoundException) {
//        ContextCompat.getDrawable(context, R.drawable.ic_android)
//    }
//    val isActive = remember{ mutableStateOf(appInfo.isActive) }
//
//    val backgroundColor =
//        if (isActive.value) MaterialTheme.colorScheme.primary
//        else MaterialTheme.colorScheme.inversePrimary
//    val textColor = if (isActive.value) Color.Black else Color.White
//
//
//    Card(
//        modifier = Modifier
//            .padding(end = 8.dp, bottom = 8.dp)
//            .clickable {
//                isActive.value = !isActive.value
//                onAppItemClick(!appInfo.isActive)
//            },
//        shape = RoundedCornerShape(32.dp),
//    ) {
//        Box(
//            modifier = Modifier
//                .wrapContentSize()
//                .background(backgroundColor)
//        ) {
//            Row(
//                modifier = Modifier
//                    .wrapContentSize()
//                    .padding(8.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//
//                Image(
//                    painter = rememberDrawablePainter(appIconDrawable),
//                    contentDescription = "App icon",
//                    modifier = Modifier
//                        .align(Alignment.CenterVertically)
//                        .size(24.dp)
//                        .clip(CircleShape),
//                )
//
//                Spacer(modifier = Modifier.width(8.dp))
//
//                Text(
//                    text = appInfo.appName,
//                    style = MaterialTheme.typography.bodyMedium,
//                    color = textColor
//                )
//            }
//        }
//    }
//}
