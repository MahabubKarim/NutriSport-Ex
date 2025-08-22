package com.nutrisport.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nutrisport.shared.Resources
import com.nutrisport.shared.component.PrimaryButton
import com.nutrisport.shared.component.ProfileForm

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileForm(
            modifier = Modifier.weight(1f),
            firstName = "",
            onFirstNameChange = {},
            lastName = "",
            onLastNameChange = {},
            email = "",
            city = "",
            onCityChange = {},
            postalCode = 1111,
            onPostalCodeChange = {},
            address = "",
            onAddressChange = {},
            phoneNumber = "",
            onPhoneNumberChange = {}
        )
    }
}