package com.code4galaxy.vaaniverse4u.presentation.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.code4galaxy.vaaniverse4u.domain.usecase.ObserveUserPreferencesUseCase
import com.code4galaxy.vaaniverse4u.domain.usecase.DeleteCloudBackupUseCase
import com.code4galaxy.vaaniverse4u.domain.usecase.ResetLearningProgressUseCase
import com.code4galaxy.vaaniverse4u.domain.usecase.SaveAccessibilitySettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val textScale: Float = 1f,
    val highContrastEnabled: Boolean = false,
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeUserPreferences: ObserveUserPreferencesUseCase,
    private val saveAccessibilitySettings: SaveAccessibilitySettingsUseCase,
    private val resetProgress: ResetLearningProgressUseCase,
    private val deleteCloudBackup: DeleteCloudBackupUseCase,
) : ViewModel() {
    val state = observeUserPreferences()
        .map { SettingsState(it.textScale, it.highContrastEnabled) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SettingsState())

    fun updateTextScale(textScale: Float) = viewModelScope.launch {
        saveAccessibilitySettings(textScale, state.value.highContrastEnabled)
    }

    fun updateHighContrast(enabled: Boolean) = viewModelScope.launch {
        saveAccessibilitySettings(state.value.textScale, enabled)
    }

    fun reset(onDone: () -> Unit) = viewModelScope.launch {
        resetProgress()
        onDone()
    }

    fun deleteCloudBackup(onResult: (Boolean) -> Unit) = viewModelScope.launch {
        onResult(runCatching { deleteCloudBackup() }.isSuccess)
    }
}

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    SettingsScreen(
        state = state,
        onTextScaleChange = viewModel::updateTextScale,
        onHighContrastChange = viewModel::updateHighContrast,
        onReset = { viewModel.reset(onBack) },
        onDeleteCloudBackup = viewModel::deleteCloudBackup,
        onBack = onBack,
    )
}

@Composable
private fun SettingsScreen(
    state: SettingsState,
    onTextScaleChange: (Float) -> Unit,
    onHighContrastChange: (Boolean) -> Unit,
    onReset: () -> Unit,
    onDeleteCloudBackup: ((Boolean) -> Unit) -> Unit,
    onBack: () -> Unit,
) {
    var showConfirm by remember { mutableStateOf(false) }
    var showCloudConfirm by remember { mutableStateOf(false) }
    var cloudBackupStatus by remember { mutableStateOf<String?>(null) }

    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.semantics { heading() },
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Your course content and progress stay available offline.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(18.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Accessibility",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() },
                )
            }
            item {
                Text(
                    text = "VaaniVerse4U follows your device font size. Use these controls to make in-app text easier to read.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            item {
                TextScaleCard(
                    selectedScale = state.textScale,
                    onSelect = onTextScaleChange,
                )
            }
            item {
                HighContrastCard(
                    enabled = state.highContrastEnabled,
                    onEnabledChange = onHighContrastChange,
                )
            }
            item {
                Text(
                    text = "Privacy & cloud backup",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() },
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Delete cloud backup", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "VaaniVerse4U uses an anonymous Firebase identity to back up learning progress. Delete it here without deleting local lessons or progress.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        cloudBackupStatus?.let { status ->
                            Spacer(Modifier.height(10.dp))
                            Text(status, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(14.dp))
                        OutlinedButton(onClick = { showCloudConfirm = true }) {
                            Text("Delete cloud backup")
                        }
                    }
                }
            }
            item {
                Text(
                    text = "Learning data",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.semantics { heading() },
                )
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                ) {
                    Column(Modifier.padding(18.dp)) {
                        Text(
                            text = "Reset local progress",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Clears completed lessons, XP, streak, challenges, and review words. Your language selection remains unchanged.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { showConfirm = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ),
                        ) {
                            Text("Reset local progress")
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Back")
        }
    }

    if (showConfirm) {
        AlertDialog(
            onDismissRequest = { showConfirm = false },
            title = { Text("Reset progress?") },
            text = { Text("This clears the learning progress stored on this device. This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirm = false
                    onReset()
                }) { Text("Reset") }
            },
            dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } },
        )
    }

    if (showCloudConfirm) {
        AlertDialog(
            onDismissRequest = { showCloudConfirm = false },
            title = { Text("Delete cloud backup?") },
            text = { Text("This permanently deletes the anonymous Firebase identity and cloud progress backup. Learning data stored locally on this device will remain.") },
            confirmButton = {
                TextButton(onClick = {
                    showCloudConfirm = false
                    onDeleteCloudBackup { deleted ->
                        cloudBackupStatus = if (deleted) {
                            "Cloud backup deleted. Your local learning data is unchanged."
                        } else {
                            "Cloud backup could not be deleted. Check your connection and try again."
                        }
                    }
                }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { showCloudConfirm = false }) { Text("Cancel") } },
        )
    }
}

@Composable
private fun TextScaleCard(selectedScale: Float, onSelect: (Float) -> Unit) {
    val choices = listOf(
        1f to "Default",
        1.15f to "Large",
        1.3f to "Extra large",
    )
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp)) {
            Text("Text size", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Choose a comfortable reading size.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            choices.forEach { (scale, label) ->
                val selected = selectedScale == scale
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = selected,
                            onClick = { onSelect(scale) },
                            role = Role.RadioButton,
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = selected, onClick = null)
                    Spacer(Modifier.width(8.dp))
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
private fun HighContrastCard(enabled: Boolean, onEnabledChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics {
                role = Role.Switch
                stateDescription = if (enabled) "On" else "Off"
            },
        onClick = { onEnabledChange(!enabled) },
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f)) {
                Text("High contrast", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Use stronger color contrast for text, controls, and focusable surfaces.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Switch(checked = enabled, onCheckedChange = null)
        }
    }
}
