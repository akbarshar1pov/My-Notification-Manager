package com.sharipov.mynotificationmanager.ui.theme

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.sharipov.mynotificationmanager.data.ThemePreferences

private val darkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val lightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

@Composable
fun getTheme(): Theme {
    val context = LocalContext.current
    val selectedTheme = ThemePreferences.getTheme(context)
    return selectedTheme.let { AppThemes.getTheme(it) }
}

@Composable
fun MyNotificationManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val theme = getTheme()
    val isSystemTheme = theme.name == "System"

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isSystemTheme) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) theme.darkColorScheme else theme.lightColorScheme
            }
        }

        darkTheme -> theme.darkColorScheme
        else -> theme.lightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = WindowCompat.getInsetsController((view.context as Activity).window, view)
            window.isAppearanceLightStatusBars = !darkTheme
            window.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topBarColorScheme(): TopAppBarColors {
    return TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primary,
        titleContentColor = contentColorFor(MaterialTheme.colorScheme.primary),
        actionIconContentColor = contentColorFor(MaterialTheme.colorScheme.primary),
        navigationIconContentColor = contentColorFor(MaterialTheme.colorScheme.primary),
    )
}

data class Theme(
    val name: String,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
)

object AppThemes {
    private val oceanBreeze =
        Theme("Ocean Breeze", oceanBreezeLightColorScheme, oceanBreezeDarkColorScheme)
    private val forestHarmony =
        Theme("Forest Harmony", forestHarmonyLightColorScheme, forestHarmonyDarkColorScheme)
    private val sunsetSerenity =
        Theme("Sunset Serenity", sunsetSerenityLightColorScheme, sunsetSerenityDarkColorScheme)
    private val fireRedBlaze =
        Theme("Fire Red Blaze", fireRedBlazeLightColorScheme, fireRedBlazeDarkColorScheme)
    private val sunnyLemonade =
        Theme("Sunny Lemonade", sunnyLemonadeLightColorScheme, sunnyLemonadeDarkColorScheme)

    private val themeList = listOf(oceanBreeze, forestHarmony, sunsetSerenity, fireRedBlaze, sunnyLemonade)

    fun getThemesList(): List<Theme> {
        return themeList
    }

    fun getTheme(name: String): Theme {
        return themeList.find { it.name == name } ?: Theme(
            "System",
            lightColorScheme,
            darkColorScheme
        )
    }
}

@Composable
fun getThemeMode(content: Context): Boolean {
    return when (ThemePreferences.getThemeMode(content)) {
        "dark_theme" -> true
        "light_theme" -> false
        "system_theme" -> isSystemInDarkTheme()
        else -> isSystemInDarkTheme()
    }
}

////////////// "Ocean Breeze"
private val oceanBreezeDarkColorScheme = darkColorScheme(
    primary = Color(0xFF006994),
    inversePrimary = Color(0xFF66A6BA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF33445A),
    onPrimaryContainer = Color(0xFFFFFFFF),

    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF006994), // fon aktivnoy iconci
    onSecondaryContainer = Color(0xFFFFFFFF),  // цвет активной иконки ботмБар

    background = Color(0xFF042E40),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF042E40),   // фон ботом шилта
    onSurface = Color(0xFFFFFFFF),  // цвет текста на ботм шилте
    surfaceVariant = Color(0xFF04384E),  // цвет карточки
    onSurfaceVariant = Color(0xFFFFFFFF),  // цыет текста на карточке

    outline = Color(0xFF006994),  // рамка радиобатона
    outlineVariant = Color(0xFFFFFFFF),  // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

