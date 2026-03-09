package com.sharipov.mynotificationmanager.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.data.ThemePreferences
import com.sharipov.mynotificationmanager.navigation.Screens
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars
import com.sharipov.mynotificationmanager.utils.updateApplicationList
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel
) {
    val context = LocalContext.current
    TransparentSystemBars()

    Splash(context)

    LaunchedEffect(Unit) {
        updateApplicationList(context, settingsViewModel)
        delay(1500)
        val autoDeleteTimeout = settingsViewModel.getAppSettings()?.autoDeleteTimeoutLong ?: 0L
        if (autoDeleteTimeout != 0L) {
            val currentTime = System.currentTimeMillis()
            val deleteThreshold = currentTime - autoDeleteTimeout
            homeViewModel.deleteExpiredNotification(deleteThreshold)
        }
        navController.navigate(Screens.Permissions.route) {
            popUpTo(Screens.Splash.route) { inclusive = true }
            launchSingleTop = true
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Splash(context: Context) {
    val useDarkAnimation = if(ThemePreferences.getThemeMode(context) == "light_theme") true
    else if (ThemePreferences.getThemeMode(context) == "dark_theme") false
    else !isSystemInDarkTheme()

    val animation = if(!useDarkAnimation)LottieCompositionSpec.RawRes(resId = R.raw.animation_dark)
    else LottieCompositionSpec.RawRes(resId = R.raw.animation_light)

    val composition = rememberLottieComposition(
        spec = animation
    ).value

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val sizeModifier = Modifier.size(300.dp)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LottieAnimation(
                modifier = sizeModifier,
                composition = composition,
                isPlaying = true,
                restartOnPlay = true
            )

            Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
