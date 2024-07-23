package com.example.remitconnect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.remitconnect.ui.screen.MobileWalletOptScreen
import com.example.remitconnect.ui.screen.MoneyTransferOptScreen
import com.example.remitconnect.ui.screen.MoneyTransferredScreen
import com.example.remitconnect.ui.screen.RecipientListScreen
import com.example.remitconnect.ui.screen.TransactionSummaryScreen
import com.example.remitconnect.ui.screen.TransferDestOptScreen
import com.example.remitconnect.viewModel.MainViewModel

@Composable
fun RemitNavGraph(navController: NavHostController, mainViewModel: MainViewModel = hiltViewModel()) {
    NavHost(
        navController = navController,
        startDestination = RemitBottomNavGraph.route
    ) {
        composable(route = RemitBottomNavGraph.route) {
            RemitBottomNavGraph(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = MobileWalletOptScreen.route) {
            MobileWalletOptScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = MoneyTransferOptScreen.route) {
            MoneyTransferOptScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = TransactionSummaryScreen.route) {
            TransactionSummaryScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = RecipientListScreen.route) {
            RecipientListScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(route = MoneyTransferredScreen.route) {
            MoneyTransferredScreen(navController = navController, mainViewModel = mainViewModel)
        }
        composable(
            route = "${TransferDestOptScreen.route}/{selectedItem}",
            arguments = listOf(navArgument("selectedItem") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedItem = backStackEntry.arguments?.getString("selectedItem") ?: ""
            if (selectedItem.isNotEmpty()) {
                TransferDestOptScreen(
                    navController = navController,
                    transferOptTitle = selectedItem,
                    mainViewModel = mainViewModel
                )
            } else {
                // Navigate back
                navController.popBackStack()
            }
        }
    }
}