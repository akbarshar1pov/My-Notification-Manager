package com.sharipov.mynotificationmanager.ui.settings


import android.annotation.SuppressLint
import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.data.PreferencesManager
import com.sharipov.mynotificationmanager.data.ThemePreferences
import com.sharipov.mynotificationmanager.navigation.Screens
import com.sharipov.mynotificationmanager.ui.bottombarcomponent.BottomBar
import com.sharipov.mynotificationmanager.ui.settings.components.ClickableListItem
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.LanguageDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.aboutUsDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.autoRemoveDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.exportImportDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.feedbackDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.privatePolicyDialog
import com.sharipov.mynotificationmanager.ui.settings.components.dialogs.selectThemeDialog
import com.sharipov.mynotificationmanager.ui.theme.topBarColorScheme
import com.sharipov.mynotificationmanager.utils.Constants
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars
import com.sharipov.mynotificationmanager.utils.setLocaleBasedOnUserPreferences
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "CoroutineCreationDuringComposition")
@Composable
fun SettingsScreen(
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel,
    navController: NavController,
) {
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentNavBackStackEntry?.destination?.route ?: Constants.Screens.SETTINGS_SCREEN
    val openAutoRemoveDialog = remember { mutableStateOf(false) }
    val openExportImportDialog = remember { mutableStateOf(false) }
    val openPrivatePolicyDialog = remember { mutableStateOf(false) }
    val openFeedbackDialog = remember { mutableStateOf(false) }
    val openAboutUsDialog = remember { mutableStateOf(false) }
    val openLanguageDialog = remember { mutableStateOf(false) }
    val openThemeDialog = remember { mutableStateOf(false) }
    val showAutoDeleteDialog = remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("Never") }

    TransparentSystemBars()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        stringResource(id = R.string.settings),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                colors = topBarColorScheme()
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
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            userScrollEnabled = true
        ) {
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.automatic_deletion_of_notifications),
                    icon = painterResource(id = R.drawable.ic_auto_delete),
                    onClick = {
                        openAutoRemoveDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.exporting_and_importing_notifications),
                    icon = painterResource(id = R.drawable.ic_move_to_inbox),
                    onClick = {
                        openExportImportDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.select_theme),
                    icon = painterResource(id = R.drawable.ic_dark_mode),
                    onClick = {
                        openThemeDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.select_language),
                    icon = painterResource(id = R.drawable.ic_language),
                    onClick = {
                        openLanguageDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.private_policy),
                    icon = painterResource(id = R.drawable.ic_policy),
                    onClick = {
                        openPrivatePolicyDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.feedback),
                    icon = painterResource(id = R.drawable.ic_comment),
                    onClick = {
                        openFeedbackDialog.value = true
                    }
                )
            }
            item {
                ClickableListItem(
                    text = stringResource(id = R.string.about_us),
                    icon = painterResource(id = R.drawable.ic_about),
                    onClick = {
                        openAboutUsDialog.value = true
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        val context = LocalContext.current
        when {
            openPrivatePolicyDialog.value -> openPrivatePolicyDialog.value = privatePolicyDialog()
            openFeedbackDialog.value -> openFeedbackDialog.value = feedbackDialog()
            openAboutUsDialog.value -> openAboutUsDialog.value = aboutUsDialog()
            openAutoRemoveDialog.value -> openAutoRemoveDialog.value = autoRemoveDialog(
                settingsViewModel = settingsViewModel,
                onDismiss = {
                    showAutoDeleteDialog.value = false
                },
                onTimeSelected = { selectedTime = it }
            )

            openLanguageDialog.value -> LanguageDialog(
                onLanguageSelected = { selectedLanguage ->
                    PreferencesManager.saveSelectedLanguage(context, selectedLanguage)
                    setLocaleBasedOnUserPreferences(context)
                    if (context is Activity) {
                        context.recreate()
                    }
                },
                onDismiss = {
                    openLanguageDialog.value = false
                }
            )

            openThemeDialog.value -> {
                openThemeDialog.value = selectThemeDialog(
                    onThemeModeSelected = { mode ->
                        ThemePreferences.updateThemeMode(context, mode)
                    },
                    onThemeStyleSelected = { theme ->
                        ThemePreferences.updateSelectedTheme(context, theme)
                        if (context is Activity) {
                            context.recreate()
                        }
                    },
                    onDismiss = {
                        openThemeDialog.value = false
                    }
                )
            }

            openExportImportDialog.value -> {
                openExportImportDialog.value = exportImportDialog(context, homeViewModel)
            }
        }
    }
}
