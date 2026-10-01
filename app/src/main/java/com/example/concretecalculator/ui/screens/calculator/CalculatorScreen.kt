package com.example.concretecalculator.ui.screens.calculator

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.concretecalculator.data.model.DepthPreset
import com.example.concretecalculator.ui.components.AppTopBar
import com.example.concretecalculator.ui.components.DepthPresetChips
import com.example.concretecalculator.ui.components.DimensionInputField
import com.example.concretecalculator.ui.components.LivePreviewCard
import com.example.concretecalculator.ui.components.WasteSelector
import com.example.concretecalculator.ui.viewmodel.ConcreteCalculatorViewModel

@Composable
fun CalculatorScreen(
    viewModel: ConcreteCalculatorViewModel,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    var isBagsSectionExpanded by rememberSaveable { mutableStateOf(false) }
    var isCostSectionExpanded by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AppTopBar(
                title = "Concrete Calculator",
                actions = {
                    IconButton(
                        onClick = { viewModel.resetCalculator() },
                        modifier = Modifier.semantics {
                            contentDescription = "Reset calculator inputs"
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            val isTabletOrLandscape = maxWidth >= 600.dp

            if (isTabletOrLandscape) {
                // Two-column layout for tablets and landscape
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left Column: Scrollable Inputs
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DimensionsSection(
                            viewModel = viewModel,
                            uiState = uiState
                        )

                        WasteSection(
                            viewModel = viewModel,
                            uiState = uiState
                        )

                        OptionalBagSection(
                            isExpanded = isBagsSectionExpanded,
                            onToggleExpand = { isBagsSectionExpanded = !isBagsSectionExpanded },
                            selectedBagSize = uiState.selectedBagSizeKg,
                            isCustomBagSize = uiState.isCustomBagSize,
                            customBagSizeInput = uiState.customBagSizeInput,
                            bagsPerM3Input = uiState.bagsPerM3Input,
                            bagsPerM3Error = uiState.bagsPerM3Error,
                            onBagSizeSelected = viewModel::onBagSizeSelected,
                            onCustomBagSizeChange = viewModel::onCustomBagSizeChange,
                            onBagsPerM3Change = viewModel::onBagsPerM3Change
                        )

                        OptionalCostSection(
                            isExpanded = isCostSectionExpanded,
                            onToggleExpand = { isCostSectionExpanded = !isCostSectionExpanded },
                            priceInput = uiState.pricePerM3Input,
                            currency = appSettings.currency,
                            priceError = uiState.pricePerM3Error,
                            onPriceChange = viewModel::onPricePerM3Change
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Right Column: Live Result & Actions
                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .fillMaxHeight()
                            .padding(top = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "ESTIMATION PREVIEW",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            letterSpacing = 1.sp
                        )

                        LivePreviewCard(
                            baseVolumeM3 = uiState.liveBaseVolumeM3,
                            requiredVolumeM3 = uiState.liveRequiredVolumeM3,
                            wastePercent = uiState.wastePercent
                        )

                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Ready to review specifications?",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Calculate to see cubic feet, liters, optional bag quantities, and total project costs.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.calculate(onSuccess = onNavigateToResult)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .semantics {
                                    contentDescription = "Calculate concrete"
                                }
                        ) {
                            Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "CALCULATE",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } else {
                // Phone portrait: Single-column scrollable
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DimensionsSection(
                        viewModel = viewModel,
                        uiState = uiState
                    )

                    WasteSection(
                        viewModel = viewModel,
                        uiState = uiState
                    )

                    OptionalBagSection(
                        isExpanded = isBagsSectionExpanded,
                        onToggleExpand = { isBagsSectionExpanded = !isBagsSectionExpanded },
                        selectedBagSize = uiState.selectedBagSizeKg,
                        isCustomBagSize = uiState.isCustomBagSize,
                        customBagSizeInput = uiState.customBagSizeInput,
                        bagsPerM3Input = uiState.bagsPerM3Input,
                        bagsPerM3Error = uiState.bagsPerM3Error,
                        onBagSizeSelected = viewModel::onBagSizeSelected,
                        onCustomBagSizeChange = viewModel::onCustomBagSizeChange,
                        onBagsPerM3Change = viewModel::onBagsPerM3Change
                    )

                    OptionalCostSection(
                        isExpanded = isCostSectionExpanded,
                        onToggleExpand = { isCostSectionExpanded = !isCostSectionExpanded },
                        priceInput = uiState.pricePerM3Input,
                        currency = appSettings.currency,
                        priceError = uiState.pricePerM3Error,
                        onPriceChange = viewModel::onPricePerM3Change
                    )

                    LivePreviewCard(
                        baseVolumeM3 = uiState.liveBaseVolumeM3,
                        requiredVolumeM3 = uiState.liveRequiredVolumeM3,
                        wastePercent = uiState.wastePercent
                    )

                    Button(
                        onClick = {
                            viewModel.calculate(onSuccess = onNavigateToResult)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .semantics {
                                contentDescription = "Calculate concrete"
                            }
                    ) {
                        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "CALCULATE",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
private fun DimensionsSection(
    viewModel: ConcreteCalculatorViewModel,
    uiState: com.example.concretecalculator.ui.viewmodel.CalculatorUiState
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "SLAB & STRUCTURE DIMENSIONS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 1.sp
            )

            DimensionInputField(
                label = "Length",
                value = uiState.lengthInput,
                unit = uiState.lengthUnit,
                onValueChange = viewModel::onLengthChange,
                onUnitChange = viewModel::onLengthUnitChange,
                errorMessage = uiState.lengthError,
                placeholder = "5.00"
            )

            DimensionInputField(
                label = "Width",
                value = uiState.widthInput,
                unit = uiState.widthUnit,
                onValueChange = viewModel::onWidthChange,
                onUnitChange = viewModel::onWidthUnitChange,
                errorMessage = uiState.widthError,
                placeholder = "4.00"
            )

            DimensionInputField(
                label = "Depth / Thickness",
                value = uiState.depthInput,
                unit = uiState.depthUnit,
                onValueChange = viewModel::onDepthChange,
                onUnitChange = viewModel::onDepthUnitChange,
                errorMessage = uiState.depthError,
                placeholder = "150"
            )

            DepthPresetChips(
                selectedPresetLabel = uiState.selectedDepthPreset,
                onPresetSelected = viewModel::onDepthPresetSelected
            )
        }
    }
}

@Composable
private fun WasteSection(
    viewModel: ConcreteCalculatorViewModel,
    uiState: com.example.concretecalculator.ui.viewmodel.CalculatorUiState
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            WasteSelector(
                selectedWastePercent = uiState.wastePercent,
                isCustomWaste = uiState.isCustomWaste,
                customWasteInput = uiState.customWasteInput,
                errorMessage = uiState.wasteError,
                onPresetSelected = viewModel::onWastePercentSelected,
                onCustomSelected = viewModel::onCustomWasteSelected,
                onCustomWasteChange = viewModel::onCustomWasteChange
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun OptionalBagSection(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    selectedBagSize: Int?,
    isCustomBagSize: Boolean,
    customBagSizeInput: String,
    bagsPerM3Input: String,
    bagsPerM3Error: String?,
    onBagSizeSelected: (Int?) -> Unit,
    onCustomBagSizeChange: (String) -> Unit,
    onBagsPerM3Change: (String) -> Unit
) {
    val standardSizes = listOf(20, 25, 30, 40, 50)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Inventory2,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Bag Estimate (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Select Bag Weight",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        standardSizes.forEach { size ->
                            val isSelected = !isCustomBagSize && selectedBagSize == size
                            FilterChip(
                                selected = isSelected,
                                onClick = { onBagSizeSelected(size) },
                                label = { Text("$size kg") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }

                        FilterChip(
                            selected = isCustomBagSize,
                            onClick = { onBagSizeSelected(null) },
                            label = { Text("Custom") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }

                    if (isCustomBagSize) {
                        OutlinedTextField(
                            value = customBagSizeInput,
                            onValueChange = { input ->
                                if (input.isEmpty() || input.matches(Regex("^\\d*$"))) {
                                    onCustomBagSizeChange(input)
                                }
                            },
                            label = { Text("Custom Bag Size (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                    Text(
                        text = "Coverage Rate (Bags per m³)",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = bagsPerM3Input,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                onBagsPerM3Change(input)
                            }
                        },
                        label = { Text("Estimated Bags Per m³") },
                        placeholder = { Text("e.g. 40 or 45") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = bagsPerM3Error != null,
                        supportingText = {
                            if (bagsPerM3Error != null) {
                                Text(bagsPerM3Error, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Varies by manufacturer mix (usually 40–55 bags/m³)")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionalCostSection(
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    priceInput: String,
    currency: String,
    priceError: String?,
    onPriceChange: (String) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggleExpand() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Payments,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cost Estimation (Optional)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier.padding(top = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceInput,
                        onValueChange = { input ->
                            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                                onPriceChange(input)
                            }
                        },
                        label = { Text("Price Per m³ ($currency)") },
                        placeholder = { Text("e.g. 110.00") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        isError = priceError != null,
                        supportingText = {
                            if (priceError != null) {
                                Text(priceError, color = MaterialTheme.colorScheme.error)
                            } else {
                                Text("Cost calculated as Required Volume × Price")
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            errorBorderColor = MaterialTheme.colorScheme.error
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
