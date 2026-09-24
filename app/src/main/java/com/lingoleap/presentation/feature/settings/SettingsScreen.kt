package com.lingoleap.presentation.feature.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.ResetLearningProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val resetProgress: ResetLearningProgressUseCase) : ViewModel() { fun reset(onDone: () -> Unit) = viewModelScope.launch { resetProgress(); onDone() } }
@Composable
fun SettingsRoute(onBack: () -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    var showConfirm by remember { mutableStateOf(false) }
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Offline settings", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp)); Text("Course content is bundled as JSON. Progress and your review deck stay on this device until cloud backup is added.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp)); Text("Learning data", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text("Resetting clears lessons, XP, streak, challenges, and review words. Your selected language stays unchanged.")
        Spacer(Modifier.height(12.dp)); Button(onClick = { showConfirm = true }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) { Text("Reset local progress") }
        Spacer(Modifier.weight(1f)); OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
    if (showConfirm) AlertDialog(onDismissRequest = { showConfirm = false }, title = { Text("Reset progress?") }, text = { Text("This cannot be undone on this device.") }, confirmButton = { TextButton(onClick = { viewModel.reset { showConfirm = false; onBack() } }) { Text("Reset") } }, dismissButton = { TextButton(onClick = { showConfirm = false }) { Text("Cancel") } })
}
