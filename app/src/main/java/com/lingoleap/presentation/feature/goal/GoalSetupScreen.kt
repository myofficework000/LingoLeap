package com.lingoleap.presentation.feature.goal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.SaveDailyGoalUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GoalSetupState(val selectedLessons: Int = 1)

@HiltViewModel
class GoalSetupViewModel @Inject constructor(private val saveDailyGoal: SaveDailyGoalUseCase) : ViewModel() {
    private val _state = MutableStateFlow(GoalSetupState())
    val state = _state.asStateFlow()
    fun select(lessons: Int) { _state.value = GoalSetupState(lessons) }
    fun save(onSaved: () -> Unit) = viewModelScope.launch { saveDailyGoal(_state.value.selectedLessons); onSaved() }
}

@Composable
fun GoalSetupRoute(onFinished: () -> Unit, viewModel: GoalSetupViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    GoalSetupScreen(state, viewModel::select) { viewModel.save(onFinished) }
}

@Composable
private fun GoalSetupScreen(state: GoalSetupState, onSelect: (Int) -> Unit, onContinue: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("Set a daily goal", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        Text("A small, repeatable goal makes the offline course easier to finish.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(28.dp))
        listOf(1 to "Easy start · about 5 minutes", 2 to "Steady progress · about 10 minutes", 3 to "Focused learning · about 15 minutes").forEach { (lessons, label) ->
            Card(onClick = { onSelect(lessons) }, modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (state.selectedLessons == lessons) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(Modifier.padding(18.dp)) { Text("$lessons lesson${if (lessons == 1) "" else "s"} a day", fontWeight = FontWeight.Bold); Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
        }
        Spacer(Modifier.height(24.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) { Text("Start my course") }
    }
}
