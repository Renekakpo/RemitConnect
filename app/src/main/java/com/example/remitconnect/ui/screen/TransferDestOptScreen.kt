package com.example.remitconnect.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.remitconnect.R
import com.example.remitconnect.data.model.sampleTransferDestOptList
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.CustomIcon
import com.example.remitconnect.ui.common.TransferOptionItem
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.viewModel.MainViewModel
import kotlinx.coroutines.launch

object TransferDestOptScreen : RemitNavDestination {
    override val route: String = "transfer_dest_opt_screen"
}

@Composable
fun TransferDestOptScreen(
    navController: NavHostController,
    transferOptTitle: String,
    mainViewModel: MainViewModel
) {
    val scope = rememberCoroutineScope()
    val currentTransaction by mainViewModel.currentTransaction.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .wrapContentSize()
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
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = transferOptTitle,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(sampleTransferDestOptList) { item ->
                TransferOptionItem(
                    item = item,
                    onClicked = {
                        scope
                            .launch {
                                // Set the transfer destination
                                val updatedTransaction = currentTransaction?.copy(
                                    from = item.title
                                )
                                // Update the current transaction
                                updatedTransaction?.let {
                                    mainViewModel.updateCurrentTransaction(
                                        transaction = it
                                    )
                                }
                            }
                            .invokeOnCompletion {
                                navController.navigate(RecipientListScreen.route)
                            }
                    }
                )
                HorizontalDivider(
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                )
            }
        }

    }
}

@Preview(showBackground = true)
@Composable
fun TransferDestOptScreenPreview() {
    RemitConnectTheme {
        TransferDestOptScreen(
            navController = rememberNavController(),
            transferOptTitle = "Lorem Ipsum",
            mainViewModel = hiltViewModel()
        )
    }
}