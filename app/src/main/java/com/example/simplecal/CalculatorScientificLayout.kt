package com.example.simplecal

import androidx.compose.runtime.Composable

@Composable
fun CalculatorScientificLayout(
    state: CalculatorState,
    history: List<String>,
    onAction: (CalculatorAction) -> Unit,
    onToggleMode: () -> Unit,
    viewModel: CalculatorViewModel
) {
    // Tetap delegasi ke basic layout (panel scientific sudah ada di sana)
    CalculatorBasicLayout(
        state = state,
        history = history,
        onAction = onAction,
        onToggleMode = onToggleMode,
        viewModel = viewModel
    )
}

// Data class untuk daftar tombol scientific (DIPERTAHANKAN agar tidak error)
data class ScientificFunction(
    val symbol: String,
    val action: CalculatorAction,
    val description: String = ""
)
