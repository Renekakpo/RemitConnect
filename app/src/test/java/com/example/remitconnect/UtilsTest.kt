package com.example.remitconnect

import com.example.remitconnect.data.local.entities.Transaction
import com.example.remitconnect.data.model.Contact
import com.example.remitconnect.data.model.MobileWallet
import com.example.remitconnect.utils.Utils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale


class UtilsTest {

    @Test
    fun testGetCountries() {
        // When
        val countries = Utils.getCountries()

        // Then
        assertEquals(Locale.getISOCountries().size, countries.size)
    }

    @Test
    fun testGetCountryByPhonePrefix() {
        // Given
        val phoneNumberStr = "+22990010203"
        val country = Utils.getCountryByPhonePrefix(phoneNumberStr)

        // Then
        assertEquals("BJ", country?.code) // Assuming +229 is BJ(Benin)
    }

    @Test
    fun testGetDefaultCountry() {
        // Given
        val defaultCountry = Utils.getDefaultCountry()

        // Then
        assertEquals(Locale.getDefault().country, defaultCountry?.code)
    }

    @Test
    fun testGetCurrencyCode() {
        // Given
        val countryName = "United States"
        val currencyCode = Utils.getCurrencyCode(countryName)

        // Then
        assertEquals("USD", currencyCode)
    }

    @Test
    fun testMapMobileWalletsToLogos() {
        // Given
        val wallets = listOf(
            MobileWallet(id = "", name = "Wave"),
            MobileWallet(id = "", name = "MTN"),
            MobileWallet(id = "", name = "Orange"),
            MobileWallet(id = "", name = "Moov"),
            MobileWallet(id = "", name = "Cash")
        )

        // When
        val mappedWallets = Utils.mapMobileWalletsToLogos(wallets)

        // Then
        assertEquals(R.drawable.ic_wave_wallet, mappedWallets[0].image)
        assertEquals(R.drawable.ic_mtn_money_wallet, mappedWallets[1].image)
        assertEquals(R.drawable.ic_orange_money_wallet, mappedWallets[2].image)
        assertEquals(R.drawable.ic_moov_money, mappedWallets[3].image)
        assertEquals(R.drawable.ic_cash_plus, mappedWallets[4].image)
    }

    @Test
    fun testDoubleToCurrency() {
        // Given
        val amount = 123.456
        val currencyCode = "USD"
        val formattedAmount = Utils.doubleToCurrency(amount, currencyCode)

        // Then
        assertEquals("123.456", formattedAmount)
    }

    @Test
    fun testFormatAsPercentage() {
        // Given
        val value = 0.1234
        val formattedPercentage = Utils.formatAsPercentage(value)

        // Then
        assertEquals("12.34%", formattedPercentage)
    }

    @Test
    fun testCalculateTransaction() {
        // Given
        val amountToSend = 100.0
        val currentTransaction = Transaction()
        val transaction = Utils.calculateTransaction(amountToSend, currentTransaction)

        // Allowable delta for floating-point comparisons
        val delta = 0.001

        // Then
        val expectedTransferFees = amountToSend * 0.05
        assertEquals(expectedTransferFees, transaction.transferFees, delta)
        assertEquals(amountToSend + currentTransaction.monecoFees + expectedTransferFees, transaction.totalSpent, delta)
        assertEquals((amountToSend - expectedTransferFees) * currentTransaction.conversionRate, transaction.amountReceived, delta)
    }

    @Test
    fun testConvertContactToRecipient() {
        // Given
        val contact = Contact("John", "Doe", "1234567890", R.drawable.ic_moneys)
        val country = "United States"
        val recipient = Utils.convertContactToRecipient(contact, country)

        // Then
        assertEquals(contact.firstName + " " + contact.lastName, recipient.name)
        assertEquals(contact.phone, recipient.phoneNumber)
        assertEquals(Utils.getCurrencyCode(country), recipient.currencyCode)
    }
}