package com.example.simplecal

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.KeyboardReturn
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplecal.ui.theme.LocalTheme
import com.example.simplecal.ui.theme.ThemeMode

// Fungsi untuk mengurai text yang diedit dan mengupdate state
private fun parseAndUpdateStateFromText(text: String, viewModel: CalculatorViewModel) {
    viewModel.updateStateFromText(text)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Calculator(
    state: CalculatorState,
    history: List<String>,
    buttonSpacing: Dp = 4.dp,
    modifier: Modifier = Modifier,
    onAction: (CalculatorAction) -> Unit,
    viewModel: CalculatorViewModel,
    onToggleScientific: () -> Unit,
    isScientificMode: Boolean = false
) {
    val themeState = LocalTheme.current
    var showThemeMenu by remember { mutableStateOf(false) }
    var isHistoryExpanded by remember { mutableStateOf(false) }
    
    // Clear history function
    val clearHistory = {
        // We'll add a ClearHistory action to the ViewModel
        onAction(CalculatorAction.ClearHistory)
    }
    
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Top App Bar with Theme Toggle
        TopAppBar(
            title = { Text("Calculator") },
            actions = {
                // Theme Toggle Button
                Box {
                    IconButton(onClick = { showThemeMenu = !showThemeMenu }) {
                        Icon(
                            imageVector = when (themeState.theme) {
                                ThemeMode.DARK -> Icons.Filled.LightMode
                                ThemeMode.LIGHT -> Icons.Filled.DarkMode
                                ThemeMode.SYSTEM -> if (isSystemInDarkTheme()) Icons.Filled.LightMode else Icons.Filled.DarkMode
                            },
                            contentDescription = "Toggle Theme"
                        )
                    }
                    
                    // Theme Selection Dropdown
                    DropdownMenu(
                        expanded = showThemeMenu,
                        onDismissRequest = { showThemeMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Light") },
                            onClick = {
                                themeState.updateTheme(ThemeMode.LIGHT)
                                showThemeMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.LightMode,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Dark") },
                            onClick = {
                                themeState.updateTheme(ThemeMode.DARK)
                                showThemeMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.DarkMode,
                                    contentDescription = null
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("System Default") },
                            onClick = {
                                themeState.updateTheme(ThemeMode.SYSTEM)
                                showThemeMenu = false
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Filled.Settings,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
                
                // Scientific Mode Toggle
                IconButton(onClick = onToggleScientific) {
                    Icon(
                        imageVector = if (isScientificMode) Icons.Filled.KeyboardReturn else Icons.Filled.Functions,
                        contentDescription = if (isScientificMode) "Basic Mode" else "Scientific Mode"
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            // History
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(if (isHistoryExpanded) 0.7f else 0.3f)
                    .padding(horizontal = 8.dp)
            ) {
                // History header with clear button
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Left side: History title and expand/collapse button
                    Row(
                        modifier = Modifier.align(Alignment.CenterStart),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "History",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 16.sp
                        )
                        
                        // Expand/Collapse button
                        IconButton(
                            onClick = { isHistoryExpanded = !isHistoryExpanded },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isHistoryExpanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                                contentDescription = if (isHistoryExpanded) "Collapse" else "Expand",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    // Right side: Clear history button
                    IconButton(
                        onClick = clearHistory,
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.CenterEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Clear,
                            contentDescription = "Clear History",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // History list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    reverseLayout = true
                ) {
                    items(history) { entry ->
                        // Split the entry into expression and result
                        val parts = entry.split(" = ")
                        val expression = parts.getOrNull(0) ?: ""
                        val result = parts.getOrNull(1) ?: ""
                        
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    // When clicked, load the result into the input
                                    onAction(CalculatorAction.LoadFromHistory(entry))
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            if (expression.isNotEmpty()) {
                                Text(
                                    text = expression,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (result.isNotEmpty()) {
                                Text(
                                    text = "= $result",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }

            // Main input field
            val focusRequester = remember { FocusRequester() }
            val textFieldValue = remember { mutableStateOf(TextFieldValue("")) }
            
            LaunchedEffect(state.number1 + (state.operation?.symbol ?: "") + state.number2) {
                textFieldValue.value = TextFieldValue(
                    text = state.number1 + (state.operation?.symbol ?: "") + state.number2,
                    selection = TextRange(textFieldValue.value.text.length)
                )
            }
            
            BasicTextField(
                value = textFieldValue.value,
                onValueChange = { newValue ->
                    textFieldValue.value = newValue
                    parseAndUpdateStateFromText(newValue.text, viewModel)
                },
                textStyle = androidx.compose.ui.text.TextStyle(
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .focusRequester(focusRequester),
                singleLine = true,
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary)
            )

            // Toggle and Delete buttons row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Scientific Toggle Button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onToggleScientific() }
                        .border(
                            width = 1.dp,
                            color = if (isScientificMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Sci",
                        color = if (isScientificMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp
                    )
                }

                // Delete button
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .clickable { onAction(CalculatorAction.Delete) }
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.error,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Backspace,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Calculator buttons grid
            val configuration = LocalConfiguration.current
            val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
            val buttonRows = if (isPortrait) 5 else 4
            val buttonCols = if (isPortrait) 4 else 5
            val buttons = listOf(
                listOf("C", "%", "()", "/"),
                listOf("7", "8", "9", "x"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("±", "0", ".", "=")
            )
            val actions = listOf(
                listOf(
                    { onAction(CalculatorAction.Clear) },
                    { onAction(CalculatorAction.Percent) },
                    { onAction(CalculatorAction.Parentheses) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Divide)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(7)) },
                    { onAction(CalculatorAction.Number(8)) },
                    { onAction(CalculatorAction.Number(9)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Multiply)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(4)) },
                    { onAction(CalculatorAction.Number(5)) },
                    { onAction(CalculatorAction.Number(6)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Subtract)) }
                ),
                listOf(
                    { onAction(CalculatorAction.Number(1)) },
                    { onAction(CalculatorAction.Number(2)) },
                    { onAction(CalculatorAction.Number(3)) },
                    { onAction(CalculatorAction.Operation(CalculatorOperation.Add)) }
                ),
                listOf(
                    { onAction(CalculatorAction.ToggleSign) },
                    { onAction(CalculatorAction.Number(0)) },
                    { onAction(CalculatorAction.Decimal) },
                    { onAction(CalculatorAction.Calculate) }
                )
            )
            for (row in 0 until buttonRows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
                ) {
                    for (col in 0 until buttonCols) {
                        val label = buttons.getOrNull(row)?.getOrNull(col) ?: ""
                        val action = actions.getOrNull(row)?.getOrNull(col) ?: {}
                        val color = when (label) {
                            "C" -> MaterialTheme.colorScheme.surfaceVariant
                            "%", "/", "x", "-", "+", "=" -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                        if (label.isNotEmpty()) {
                            CalculatorButton(
                                symbol = label,
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .weight(1f)
                                    .background(color),
                                onClick = action
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}