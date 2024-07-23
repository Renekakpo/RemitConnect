package com.example.remitconnect.utils

import android.widget.Toast
import com.example.remitconnect.R
import com.example.remitconnect.RemitConnectApp.Companion.appContext
import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.data.model.Contact
import com.example.remitconnect.data.model.Country
import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.data.model.Recipient
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import java.util.UUID

object Utils {

    /**
     * Returns a list of all countries with their names, country codes, phone prefixes, and flags.
     */
    fun getCountries(): List<Country> {
        val countries = mutableListOf<Country>()
        val locale = Locale.getISOCountries()

        for (countryCode in locale) {
            val country = Locale("", countryCode)
            val phonePrefix = PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode)
            countries.add(
                Country(
                    name = country.displayCountry,
                    code = countryCode,
                    phonePrefix = "$phonePrefix",
                    flag = countryCode
                )
            )
        }

        return countries
    }

    /**
     * Returns a Country object corresponding to the phone prefix extracted from the given phone number string.
     */
    fun getCountryByPhonePrefix(phoneNumberStr: String): Country? {
        try {
            val phoneNumberUtil = PhoneNumberUtil.getInstance()
            // Parse the phone number string
            val phoneNumber: Phonenumber.PhoneNumber = phoneNumberUtil.parse(phoneNumberStr, null)

            // Get the country code as the prefix
            val countryCode = phoneNumber.countryCode

            // Find the country from the list based on phone prefix
            val country = getCountries().find { it.phonePrefix == "$countryCode" }
            return country
        } catch (e: Exception) {
            // Return null if parsing fails
            return null
        }
    }

    /**
     * Retrieves the default country based on the device's locale settings.
     */
    fun getDefaultCountry(): Country? {
        val defaultCountryCode = Locale.getDefault().country
        val countries = getCountries()

        // Find and return the country matching the default country code
        return countries.find { it.code == defaultCountryCode }
    }

    /**
     * Returns the currency code for a given country name.
     */
    fun getCurrencyCode(countryName: String): String? {
        // Find the locale for the given country name
        val locale = Locale.getAvailableLocales().find { it.displayCountry.equals(countryName, ignoreCase = true) }

        // If the locale is found, get the currency code
        return locale?.let {
            Currency.getInstance(it).currencyCode
        }
    }

    /**
     * Maps a list of MobileWallets to their corresponding logo resources.
     */
    fun mapMobileWalletsToLogos(wallets: List<MobileWallet>): List<MobileWallet> {
        return wallets.map { wallet ->
            wallet.image = when {
                wallet.name.lowercase().contains("wave", ignoreCase = true) -> R.drawable.ic_wave_wallet
                wallet.name.lowercase().contains("mtn", ignoreCase = true) -> R.drawable.ic_mtn_money_wallet
                wallet.name.lowercase().contains("orange", ignoreCase = true) -> R.drawable.ic_orange_money_wallet
                wallet.name.lowercase().contains("moov", ignoreCase = true) -> R.drawable.ic_moov_money
                wallet.name.lowercase().contains("cash", ignoreCase = true) -> R.drawable.ic_cash_plus
                else -> R.drawable.ic_broken_image
            }
            wallet
        }
    }

    /**
     * Converts a double amount to a currency string format without the currency symbol.
     */
    fun doubleToCurrency(amount: Double, currencyCode: String): String {
        return try {
            val currencyInstance = NumberFormat.getCurrencyInstance()
            currencyInstance.maximumFractionDigits = 5
            val currency = Currency.getInstance(currencyCode)

            // Set the currency and format the amount
            currencyInstance.currency = currency
            val roundedAmount = String.format(Locale.getDefault(), "%.3f", amount).toDouble()

            // Format amount without currency symbol
            val formattedAmount = currencyInstance.format(roundedAmount)

            // Remove currency symbol
            val currencySymbol = currency.symbol
            formattedAmount.replace(currencySymbol, "").trim()
        } catch (e: Exception) {
            // Return the amount as a string if formatting fails
            amount.toString()
        }
    }

    /**
     * Formats a double value as a percentage with two decimal places.
     */
    fun formatAsPercentage(value: Double): String {
        val percentFormat = NumberFormat.getPercentInstance()
        percentFormat.minimumFractionDigits = 2
        return percentFormat.format(value)
    }

    /**
     * Calculates transaction details including fees, total spent, and amount received.
     */
    fun calculateTransaction(amountToSend: Double, currentTransaction: Transaction): Transaction {
        val transferFeesPercentage = 0.05 // Example: 5% transaction fee
        val transferFees = amountToSend * transferFeesPercentage
        val totalSpent = amountToSend + currentTransaction.monecoFees + transferFees
        val amountReceived = (amountToSend - transferFees) * currentTransaction.conversionRate

        return currentTransaction.copy(
            amount = amountToSend.toString(),
            transferFees = transferFees,
            totalSpent = totalSpent,
            amountReceived = amountReceived
        )
    }

    /**
     * Converts a contact to a recipient object with a unique ID and other relevant details.
     */
    fun convertContactToRecipient(contact: Contact, country: String): Recipient {
        val id = UUID.randomUUID().toString()
        val name = "${contact.firstName} ${contact.lastName}"
        val currencyCode = getCurrencyCode(country)

        return Recipient(
            id = id,
            name = name,
            country = country,
            mobileWallet = "",
            phoneNumber = contact.phone,
            currencyCode = currencyCode
        )
    }

    /**
     * Displays a toast message on the screen.
     */
    fun displayToast(message: String) {
        Toast.makeText(appContext, message, Toast.LENGTH_SHORT).show()
    }
}