package com.example.remitconnect.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.R
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.CustomButton
import com.example.remitconnect.ui.common.CustomIcon
import com.example.remitconnect.ui.common.DashedLine
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.utils.Utils.calculateTransaction
import com.example.remitconnect.utils.Utils.displayToast
import com.example.remitconnect.utils.Utils.doubleToCurrency
import com.example.remitconnect.utils.Utils.formatAsPercentage
import com.example.remitconnect.viewModel.MainViewModel
import kotlinx.coroutines.launch

object TransactionSummaryScreen : RemitNavDestination {
    override val route: String = "transaction_summary_screen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionSummaryScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }

    val currentTransaction by mainViewModel.currentTransaction.collectAsState()
    val currentBalance by mainViewModel.currentBalance.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        CustomIcon(
            imageResId = R.drawable.ic_baseline_arrow_back_24,
            contentDescription = stringResource(id = R.string.back_arrow_icon_desc),
            size = 34.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f),
            onClicked = {
                // Navigate back to previous screen without adding to the stack
                navController.popBackStack()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.transaction_summary_screen_title),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(24.dp))

        AmountToSendSection(
            currentBalance = currentBalance,
            currentCurrencyCode = "${currentTransaction?.currencyCode?.uppercase()}",
            updateTransaction = { amount ->
                scope
                    .launch {
                        val updatedTransaction = currentTransaction?.let {
                            calculateTransaction(
                                amountToSend = amount.toDouble(),
                                currentTransaction = it
                            )
                        }
                        updatedTransaction?.let { mainViewModel.updateCurrentTransaction(transaction = it) }
                    }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        RemitInfoSection()

        Spacer(modifier = Modifier.height(24.dp))

        FeesBreakdownSection(transaction = currentTransaction)

        Spacer(modifier = Modifier.weight(1f))

        CustomButton(
            onClick = {
                if ((currentTransaction?.totalSpent ?: 0.0) < currentBalance) {
                    showBottomSheet = true
                } else {
                    displayToast(message = context.getString(R.string.insufficient_balance_text))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.small,
            enabled = (currentTransaction?.amount?.toDouble() ?: 0.0) > 0.0,
            text = stringResource(R.string.send_button_text),
            textColor = MaterialTheme.colorScheme.background
        )
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Content of the bottom sheet
            ConfirmationBottomSheetContent(
                transaction = currentTransaction,
                onConfirmClick = {
                    scope
                        .launch {
                            currentTransaction?.let { mainViewModel.insertCurrentTransaction(it) }
                            sheetState.hide()
                        }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                            navController.navigate(MoneyTransferredScreen.route)
                        }
                }
            )
        }
    }
}

@Composable
fun ConfirmationBottomSheetContent(transaction: Transaction?, onConfirmClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.White)
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.confirm_transfer_sheet_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.you_re_sending_text),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "${
                doubleToCurrency(
                    amount = transaction?.totalSpent ?: 0.0,
                    currencyCode = "${transaction?.recipient?.currencyCode?.uppercase()}"
                )
            }  ${transaction?.recipient?.currencyCode?.uppercase()}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.to_text),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "${transaction?.recipient?.name}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.via_text),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "${if (!transaction?.recipient?.mobileWallet.isNullOrEmpty()) transaction?.recipient?.mobileWallet else transaction?.selectedWallet} ${transaction?.recipient?.phoneNumber ?: ""}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(30.dp))

        CustomButton(
            onClick = onConfirmClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                disabledContainerColor = MaterialTheme.colorScheme.primaryContainer,
                disabledContentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.medium,
            enabled = true,
            text = stringResource(R.string.confirm_button_text),
            textColor = MaterialTheme.colorScheme.background
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmountToSendSection(
    currentBalance: Double,
    currentCurrencyCode: String,
    updateTransaction: (String) -> Unit
) {
    var amountToSend by remember { mutableStateOf("") }

    val borderColor = if (amountToSend.isEmpty())
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
    else
        MaterialTheme.colorScheme.primaryContainer

    val balanceBoxColor = if (amountToSend.isEmpty())
        MaterialTheme.colorScheme.onBackground
    else
        MaterialTheme.colorScheme.primary

    Column {
        Text(
            text = stringResource(R.string.amount_to_send_label),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = borderColor
                    ),
                    shape = MaterialTheme.shapes.small
                )
        ) {

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = amountToSend,
                onValueChange = { input ->
                    amountToSend = input

                    if (amountToSend.isEmpty()) {
                        updateTransaction("0")
                    } else {
                        updateTransaction(amountToSend)
                    }
                },
                placeholder = {
                    Text(
                        text = "00",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                trailingIcon = {
                    Text(
                        text = currentCurrencyCode,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    keyboardType = KeyboardType.Number
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = borderColor
                        ),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
                    .background(
                        color = balanceBoxColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)
                    )
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(
                        R.string.account_balance_text,
                        currentBalance,
                        currentCurrencyCode
                    ),
                    color = balanceBoxColor,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    modifier = Modifier.fillMaxWidth()
                )
            }


        }
    }
}

@Composable
fun RemitInfoSection() {
    Column {
        Text(
            text = stringResource(R.string.yearly_free_remittances_text),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.remit_info),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = stringResource(R.string.remaining_remittance_text),
            color = MaterialTheme.colorScheme.outlineVariant,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun FeesBreakdownSection(transaction: Transaction?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.fees_breakdown_text),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Moneco fees
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.moneco_fees_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${transaction?.monecoFees ?: 0.0} ${transaction?.currencyCode?.uppercase()}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Transfer fees
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.transfer_fees_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = formatAsPercentage(transaction?.transferFees ?: 0.01),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conversion rate
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.conversion_rate_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${transaction?.conversionRate} ${transaction?.recipient?.currencyCode?.uppercase() ?: ""}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Total spend
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.total_spent_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${
                    doubleToCurrency(
                        transaction?.totalSpent ?: 0.0,
                        "${transaction?.currencyCode}"
                    )
                } ${transaction?.currencyCode?.uppercase()}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        DashedLine(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))

        Spacer(modifier = Modifier.height(26.dp))

        // Recipient gets
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.total_received_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                style = MaterialTheme.typography.bodySmall,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "${
                    doubleToCurrency(
                        transaction?.amountReceived ?: 0.0,
                        "${transaction?.recipient?.currencyCode?.uppercase()}"
                    )
                } ${transaction?.recipient?.currencyCode?.uppercase() ?: ""}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TransactionOverviewScreenPreview() {
    RemitConnectTheme {
        TransactionSummaryScreen(
            navController = rememberNavController(),
            mainViewModel = hiltViewModel()
        )
    }
}