private val oceanBreezeLightColorScheme = lightColorScheme(
    primary = Color(0xFF006994),
    inversePrimary = Color(0xFF66A6BA),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFC1E1E3),
    onPrimaryContainer = Color(0xFF006994),

    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFC1E1E3),
    onSecondaryContainer = Color(0xFF006994),  // цвет активной иконки ботмБар

    background = Color(0xFFF2F1EF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFEEEEEE),  // фон ботом шилта
    onSurface = Color(0xFF000000),  // цвет текста на ботм шилте
    surfaceVariant = Color(0xFFFFFFFF),  // цвет карточки
    onSurfaceVariant = Color(0xFF006994),  // цыет текста на карточке

    outline = Color(0xFF006994), // рамка радиобатона
    outlineVariant = Color(0xFFFFFFFF), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

//name = "Forest Harmony",
private val forestHarmonyLightColorScheme = lightColorScheme(
    primary = Color(0xFF777E5C),
    inversePrimary = Color(0xFFAAB68B),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFADB49C),
    onPrimaryContainer = Color(0xFF777E5C),

    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFB8BDAF),
    onSecondaryContainer = Color(0xFF777E5C),  // цвет активной иконки ботмБар

    background = Color(0xFFF2F1EF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFEEEEEE),  // фон ботом шилта
    onSurface = Color(0xFF000000),  // цвет текста на ботм шилте
    surfaceVariant = Color(0xFFFFFFFF),  // цвет карточки
    onSurfaceVariant = Color(0xFF777E5C),  // цыет текста на карточке

    outline = Color(0xFF777E5C), // рамка радиобатона
    outlineVariant = Color(0xFFFFFFFF), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

private val forestHarmonyDarkColorScheme = darkColorScheme(
    primary = Color(0xFF777E5C),
    inversePrimary = Color(0xFF585F3E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF40462C),
    onPrimaryContainer = Color(0xFFFFFFFF),

    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF90947F), // Цвет фона активной кнопки
    onSecondaryContainer = Color(0xFFFFFFFF),  // цвет активной иконки ботмБар

    background = Color(0xFF585F3E),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF585F3E), // фон ботом шилта
    onSurface = Color(0xFFFFFFFF), // цвет текста на ботм шилте
    surfaceVariant = Color(0xFF60664B),  // цвет карточки
    onSurfaceVariant = Color(0xFFFFFFFF),  // цыет текста на карточке

    outline = Color(0xFF777E5C), // рамка радиобатона
    outlineVariant = Color(0xFFCCCCCC), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
 )

///"Fire Red Blaze",
private val fireRedBlazeLightColorScheme = lightColorScheme(
    primary = Color(0xFFD32F2F),
    inversePrimary = Color(0xFFD14646),
    onPrimary = Color(0xFFFFFFFF),
    onPrimaryContainer = Color(0xFFD32F2F),

    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFE9AFAF), // Цвет фона активной кнопки
    onSecondaryContainer = Color(0xFFD32F2F),  // цвет активной иконки ботмБар

    background = Color(0xFFF2F1EF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFDDDDDD), // фон ботом шилта
    onSurface = Color(0xFF000000), // цвет текста на ботм шилте
    surfaceVariant = Color(0xFFFFFFFF),  // цвет карточки
    onSurfaceVariant = Color(0xFFD32F2F),  // цыет текста на карточке

    outline = Color(0xFFD32F2F), // рамка радиобатона
    outlineVariant = Color(0xFFCCCCCC), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

private val fireRedBlazeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFD32F2F),
    inversePrimary = Color(0xFFE45555),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD32F2F),
    onPrimaryContainer = Color(0xFFFFFFFF),

    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFEB5252), // Color for active button background
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for active icon in bottom bar

    background = Color(0xFF802222),  // Background color for dark theme
    onBackground = Color(0xFFFFFFFF), // Text color on the dark background

    surface = Color(0xFF802222), // Background color for bottom sheet
    onSurface = Color(0xFFFFFFFF), // Text color on the bottom sheet
    surfaceVariant = Color(0xFFA72727), // Color for card background
    onSurfaceVariant = Color(0xFFFFFFFF), // Text color on the card

    outline = Color(0xFFD32F2F), // Border color for radio button
    outlineVariant = Color(0xFFFFFFFF), // Color inside radio button
    error = Color(0xFFFF0000),
)



