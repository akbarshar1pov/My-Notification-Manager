package com.sharipov.mynotificationmanager.ui.splashscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sharipov.mynotificationmanager.navigation.Screens
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel
import com.sharipov.mynotificationmanager.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import com.sharipov.mynotificationmanager.utils.TransparentSystemBars
import com.sharipov.mynotificationmanager.utils.UpdateApplicationList
import com.sharipov.mynotificationmanager.R

@Composable
fun SplashScreen(
    navController: NavController,
    homeViewModel: HomeViewModel,
    settingsViewModel: SettingsViewModel
) {

    var startAnimate by remember {
        mutableStateOf(false)
    }

    val alphaAnimation = animateFloatAsState(
        targetValue = if (startAnimate) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    UpdateApplicationList(settingsViewModel = settingsViewModel)
    TransparentSystemBars()

    LaunchedEffect(key1 = true) {
        startAnimate = true
        delay(1000)

        var autoDeleteTimeout = settingsViewModel.getAppSettings()?.autoDeleteTimeoutLong ?: 0L

        if (autoDeleteTimeout != 0L) {
            val currentTime = System.currentTimeMillis()
            autoDeleteTimeout = currentTime - autoDeleteTimeout
            homeViewModel.deleteExpiredNotification(autoDeleteTimeout)
        }


        navController.navigate(Screens.Applications.route)
    }

    Splash(alpha = alphaAnimation.value)
}

@Composable
fun Splash(alpha: Float) {
    val appIcon = painterResource(R.drawable.ic_app)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
//        Icon(
//            modifier = Modifier
//                .size(120.dp)
//                .alpha(alpha),
//            imageVector = appIcon,
//            contentDescription = "",
//            tint = MaterialTheme.colorScheme.primary
//        )
        Image(
            painter = appIcon,
            contentDescription = "App icon",
            modifier = Modifier.size(120.dp).alpha(alpha),
        )
    }
}