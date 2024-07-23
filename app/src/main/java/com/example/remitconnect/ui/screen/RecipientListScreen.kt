package com.example.remitconnect.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.CrossFade
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.remitconnect.R
import com.example.remitconnect.data.model.Contact
import com.example.remitconnect.data.model.Country
import com.example.remitconnect.data.model.Recipient
import com.example.remitconnect.enums.ProcessState
import com.example.remitconnect.navigation.RemitNavDestination
import com.example.remitconnect.ui.common.CustomButton
import com.example.remitconnect.ui.common.CustomIcon
import com.example.remitconnect.ui.common.CustomInputField
import com.example.remitconnect.ui.common.CustomLoader
import com.example.remitconnect.ui.common.ErrorHandlerView
import com.example.remitconnect.ui.theme.RemitConnectTheme
import com.example.remitconnect.utils.ContactPicker
import com.example.remitconnect.utils.Utils.convertContactToRecipient
import com.example.remitconnect.utils.Utils.displayToast
import com.example.remitconnect.utils.Utils.getCountries
import com.example.remitconnect.utils.Utils.getCountryByPhonePrefix
import com.example.remitconnect.utils.Utils.getCurrencyCode
import com.example.remitconnect.utils.Utils.getDefaultCountry
import com.example.remitconnect.viewModel.MainViewModel
import kotlinx.coroutines.launch

