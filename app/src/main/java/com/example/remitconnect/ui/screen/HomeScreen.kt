package com.example.remitconnect.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.R
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.enums.ProcessState
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.ActionCard
import com.example.remitconnect.ui.common.CustomIcon
import com.example.remitconnect.ui.common.CustomLoader
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.utils.Utils.doubleToCurrency
import com.example.remitconnect.viewModel.MainViewModel

object HomeScreen : RemitNavDestination {
    override val route: String = "home_screen"
}

@Composable
fun HomeScreen(navController: NavHostController, mainViewModel: MainViewModel = hiltViewModel()) {
    val localConfig = LocalConfiguration.current
    val screenWidth = localConfig.screenWidthDp

    val defaultCurrencyCode = mainViewModel.defaultCurrencyCode
    val processState by mainViewModel.processState.collectAsState()
    val currentBalance by mainViewModel.currentBalance.collectAsState()
    val last5Transactions by mainViewModel.localTransactions.collectAsState()

    // Fetch data in a LaunchedEffect to avoid fetching on every recomposition
    LaunchedEffect(Unit) {
        mainViewModel.fetchLocalLast5Transactions()
        mainViewModel.calculateRemainingBalance()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Hey, John Doe",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            CustomIcon(
                imageResId = R.drawable.ic_bell,
                contentDescription = stringResource(R.string.notification_icon_desc),
                size = 34.dp,
                shape = RoundedCornerShape(8.dp),
                backgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BalanceCard(
            height = screenWidth.dp * 0.5f,
            currentBalance = doubleToCurrency(currentBalance, defaultCurrencyCode)
        )

        Spacer(modifier = Modifier.height(8.dp))

        ActionCards(screenWidth = screenWidth.dp, screenHeight = screenWidth.dp)

        Spacer(modifier = Modifier.height(8.dp))

        when (processState) {
            ProcessState.Loading -> {
                CustomLoader(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            ProcessState.Done -> {
                TransactionSection(
                    transactions = last5Transactions,
                    onEmptyTransactionsMessage = null
                )
            }

            else -> {
                TransactionSection(
                    transactions = last5Transactions,
                    onEmptyTransactionsMessage = (processState as ProcessState.Error).message
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun BalanceCard(height: Dp, currentBalance: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .padding(10.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    )
                ),
                shape = MaterialTheme.shapes.large
            )
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxSize(),
        ) {
            Column(
                modifier = Modifier
                    .padding(4.dp),
            ) {
                Text(
                    text = stringResource(R.string.your_balance_text),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = currentBalance,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(R.string.default_currency_code),
                    color = Color.White,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            CustomIcon(
                imageResId = R.drawable.ic_money_card,
                contentDescription = stringResource(R.string.credit_card_icon_desc),
                size = 50.dp,
                shape = RoundedCornerShape(16.dp),
                backgroundColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.1f)
            )
        }
    }
}

@Composable
fun ActionCards(screenWidth: Dp, screenHeight: Dp) {
    val actionCardWidth = screenWidth * 0.41f
    val actionCardHeight = screenHeight * 0.25f

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionCard(
                width = actionCardWidth,
                height = actionCardHeight,
                imageResId = R.drawable.ic_moneys,
                imageResDescription = stringResource(R.string.top_up_balance_icon_desc),
                cardTitle = stringResource(R.string.top_up_balance_text),
                onClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            ActionCard(
                width = actionCardWidth,
                height = actionCardHeight,
                imageResId = R.drawable.ic_moneys,
                imageResDescription = stringResource(R.string.withdraw_money_icon_desc),
                cardTitle = stringResource(R.string.withdraw_money_text),
                onClick = {}
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionCard(
                width = actionCardWidth,
                height = actionCardHeight,
                imageResId = R.drawable.ic_moneys,
                imageResDescription = stringResource(R.string.get_iban_icon_desc),
                cardTitle = stringResource(R.string.get_iban_text),
                onClick = {}
            )

            Spacer(modifier = Modifier.weight(1f))

            ActionCard(
                width = actionCardWidth,
                height = actionCardHeight,
                imageResId = R.drawable.ic_moneys,
                imageResDescription = stringResource(R.string.view_analytics_icon_desc),
                cardTitle = stringResource(R.string.view_analytics_text),
                onClick = {}
            )
        }

    }
}

@Composable
fun TransactionSection(transactions: List<Transaction>, onEmptyTransactionsMessage: String?) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.transactions_section_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(14.dp))

        if (transactions.isEmpty()) {
            Text(
                text = "$onEmptyTransactionsMessage",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
            )
        } else {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                color = Color.White
            ) {
                Column {
                    transactions.forEach { item ->
                        TransactionItem(transaction = item)
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CustomIcon(
            imageResId = R.drawable.ic_baseline_arrow_outward_24,
            contentDescription = stringResource(R.string.transaction_item_arrow_icon_desc),
            size = 34.dp,
            shape = RoundedCornerShape(8.dp),
            backgroundColor = MaterialTheme.colorScheme.surface,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Column(verticalArrangement = Arrangement.SpaceBetween) {
            Text(
                text = stringResource(R.string.send_to_text),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
            )

            Text(
                text = "${transaction.recipient?.name}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = "€ ${
                doubleToCurrency(
                    amount = transaction.totalSpent,
                    currencyCode = transaction.currencyCode
                )
            }",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    RemitConnectTheme {
        HomeScreen(navController = rememberNavController())
    }
}