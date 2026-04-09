package com.example.reloadcostcaluclator.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun TextInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun DecimalNumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberInputField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Decimal,
        allowDecimal = true,
        modifier = modifier,
    )
}

@Composable
fun IntegerNumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NumberInputField(
        label = label,
        value = value,
        onValueChange = onValueChange,
        keyboardType = KeyboardType.Number,
        allowDecimal = false,
        modifier = modifier,
    )
}

@Composable
private fun NumberInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType,
    allowDecimal: Boolean,
    modifier: Modifier = Modifier,
) {
    OutlinedTextField(
        value = value,
        onValueChange = { nextValue ->
            val normalized = nextValue.trim()
            val validInput = if (allowDecimal) {
                normalized.matches(Regex("^\\d*\\.?\\d*$"))
            } else {
                normalized.matches(Regex("^\\d*$"))
            }

            if (validInput) {
                onValueChange(normalized)
            }
        },
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
    )
}
