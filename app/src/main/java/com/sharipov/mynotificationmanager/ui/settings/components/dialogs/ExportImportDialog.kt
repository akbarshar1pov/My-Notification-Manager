package com.sharipov.mynotificationmanager.ui.settings.components.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.utils.exportDatabase
import com.sharipov.mynotificationmanager.utils.getFileNameFromUri
import com.sharipov.mynotificationmanager.utils.importDatabase
import com.sharipov.mynotificationmanager.utils.shareFile
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun exportImportDialog(context: Context, homeViewModel: HomeViewModel): Boolean {
    val openDialog = remember { mutableStateOf(true) }
    if (openDialog.value) {
        ModalBottomSheet(
            onDismissRequest = {
                openDialog.value = false
            },
        ) {
            Surface(
                modifier = Modifier.wrapContentSize(),
            ) {
                ExportImportModalBottomSheetContent(context, homeViewModel)
            }
        }
    }
    return openDialog.value
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportImportModalBottomSheetContent(context: Context, homeViewModel: HomeViewModel) {
    val pagerState = rememberPagerState(pageCount = { 2 }, initialPage = 0)
    var selectedTabIndex by remember { mutableIntStateOf(pagerState.currentPage) }

    Column(modifier = Modifier.wrapContentSize()) {
        SecondaryTabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.background(Color.Gray)
        ) {
            Tab(
                text = { Text(
                        text = stringResource(id = R.string.export),
                        color = MaterialTheme.colorScheme.onBackground)
                       },
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 }
            )
            Tab(
                text = { Text(
                        text = stringResource(id = R.string.import_),
                        color = MaterialTheme.colorScheme.onBackground)
                       },
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 }
            )
        }

        LaunchedEffect(selectedTabIndex) {
            pagerState.animateScrollToPage(selectedTabIndex)
        }

        LaunchedEffect(pagerState.currentPage) {
            selectedTabIndex = pagerState.currentPage
        }

        HorizontalPager(
            state = pagerState
        ) { page ->
            when (page) {
                0 -> ExportScreen(context, homeViewModel)
                1 -> ImportScreen(context, homeViewModel)
                else -> ImportScreen(context, homeViewModel)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImportScreen(context: Context, homeViewModel: HomeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var importStatus by remember{mutableStateOf(Pair(true, ""))}
    var importButtonStatus by remember{mutableStateOf(true)}
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp, bottom = 48.dp)
    ) {
        Text(
            text = stringResource(id = R.string.select_the_backup_file),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row {
            Text(
                text = stringResource(id = R.string.selected_file) + ": ",
                maxLines = 1
            )
            Text(
                text = selectedFileName,
                modifier = Modifier.basicMarquee(),
                color = MaterialTheme.colorScheme.inversePrimary
            )
        }
        if(importButtonStatus) {
            FileSelectionButton(selectedFileUri) { uri ->
                selectedFileUri = uri
                selectedFileName = getFileNameFromUri(context.contentResolver, uri) ?: ""
            }
            Text(
                text = stringResource(id = R.string.click_on_the_import_button),
                modifier = Modifier.padding(top = 16.dp)
            )
            Button(
                enabled = selectedFileUri != null,
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    coroutineScope.launch {
                        importStatus = importDatabase(context, homeViewModel, selectedFileUri)
                        importButtonStatus = !importStatus.first
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.import_), color = Color.White)
            }
        }
        if(importStatus.second != "") {
            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 5.dp
            )
            Row(modifier = Modifier
                .fillMaxWidth()
                .align(alignment = Alignment.End)
            ) {
                Text(text = stringResource(id = R.string.status) + " ")
                if(importStatus.first) {
                    Text(text = stringResource(id = R.string.success), color = Color.Green)
                    Toast.makeText(context,importStatus.second, Toast.LENGTH_SHORT).show()
                } else {
                    Text(text = stringResource(id = R.string.no_done), color = Color.Red)
                    Toast.makeText(context,importStatus.second, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@SuppressLint("IntentReset")
@Composable
fun ExportScreen(context: Context, homeViewModel: HomeViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var exportButtonStatus by remember{mutableStateOf(true)}
    var exportStatus by remember{mutableStateOf(Pair(true, ""))}

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {
        Text(text = stringResource(id = R.string.export_rules))
        if(exportButtonStatus) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 16.dp),
                onClick = {
                    coroutineScope.launch {
                        exportStatus = exportDatabase(context, homeViewModel)
                        exportButtonStatus = !exportStatus.first
                    }
                }
            ) {
                Text(text = stringResource(id = R.string.export), color = Color.White)
            }
        } else {
            HorizontalDivider(
                modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
                color = MaterialTheme.colorScheme.primary,
                thickness = 5.dp
            )
            Column {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .align(alignment = Alignment.End)
                ) {
                    Text(text = stringResource(id = R.string.status))
                    if(exportStatus.first) {
                        Text(text = stringResource(id = R.string.success), color = Color.Green)
                    } else {
                        Text(text = stringResource(id = R.string.no_done), color = Color.Red)
                    }
                }
                if(exportStatus.first) {
                    Column {
                        Row{
                            Text(text = stringResource(id = R.string.file_name))
                            Text(
                                text = "${exportStatus.second}_backup.json",
                                modifier = Modifier.basicMarquee(),
                                maxLines = 1
                            )
                        }
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 16.dp),
                            onClick = {
                                shareFile(context = context, fileName = "${exportStatus.second}_backup.json")
                            }
                        ) {
                            Text(text = stringResource(id = R.string.share), color = Color.White)
                        }
                    }

                }
            }
        }
        Spacer(modifier = Modifier.padding(12.dp))
    }
}

@Composable
fun FileSelectionButton(
    selectedFileUri: Uri?,
    onFileSelected: (Uri) -> Unit
) {
    val context = LocalContext.current
    val fileNotSelected = stringResource(id = R.string.file_not_selected)
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                onFileSelected(uri)
            } else {
                Toast.makeText(context, fileNotSelected, Toast.LENGTH_SHORT).show()
            }
        }

    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            launcher.launch("application/json")
        }
    ) {
        Text(
            if(selectedFileUri != null) stringResource(id = R.string.select_another_file)
            else stringResource(id = R.string.select_file),
            color = Color.White,
        )
    }
}