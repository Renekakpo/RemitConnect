package com.example.remitconnect.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.R
import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.enums.ProcessState
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.CustomButton
import com.example.remitconnect.ui.common.CustomIcon
import com.example.remitconnect.ui.common.CustomLoader
import com.example.remitconnect.ui.common.ErrorHandlerView
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.viewModel.MainViewModel
import kotlinx.coroutines.launch

object MobileWalletOptScreen : RemitNavDestination {
    override val route: String = "mobile_wallet_opt_screen"
}

@Composable
fun MobileWalletOptScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    var selectMobileWallet by remember { mutableStateOf<MobileWallet?>(null) }
    val scope = rememberCoroutineScope()

    val processState by mainViewModel.processState.collectAsState()
    val currentTransaction by mainViewModel.currentTransaction.collectAsState()
    val mobileWallets by mainViewModel.mobileWallets.collectAsState()

    LaunchedEffect(mobileWallets) {
        mainViewModel.fetchMobileWallets()
        val walletName = currentTransaction?.recipient?.mobileWallet
        selectMobileWallet = mobileWallets.find { it.name == walletName }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
                .padding(vertical = 16.dp)
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.mobile_wallet_opt_screen_title_text),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (processState) {
            ProcessState.Loading -> {
                CustomLoader(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            ProcessState.Done -> {
                LazyColumn {
                    items(mobileWallets) { item ->
                        MobileWalletItem(
                            item = item,
                            isSelectedItem = item == selectMobileWallet,
                            onItemClicked = { wallet ->
                                selectMobileWallet = wallet
                            }
                        )
                    }
                }
            }

            else -> {
                ErrorHandlerView(text = (processState as ProcessState.Error).message)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        CustomButton(
            onClick = {
                scope
                    .launch {
                        val updatedTransaction = currentTransaction?.copy(
                            selectedWallet = selectMobileWallet?.name
                        )
                        updatedTransaction?.let { mainViewModel.updateCurrentTransaction(transaction = it) }
                    }
                    .invokeOnCompletion {
                        navController.navigate(TransactionSummaryScreen.route)
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
            ),
            shape = MaterialTheme.shapes.small,
            enabled = selectMobileWallet != null,
            text = stringResource(id = R.string.continue_button_text),
            textColor = MaterialTheme.colorScheme.background
        )
    }
}

@Composable
fun MobileWalletItem(
    item: MobileWallet,
    isSelectedItem: Boolean,
    onItemClicked: (MobileWallet) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .background(
                color = if (isSelectedItem) MaterialTheme.colorScheme.surface else Color.Transparent,
                shape = MaterialTheme.shapes.small
            )
            .border(
                width = 1.dp,
                color = if (isSelectedItem)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.onBackground.copy(
                        alpha = 0.3f
                    ),
                shape = MaterialTheme.shapes.small
            )
            .clickable {
                onItemClicked(item)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painterResource(id = item.image ?: R.drawable.ic_broken_image),
            contentDescription = stringResource(R.string.wallet_image_desc),
            modifier = Modifier
                .padding(start = 16.dp, top = 10.dp, bottom = 10.dp)
                .size(30.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = item.name,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )

        if (isSelectedItem) {
            Spacer(modifier = Modifier.weight(1f))

            Icon(
                imageVector = Icons.Outlined.CheckCircle,
                contentDescription = stringResource(R.string.check_circle_icon_desc),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 10.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MobileWalletOptScreenPreview() {
    RemitConnectTheme {
        MobileWalletOptScreen(
            navController = rememberNavController(),
            mainViewModel = hiltViewModel()
        )
    }
}