package com.example.remitconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.R
import com.example.remitconnect.navigation.RemitBottomNavGraph
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.CustomButton
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.viewModel.MainViewModel
import kotlinx.coroutines.launch

object MoneyTransferredScreen : RemitNavDestination {
    override val route: String = "money_transferred_screen"
}

@Composable
fun MoneyTransferredScreen(navController: NavHostController, mainViewModel: MainViewModel) {
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(id = R.drawable.ic_success_transfer),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.transaction_success_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            onClick = {
                scope
                    .launch {
                        mainViewModel.updateCurrentTransaction(transaction = null) // Clear transaction
                    }
                    .invokeOnCompletion {
                        navController.navigate(RemitBottomNavGraph.route) {
                            popUpTo(RemitBottomNavGraph.route) { inclusive = true }
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
            ),
            shape = MaterialTheme.shapes.large,
            enabled = true,
            text = stringResource(R.string.got_it_button_text),
            textColor = MaterialTheme.colorScheme.onPrimary
        )

    }
}

@Preview(showBackground = true)
@Composable
fun MoneyTransferredScreenPreview() {
    RemitConnectTheme {
        MoneyTransferredScreen(
            navController = rememberNavController(),
            mainViewModel = hiltViewModel()
        )
    }
}