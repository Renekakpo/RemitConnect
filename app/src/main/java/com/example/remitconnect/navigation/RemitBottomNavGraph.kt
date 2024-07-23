package com.example.remitconnect.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.remitconnect.R
import com.example.remitconnect.ui.screen.HomeScreen
import com.example.remitconnect.ui.screen.MoneyTransferOptScreen
import com.example.remitconnect.ui.screen.bottomItem.CardsScreen
import com.example.remitconnect.ui.screen.bottomItem.SettingsScreen
import com.example.remitconnect.ui.screen.bottomItem.TontinesScreen
import com.example.remitconnect.viewModel.MainViewModel

object RemitBottomNavGraph : RemitNavDestination {
    override val route: String = "remit_bottom_nav_graph"
}

sealed class BottomNavScreen(val route: String, val label: String, val iconID: Int) {
    data object Home : BottomNavScreen("home", "Home", R.drawable.ic_home)
    data object Cards : BottomNavScreen("cards", "Cards", R.drawable.ic_credit_card)
    data object Tontines : BottomNavScreen("tontines", "Tontines", R.drawable.ic_coin)
    data object Settings : BottomNavScreen("settings", "Settings", R.drawable.ic_settings)
}

@Composable
fun RemitBottomNavGraph(navController: NavHostController, mainViewModel: MainViewModel) {
    val items = listOf(
        BottomNavScreen.Home,
        BottomNavScreen.Cards,
        BottomNavScreen.Tontines,
        BottomNavScreen.Settings
    )
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val currentRoute = remember { mutableStateOf(BottomNavScreen.Home.route) }

    Scaffold(
        bottomBar = {
            BottomNavigation(
                modifier = Modifier.height(screenHeight * 0.1f),
                contentColor = MaterialTheme.colorScheme.background,
                backgroundColor = MaterialTheme.colorScheme.background
            ) {
                items.forEach { screen ->
                    val selected = currentRoute.value == screen.route

                    NavigationBarItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = screen.iconID),
                                contentDescription = ""
                            )
                        },
                        label = { Text(screen.label) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            unselectedTextColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                            indicatorColor = Color.Transparent
                        ),
                        onClick = {
                            currentRoute.value = screen.route
                        }
                    )
                }
            }
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(MoneyTransferOptScreen.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when
                            // re-selecting the same item
                            launchSingleTop = true
                            // Restore state when re-selecting a previously selected item
                            restoreState = true
                        }
                    },
                    shape = CircleShape,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(60.dp)
                        .offset(y = 40.dp)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_paper_plane),
                        contentDescription = "Money transfer flow icon",
                        modifier = Modifier.size(25.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        // Content of the selected item
        when (currentRoute.value) {
            BottomNavScreen.Home.route -> {
                HomeScreen(modifier = modifier, navController = navController, mainViewModel = mainViewModel)
            }

            BottomNavScreen.Cards.route -> {
                CardsScreen(navController)
            }

            BottomNavScreen.Tontines.route -> {
                TontinesScreen(navController)
            }

            BottomNavScreen.Settings.route -> {
                SettingsScreen(navController)
            }
        }
    }
}