object RecipientListScreen : RemitNavDestination {
    override val route: String = "recipient_list_screen"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipientListScreen(
    navController: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedCountry by remember {
        mutableStateOf(getDefaultCountry())
    }

    var containerState by remember { mutableStateOf(false) }
    var input by remember { mutableStateOf("") }
    var selectedContact by remember { mutableStateOf<Contact?>(null) }

    val contactPickerLauncher = rememberLauncherForActivityResult(ContactPicker()) { contact ->
        selectedContact = contact
        getCountryByPhonePrefix("${contact?.phone}")?.let {
            selectedCountry = it
        }
    }

    val processState by mainViewModel.processState.collectAsState()
    val currentTransaction by mainViewModel.currentTransaction.collectAsState()
    val previousRecipients by mainViewModel.previousRecipients.collectAsState()

    // Filter the contacts based on the search input
    val filteredPreviousRecipients = if (input.isEmpty()) {
        previousRecipients
    } else {
        previousRecipients.filter { item ->
            item.name.contains(input, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.fetchRecipients()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HeaderSection(
            navController = navController,
            onPreviousRecipientsClicked = {
                containerState = false
            },
            onNewRecipientClicked = {
                containerState = true
            },
            keyboardController = keyboardController,
            enabledSearch = !containerState,
            onSearchInputChanged = {
                input = it
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (containerState) {
            NewRecipientsSection(
                onCountryPickerClick = {
                    showBottomSheet = true
                },
                onChooseContactClicked = {
                    contactPickerLauncher.launch(Unit)
                },
                onFormSubmitted = {
                    if (selectedCountry == null) {
                        displayToast(message = context.getString(R.string.select_a_country_toast))
                    } else if (selectedContact == null) {
                        displayToast(message = context.getString(R.string.select_contact_toast))
                    } else {
                        scope
                            .launch {
                                val recipient = convertContactToRecipient(
                                    contact = selectedContact!!,
                                    country = "${selectedCountry?.name}"
                                )
                                val updatedTransaction = currentTransaction?.copy(
                                    recipient = recipient
                                )
                                updatedTransaction?.let {
                                    mainViewModel.updateCurrentTransaction(
                                        transaction = it
                                    )
                                }
                            }
                            .invokeOnCompletion {
                                navController.navigate(MobileWalletOptScreen.route)
                            }
                    }
                },
                selectedCountry = selectedCountry,
                keyboardController = keyboardController,
                selectedContact = selectedContact
            )
        } else {
            when (processState) {
                ProcessState.Loading -> {
                    CustomLoader(modifier = Modifier.align(Alignment.CenterHorizontally))
                }

                ProcessState.Done -> {
                    PreviewRecipientsSection(
                        previousRecipients = filteredPreviousRecipients,
                        onItemClick = { item ->
                            scope
                                .launch {
                                    val updatedTransaction = currentTransaction?.copy(
                                        recipient = item.copy(currencyCode =  getCurrencyCode(item.country))
                                    )
                                    updatedTransaction?.let {
                                        mainViewModel.updateCurrentTransaction(
                                            transaction = it
                                        )
                                    }
                                }
                                .invokeOnCompletion {
                                    navController.navigate(MobileWalletOptScreen.route)
                                }
                        },
                        onErrorMessage = null
                    )
                }

                else -> {
                    PreviewRecipientsSection(
                        previousRecipients = filteredPreviousRecipients,
                        onItemClick = { },
                        onErrorMessage = (processState as ProcessState.Error).message
                    )
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet = false
            },
            sheetState = sheetState
        ) {
            // Content of the bottom sheet
            NewRecipientsSheetContent(
                onCountrySelected = { selectedItem ->
                    scope
                        .launch { sheetState.hide() }
                        .invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet = false
                            }
                        }
                    selectedCountry = selectedItem
                },
                keyboardController = keyboardController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeaderSection(
    navController: NavHostController,
    onPreviousRecipientsClicked: () -> Unit,
    onNewRecipientClicked: () -> Unit,
    keyboardController: SoftwareKeyboardController?,
    enabledSearch: Boolean,
    onSearchInputChanged: (String) -> Unit
) {
    var prevRecipientsState by remember { mutableStateOf(true) }
    var newRecipientsState by remember { mutableStateOf(false) }
    var searchInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
    ) {
        CustomIcon(
            imageResId = R.drawable.ic_baseline_arrow_back_24,
            contentDescription = stringResource(R.string.close_button_icon_desc),
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
            text = stringResource(R.string.recipients_screen_title_text),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp)
                )
        ) {
            // Previous recipients button
            CustomButton(
                onClick = {
                    onPreviousRecipientsClicked()
                    prevRecipientsState = true // Enable previous recipients button
                    newRecipientsState = false // Disable new recipient button
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (prevRecipientsState)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    contentColor = if (prevRecipientsState)
                        MaterialTheme.colorScheme.background
                    else
                        MaterialTheme.colorScheme.primary,
                ),
                shape = MaterialTheme.shapes.small,
                enabled = true,
                text = stringResource(R.string.previous_recipients_button_text),
                textColor = if (prevRecipientsState) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary
            )

            // New recipients button
            CustomButton(
                onClick = {
                    onNewRecipientClicked()
                    newRecipientsState = true // Enable new recipient button
                    prevRecipientsState = false // Disable previous recipients button
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (newRecipientsState)
                        MaterialTheme.colorScheme.primary
                    else
                        Color.Transparent,
                    contentColor = if (newRecipientsState)
                        MaterialTheme.colorScheme.onPrimary
                    else
                        MaterialTheme.colorScheme.primary,
                ),
                shape = MaterialTheme.shapes.small,
                enabled = true,
                text = stringResource(R.string.new_recipient_button_text),
                textColor = if (newRecipientsState) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search input field
        CustomInputField(
            value = searchInput,
            onValueChange = {
                searchInput = it
                onSearchInputChanged(it)
            },
            placeholderText = stringResource(R.string.search_field_placeholder),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                    contentDescription = stringResource(R.string.search_field_leading_icon_desc),
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Close the keyboard when the Done action is clicked
                    keyboardController?.hide()
                }
            ),
            enabled = enabledSearch
        )
    }
}

@Composable
fun PreviewRecipientsSection(
    previousRecipients: List<Recipient>,
    onItemClick: (Recipient) -> Unit,
    onErrorMessage: String?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        if (previousRecipients.isEmpty()) {
            Spacer(modifier = Modifier.height(36.dp))

            ErrorHandlerView(text = "$onErrorMessage")
        } else {
            Text(
                text = stringResource(R.string.recipients_text),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            LazyColumn {
                items(previousRecipients) { item ->
                    RecipientItem(
                        recipient = item,
                        onClick = { onItemClick(it) })
                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                    )
                }
            }
        }

    }
}

@Composable
fun RecipientItem(recipient: Recipient, onClick: (Recipient) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = { onClick(recipient) })
            .padding(vertical = 10.dp, horizontal = 16.dp)
    ) {
        CustomIcon(
            imageResId = R.drawable.ic_user_square,
            contentDescription = stringResource(R.string.contact_image_default_icon_desc),
            size = 40.dp,
            shape = RoundedCornerShape(10.dp),
            backgroundColor = MaterialTheme.colorScheme.surface
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = recipient.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = recipient.mobileWallet,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = stringResource(R.string.back_arrow_icon_desc),
            modifier = Modifier.size(10.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecipientsSheetContent(
    onCountrySelected: (Country) -> Unit,
    keyboardController: SoftwareKeyboardController?
) {
    var searchInput by remember { mutableStateOf("") }

    // Get the initial list of countries
    val allCountries = getCountries()

    // Filter the countries based on the search input
    val filteredCountries = if (searchInput.isEmpty()) {
        allCountries
    } else {
        allCountries.filter { country ->
            country.name.contains(searchInput, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.new_recipient_sheet_title_text),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomInputField(
            value = searchInput,
            onValueChange = { searchInput = it },
            placeholderText = stringResource(R.string.search_field_placeholder),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent
            ),
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_search_24),
                    contentDescription = stringResource(R.string.search_field_leading_icon_desc),
                    tint = Color.Gray
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    // Close the keyboard when the Done action is clicked
                    keyboardController?.hide()
                }
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (filteredCountries.isEmpty()) {
            ErrorHandlerView(text = stringResource(R.string.country_not_found_text))
        } else {
            LazyColumn {
                items(filteredCountries) { country ->
                    Spacer(modifier = Modifier.height(8.dp))

                    CountryItem(
                        country = country,
                        onItemClicked = {
                            onCountrySelected(country)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CountryItem(country: Country, onItemClicked: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClicked),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = stringResource(R.string.country_flag_url, country.code.uppercase()),
            contentDescription = stringResource(R.string.country_flag_image_desc),
            transition = CrossFade,
            modifier = Modifier
                .clip(CircleShape)
                .padding(start = 16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(text = country.name)
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun NewRecipientsSection(
    selectedCountry: Country?,
    selectedContact: Contact?,
    onFormSubmitted: () -> Unit,
    onCountryPickerClick: () -> Unit,
    onChooseContactClicked: () -> Unit,
    keyboardController: SoftwareKeyboardController?
) {
    var firstName by remember { mutableStateOf(selectedContact?.firstName ?: "") }
    var lastName by remember { mutableStateOf(selectedContact?.lastName ?: "") }
    var phoneNumber by remember { mutableStateOf(selectedContact?.phone ?: "") }
    var enabled by remember { mutableStateOf(true) }

    // Watch for changes in selectedContact and update firstName accordingly
    LaunchedEffect(selectedContact) {
        firstName = selectedContact?.firstName ?: ""
        lastName = selectedContact?.lastName ?: ""
        phoneNumber = selectedContact?.phone ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = stringResource(R.string.country_text),
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                )
                .clickable { onCountryPickerClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GlideImage(
                model = stringResource(
                    R.string.country_flag_url,
                    "${selectedCountry?.flag?.uppercase()}"
                ),
                contentDescription = stringResource(R.string.select_country_flag_desc),
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clip(RoundedCornerShape(64.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = CircleShape
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${selectedCountry?.name}",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier
                    .weight(1f)
            )

            Text(
                text = selectedCountry?.phonePrefix ?: "",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = stringResource(R.string.dropdown_arrow_down_icon_desc),
                modifier = Modifier.padding(end = 10.dp, top = 15.dp, bottom = 15.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Phone contact picker
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                )
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    shape = MaterialTheme.shapes.small
                )
                .clickable(onClick = onChooseContactClicked),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_document),
                contentDescription = stringResource(id = R.string.choose_a_contact_icon_desc),
                modifier = Modifier.padding(vertical = 13.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = stringResource(R.string.choose_a_contact_button_text),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                modifier = Modifier.weight(0.5f)
            )

            Text(
                text = stringResource(R.string.or_add_manually_text),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                modifier = Modifier.weight(0.5f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phone number input
        NewRecipientInputField(
            label = stringResource(R.string.phone_number_label),
            input = phoneNumber,
            onValueChange = { phoneNumber = it },
            placeholderText = stringResource(R.string.enter_your_phone_number_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardController = keyboardController
        )

        Spacer(modifier = Modifier.height(16.dp))

        // First Name input
        NewRecipientInputField(
            label = stringResource(R.string.first_name_label),
            input = firstName,
            onValueChange = { firstName = it },
            placeholderText = stringResource(R.string.enter_your_first_name_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Next
            ),
            keyboardController = keyboardController
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Last Name input
        NewRecipientInputField(
            label = stringResource(R.string.last_name_label),
            input = lastName,
            onValueChange = { lastName = it },
            placeholderText = stringResource(R.string.enter_your_last_name_placeholder),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardController = keyboardController
        )

        Spacer(modifier = Modifier.height(45.dp))

        CustomButton(
            onClick = {
                enabled = selectedCountry != null && selectedContact != null
                onFormSubmitted()
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
            enabled = enabled,
            text = stringResource(R.string.continue_button_text),
            textColor = MaterialTheme.colorScheme.background
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewRecipientInputField(
    label: String,
    input: String, onValueChange: (String) -> Unit = {},
    placeholderText: String?,
    keyboardOptions: KeyboardOptions,
    keyboardController: SoftwareKeyboardController?
) {
    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        CustomInputField(
            value = input,
            onValueChange = onValueChange,
            placeholderText = placeholderText,
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                focusedBorderColor = MaterialTheme.colorScheme.primaryContainer
            ),
            shape = MaterialTheme.shapes.small,
            leadingIcon = null,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions(
                onDone = {
                    // Close the keyboard when the Done action is clicked
                    keyboardController?.hide()
                }
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RecipientListScreenPreview() {
    RemitConnectTheme {
        RecipientListScreen(
            navController = rememberNavController(),
            mainViewModel = hiltViewModel()
        )
    }
}