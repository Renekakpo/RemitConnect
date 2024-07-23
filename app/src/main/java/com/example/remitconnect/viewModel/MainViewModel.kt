package com.example.remitconnect.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remitconnect.R
import com.example.remitconnect.RemitConnectApp.Companion.appContext
import com.example.remitconnect.data.api.RemitApi
import com.example.remitconnect.data.local.dao.TransactionDao
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.data.model.Recipient
import com.example.remitconnect.enums.ProcessState
import com.example.remitconnect.utils.Utils.mapMobileWalletsToLogos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remitApi: RemitApi,
    private val transactionDao: TransactionDao
) : ViewModel() {
    // Default currency code used throughout the application
    val defaultCurrencyCode = "EUR"

    // Flow to hold the current balance of the user (demo purposes)
    private val _currentBalance = MutableStateFlow(0.0)
    val currentBalance: StateFlow<Double> = _currentBalance

    // Flow to represent the state of a process (Loading, Done, Error)
    private val _processState = MutableStateFlow<ProcessState>(ProcessState.Loading)
    val processState: StateFlow<ProcessState> = _processState

    // Flow to hold a list of previous recipients
    private val _previousRecipients = MutableStateFlow<List<Recipient>>(emptyList())
    val previousRecipients: StateFlow<List<Recipient>> = _previousRecipients

    // Flow to hold a list of mobile wallets
    private val _mobileWallets = MutableStateFlow<List<MobileWallet>>(emptyList())
    val mobileWallets: StateFlow<List<MobileWallet>> = _mobileWallets

    // Flow to hold the current transaction
    private val _currentTransaction = MutableStateFlow<Transaction?>(null)
    val currentTransaction: StateFlow<Transaction?> = _currentTransaction

    // Flow to hold a list of local transactions
    private val _localTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val localTransactions: StateFlow<List<Transaction>> = _localTransactions

    // Fetches the last 5 transactions from local database and updates state
    fun fetchLocalLast5Transactions() {
        _processState.value = ProcessState.Loading // Set the process state to Loading
        viewModelScope.launch {
            _processState.value = try {
                val last5Transactions = transactionDao.getLast5Transactions()
                if (last5Transactions.isEmpty()) {
                    ProcessState.Error(message = appContext.getString(R.string.no_transactions_yet_text))
                } else {
                    _localTransactions.value = last5Transactions
                    ProcessState.Done
                }
            } catch (e: Exception) {
                ProcessState.Error(
                    message = e.localizedMessage ?: appContext.getString(R.string.failed_to_fetch_last_5_local_transactions_text)
                )
            }
        }
    }

    // Fetches the list of previous recipients from the API and updates state
    fun fetchRecipients() {
        _processState.value = ProcessState.Loading // Set the process state to Loading
        viewModelScope.launch {
            _processState.value = try {
                val recipients = remitApi.getPreviousRecipients()
                if (recipients.isEmpty()) {
                    ProcessState.Error(message = appContext.getString(R.string.no_recipients_found_text))
                } else {
                    updatePreviousRecipients(recipients)
                    ProcessState.Done
                }
            } catch (e: Exception) {
                ProcessState.Error(message = e.localizedMessage ?: appContext.getString(R.string.recipients_fetch_failed_text))
            }
        }
    }

    // Fetches the list of mobile wallets from the API, maps them to logos, and updates state
    fun fetchMobileWallets() {
        _processState.value = ProcessState.Loading // Set the process state to Loading
        viewModelScope.launch {
            _processState.value = try {
                val mobileWallets = remitApi.getMobileWallets()
                if (mobileWallets.isEmpty()) {
                    ProcessState.Error(message = appContext.getString(R.string.empty_mobile_wallets_text))
                } else {
                    val mappedWallets = mapMobileWalletsToLogos(mobileWallets)
                    updateMobileWallets(mappedWallets)
                    ProcessState.Done
                }
            } catch (e: Exception) {
                ProcessState.Error(
                    message = e.localizedMessage ?: appContext.getString(R.string.failed_to_fetch_mobile_wallets_text)
                )
            }
        }
    }

    // Updates the list of previous recipients
    private fun updatePreviousRecipients(recipients: List<Recipient>) {
        _previousRecipients.value = recipients
    }

    // Updates the list of mobile wallets
    private fun updateMobileWallets(mobileWallets: List<MobileWallet>) {
        _mobileWallets.value = mobileWallets
    }

    // Updates the current balance
    private fun updateBalance(newBalance: Double) {
        viewModelScope.launch { _currentBalance.value = newBalance }
    }

    // Updates the current transaction
    fun updateCurrentTransaction(transaction: Transaction?) {
        viewModelScope.launch { _currentTransaction.value = transaction }
    }

    // Inserts a transaction into the local database
    fun insertCurrentTransaction(transaction: Transaction) {
        viewModelScope.launch {
            transactionDao.insertTransaction(transaction)
        }
    }

    // Calculate the remaining balance based on the total spent sum
    fun calculateRemainingBalance() {
        viewModelScope.launch {
            try {
                val totalSpentSum = transactionDao.getTotalSpentSum() ?: 0.0
                updateBalance(5000.0 - totalSpentSum)
            } catch (e: Exception) {
                _currentBalance.value = 5000.0
            }
        }
    }
}