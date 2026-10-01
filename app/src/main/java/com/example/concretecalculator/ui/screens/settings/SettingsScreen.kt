package com.example.concretecalculator.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.concretecalculator.data.model.MeasurementUnit
import com.example.concretecalculator.data.model.ThemeMode
import com.example.concretecalculator.ui.components.AppTopBar
import com.example.concretecalculator.ui.viewmodel.ConcreteCalculatorViewModel

@Composable
fun SettingsScreen(
    viewModel: ConcreteCalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()

    var showResetSettingsDialog by remember { mutableStateOf(false) }
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showCustomCurrencyDialog by remember { mutableStateOf(false) }
    var customCurrencyInput by remember { mutableStateOf("") }

    val commonCurrencies = listOf("USD", "PKR", "EUR", "GBP", "CAD", "AUD")

    Scaffold(
        topBar = {
            AppTopBar(title = "Settings")
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Theme & Appearance
            SettingsGroup(title = "APPEARANCE") {
                // Theme Mode Selection
                Text(
                    text = "Theme Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = settings.themeMode == mode
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateThemeMode(mode) },
                            label = { Text(mode.name.lowercase().replaceFirstChar { it.uppercase() }) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Dynamic Color Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Dynamic Color",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Use system wallpaper colors (Android 12+)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.dynamicColor,
                        onCheckedChange = { viewModel.updateDynamicColor(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Defaults Section
            SettingsGroup(title = "CALCULATION DEFAULTS") {
                // Default Dimension Unit
                Text(
                    text = "Default Dimension Unit",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    MeasurementUnit.entries.forEach { unit ->
                        val isSelected = settings.defaultUnit == unit
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateDefaultUnit(unit) },
                            label = { Text(unit.symbol) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Default Waste Percentage
                Text(
                    text = "Default Waste Allowance",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(5.0, 10.0, 15.0, 20.0).forEach { waste ->
                        val isSelected = settings.defaultWastePercent == waste
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateDefaultWaste(waste) },
                            label = { Text("${waste.toInt()}%") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Currency Label
                Text(
                    text = "Project Currency Display Label",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    commonCurrencies.forEach { curr ->
                        val isSelected = settings.currency == curr
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.updateCurrency(curr) },
                            label = { Text(curr) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                TextButton(
                    onClick = {
                        customCurrencyInput = settings.currency
                        showCustomCurrencyDialog = true
                    }
                ) {
                    Text("Set Custom Currency (${settings.currency})")
                }
            }

            // Haptics & Sound Section
            SettingsGroup(title = "TACTILE & SOUND") {
                // Haptics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Haptic Feedback",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Subtle tactile pulses on actions and warnings",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.hapticFeedback,
                        onCheckedChange = { viewModel.updateHapticFeedback(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(14.dp))

                // Sound Effects
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Sound Effects",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Play subtle click sound on calculate and save",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = settings.soundEffects,
                        onCheckedChange = { viewModel.updateSoundEffects(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }

            // Management Section
            SettingsGroup(title = "DATA MANAGEMENT") {
                OutlinedButton(
                    onClick = { showResetSettingsDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Reset All Settings to Default")
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = { showClearHistoryDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Clear All Saved Calculations", color = MaterialTheme.colorScheme.error)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }

        // Custom Currency Dialog
        if (showCustomCurrencyDialog) {
            AlertDialog(
                onDismissRequest = { showCustomCurrencyDialog = false },
                title = { Text("Custom Currency Label") },
                text = {
                    Column {
                        Text("Enter currency code or symbol (e.g. INR, AED, $, £, Rs.):")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customCurrencyInput,
                            onValueChange = { customCurrencyInput = it.take(8) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (customCurrencyInput.isNotBlank()) {
                                viewModel.updateCurrency(customCurrencyInput.trim())
                            }
                            showCustomCurrencyDialog = false
                        }
                    ) {
                        Text("Apply")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomCurrencyDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reset Settings Dialog
        if (showResetSettingsDialog) {
            AlertDialog(
                onDismissRequest = { showResetSettingsDialog = false },
                title = { Text("Reset Settings?") },
                text = { Text("Restore all calculation units, waste defaults, and theme options to defaults?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.resetSettings()
                            showResetSettingsDialog = false
                        }
                    ) {
                        Text("Reset", color = MaterialTheme.colorScheme.primary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetSettingsDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Clear History Dialog
        if (showClearHistoryDialog) {
            AlertDialog(
                onDismissRequest = { showClearHistoryDialog = false },
                title = { Text("Clear Calculation History?") },
                text = { Text("Permanently delete all saved calculations from local storage?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllHistory()
                            showClearHistoryDialog = false
                        }
                    ) {
                        Text("Clear All", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showClearHistoryDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(14.dp))
            content()
        }
    }
}