///"Sunny Lemonade"
private val sunnyLemonadeLightColorScheme = lightColorScheme(
    primary = Color(0xFFB8A01F),
    inversePrimary = Color(0xFFCDDC39),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD3D35C),
    onPrimaryContainer = Color(0xFFB8A01F),

    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFC7C19F), // Цвет фона активной кнопки
    onSecondaryContainer = Color(0xFFB8A01F),  // цвет активной иконки ботмБар

    background = Color(0xFFF2F1EF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFDDDDDD), // фон ботом шилта
    onSurface = Color(0xFF000000), // цвет текста на ботм шилте
    surfaceVariant = Color(0xFFFFFFFF),  // цвет карточки
    onSurfaceVariant = Color(0xFFB8A01F),  // цыет текста на карточке

    outline = Color(0xFFB8A01F), // рамка радиобатона
    outlineVariant = Color(0xFFCCCCCC), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

private val sunnyLemonadeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFB8A01F),
    inversePrimary = Color(0xFF919102),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF5E5319),
    onPrimaryContainer = Color(0xFFFFFFFF),

    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF968D5F), // Color for active button background
    onSecondaryContainer = Color(0xFFFFFFFF), // Color for active icon in bottom bar

    background = Color(0xFF756401), // Background color for dark theme
    onBackground = Color(0xFFFFFFFF), // Text color on the dark background


    surface = Color(0xFFA28800), // Background color for bottom sheet
    onSurface = Color(0xFFFFFFFF), // Text color on the bottom sheet
    surfaceVariant = Color(0xFFA08E2B), // Color for card background
    onSurfaceVariant = Color(0xFFFFFFFF), // Text color on the card

    outline = Color(0xFFB8A01F), // Border color for radio button
    outlineVariant = Color(0xFFFFFFFF), // Color inside radio button
    error = Color(0xFFFF0000),
)


///Sunset Serenity
private val sunsetSerenityLightColorScheme = lightColorScheme(
    primary = Color(0xFFD66D41),
    onPrimary = Color(0xFFFFFFFF),
    inversePrimary = Color(0xFFA96B57),
    primaryContainer = Color(0xFFA8897F),
    onPrimaryContainer = Color(0xFFD66D41),

    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFFD5C8C3), // Цвет фона активной кнопки
    onSecondaryContainer = Color(0xFFD66D41),  // цвет активной иконки ботмБар

    background = Color(0xFFF2F1EF),
    onBackground = Color(0xFF000000),

    surface = Color(0xFFDDDDDD), // фон ботом шилта
    onSurface = Color(0xFF000000), // цвет текста на ботм шилте
    surfaceVariant = Color(0xFFFFFFFF),  // цвет карточки
    onSurfaceVariant = Color(0xFFD66D41),  // цыет текста на карточке

    outline = Color(0xFFD66D41), // рамка радиобатона
    outlineVariant = Color(0xFFCCCCCC), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)

private val sunsetSerenityDarkColorScheme = darkColorScheme(
    primary = Color(0xFFA85532),
    inversePrimary = Color(0xFFA96B57),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFF80442A),
    onPrimaryContainer = Color(0xFFFFFFFF),

    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF7E4026), // Цвет фона активной кнопки
    onSecondaryContainer = Color(0xFFFFFFFF),  // цвет активной иконки ботмБар

    background = Color(0xFF7E4026),
    onBackground = Color(0xFFFFFFFF),

    surface = Color(0xFF69341E), // фон ботом шилта
    onSurface = Color(0xFFFFFFFF), // цвет текста на ботм шилте
    surfaceVariant = Color(0xFF7E4026),  // цвет карточки
    onSurfaceVariant = Color(0xFFFFFFFF),  // цыет текста на карточке

    outline = Color(0xFFA85532), // рамка радиобатона
    outlineVariant = Color(0xFFCCCCCC), // цвет внутри радиобатона
    error = Color(0xFFFF0000),
)
