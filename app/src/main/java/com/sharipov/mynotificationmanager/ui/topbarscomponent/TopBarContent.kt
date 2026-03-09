package com.sharipov.mynotificationmanager.ui.topbarscomponent

import android.graphics.drawable.Drawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.sharipov.mynotificationmanager.R
import com.sharipov.mynotificationmanager.ui.theme.topBarColorScheme
import com.sharipov.mynotificationmanager.viewmodel.HomeViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTopBarContent(
    title: String,
    onSearchClick: () -> Unit,
    searchVisible: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        actions = {
            IconButton(onClick = { onSearchClick() }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Localized description"
                )
            }
        },
        colors = topBarColorScheme()
    )
    SearchOutlineTextFiled(searchVisible, searchText, onSearchTextChange, onSearchClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarContent(
    title: String,
    icon: ImageVector,
    appIcon: Drawable?,
    onNavigationClick: () -> Unit,
) {
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (appIcon != null) {
                    Image(
                        painter = rememberDrawablePainter(appIcon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                }
                Text(
                    title,
                    maxLines = 1,
                    modifier = Modifier.padding(start = 8.dp),
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { onNavigationClick() }) {
                Icon(
                    imageVector = icon,
                    contentDescription = "icon"
                )
            }
        },
        colors = topBarColorScheme()
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatTopBarContent(
    group: String,
    userName: String,
    packageName: String,
    searchVisible: Boolean,
    searchText: String,
    navController: NavController,
    homeViewModel: HomeViewModel,
    onSearchVisibleChange: (Boolean) -> Unit,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        title = {
            Text(
                text = if (group == "not_group") userName else group,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.basicMarquee()
            )
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    navController.navigateUp()
                }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow back"
                )
            }
        },
        actions = {
            IconButton(
                onClick = {
                    expanded = true
                }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "More vert"
                )
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .offset {
                        IntOffset(0, 0)
                    }
            ) {
                DropdownMenuItem(
                    text = {
                        Row {
                            Icon(Icons.Default.Search, "Search in chat")
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.search))
                        }
                    },
                    onClick = {
                        onSearchVisibleChange(!searchVisible)
                        expanded = false
                    },
                )

                HorizontalDivider()

                DropdownMenuItem(
                    text = {
                        Row {
                            Icon(Icons.Default.Delete, "Delete chat")
                            Spacer(Modifier.width(8.dp))
                            Text(stringResource(id = R.string.delete_chat))
                        }
                    },
                    onClick = {
                        homeViewModel.deleteNotificationsForUser(group, userName, packageName)
                        navController.navigateUp()
                        expanded = false
                    }
                )
            }
        },
        colors = topBarColorScheme()
    )
    SearchOutlineTextFiled(searchVisible, searchText, onSearchTextChange, onSearchClick)
}

@Composable
fun SearchOutlineTextFiled(
    visibility: Boolean,
    text: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit,
) {

    val focusRequester = remember { FocusRequester() }

    if (visibility) {
        LaunchedEffect(true) {
            focusRequester.requestFocus()
        }

        OutlinedTextField(
            value = text,
            onValueChange = { onSearchTextChange(it) },
            label = { Text(stringResource(id = R.string.search)) },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester = focusRequester)
                .padding(top = 64.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    onSearchClick()
                }
            ),
            singleLine = true
        )
    }
}
