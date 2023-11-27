package com.learn.worlds

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.learn.worlds.navigation.MyNavHost
import com.learn.worlds.navigation.Screen
import com.learn.worlds.ui.common.BottomBar
import com.learn.worlds.utils.stringRes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


object NavigationMediator {

    private var closeAction: () -> Unit = {}
    private var openAction: () -> Unit = {}
    private lateinit var navController: NavHostController

    fun init(onCloseDrawer: () -> Unit, onOpenDrawer: () -> Unit, navCon: NavHostController) {
        navController = navCon
        closeAction = onCloseDrawer
        openAction = onOpenDrawer
    }

    fun popBackStack() {
        navController.popBackStack()
    }

    fun close() {
        closeAction.invoke()
    }

    fun open() {
        openAction.invoke()
    }
}

data class DrawerMenuItem(
    val text: String,
    val imageVector: ImageVector,
    val onClickAction: () -> Unit = {}
)

@Composable
fun LearnWordsApp(
    navController: NavHostController,
    allScreens: List<Screen>,
    viewModel: ActivityViewModel = hiltViewModel(),
    context: Context = LocalContext.current,
) {
    val mainBottomsScreens = allScreens.filter { it.bottomItem != null }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val scope = rememberCoroutineScope()
    val authState = viewModel.authState.collectAsStateWithLifecycle().value
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val shouldShowBottomBar =
        navBackStackEntry?.destination?.route in mainBottomsScreens.map { it.route }
    val items = mutableListOf(
        DrawerMenuItem(
            onClickAction = { navController.navigate(Screen.PreferencesScreen.route) },
            text = stringRes(R.string.settings),
            imageVector = Icons.Filled.Settings
        )
    )

    if (authState) {
        items.addAll(
            listOf(
                DrawerMenuItem(
                    text = stringRes(R.string.profile),
                    imageVector = Icons.Filled.Home,
                    onClickAction = {
                        Toast.makeText(
                            context,
                            R.string.toast_will_be_soon,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                ),
                DrawerMenuItem(
                    text = stringRes(R.string.synck),
                    imageVector = Icons.Default.Sync,
                    onClickAction = {
                        navController.navigate(Screen.SynchronizationScreen.route)
                    }
                )
            )
        )

    }


    var selectedItem by remember { mutableStateOf(items[0]) }

    NavigationMediator.init(
        navCon = navController,
        onCloseDrawer = {
            scope.launch { drawerState.close() }
        }, onOpenDrawer = {
            scope.launch { drawerState.open() }
        })

    ModalNavigationDrawer(drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier
                    .requiredWidth(250.dp)
                    .fillMaxHeight()
            ) {
                Spacer(Modifier.height(12.dp))
                items.forEach { item ->
                    NavigationDrawerItem(
                        icon = { Icon(item.imageVector, contentDescription = null) },
                        label = { Text(item.text) },
                        selected = item == selectedItem,
                        onClick = {
                            NavigationMediator.close()
                            item.onClickAction()
                            selectedItem = item
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                Spacer(Modifier.weight(1f))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(NavigationDrawerItemDefaults.ItemPadding)
                        .padding(vertical = 16.dp)
                        .clickable {
                            scope.launch {
                                NavigationMediator.close()
                                delay(200)
                                if (authState) {
                                    viewModel.logout()
                                } else {
                                    navController.navigate(Screen.AuthScreen.route)
                                }
                            }
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (authState) stringResource(R.string.logout) else stringResource(R.string.login),
                        style = TextStyle(textDecoration = TextDecoration.Underline),
                        maxLines = 1,
                    )

                }


            }
        }) {
        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant) {
                        val currentDestination = navBackStackEntry?.destination
                        BottomBar(
                            screens = mainBottomsScreens,
                            currentDestination = currentDestination,
                            navController = navController
                        )
                    }
                }
            }
        ) { innerPadding ->
            MyNavHost(navController, modifier = Modifier.padding(innerPadding))
        }
    }


}


