package com.lingoleap.presentation.feature.progress

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Achievement
import com.lingoleap.domain.usecase.GetAchievementsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class AchievementsState(val achievements: List<Achievement> = emptyList()) : UiState
sealed interface AchievementsEvent : UiEvent { data object Back : AchievementsEvent }

@HiltViewModel
class AchievementsViewModel @Inject constructor(getAchievements: GetAchievementsUseCase) : ViewModel() {
    val state = getAchievements.observe().map(::AchievementsState)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AchievementsState())
}

@Composable
fun AchievementsScreen(onEvent: (AchievementsEvent) -> Unit, viewModel: AchievementsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    AchievementGrid(state.achievements)
}

@Composable
fun AchievementGrid(achievements: List<Achievement>) = LazyColumn(
    modifier = Modifier.fillMaxSize().padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp),
) {
    items(achievements, key = { it.id }) { achievement ->
        Card(Modifier.fillMaxWidth()) { Column(Modifier.padding(16.dp)) {
            Text(if (achievement.isUnlocked) "Unlocked: ${achievement.title}" else "Locked: ${achievement.title}", style = MaterialTheme.typography.titleMedium)
            Text(achievement.description, style = MaterialTheme.typography.bodyMedium)
        } }
    }
}
