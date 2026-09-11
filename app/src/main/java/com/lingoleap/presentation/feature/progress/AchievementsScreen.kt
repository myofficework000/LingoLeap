package com.lingoleap.presentation.feature.progress

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState

data class AchievementsState(val unlockedIds: Set<String> = emptySet()) : UiState
sealed interface AchievementsEvent : UiEvent { data object Back : AchievementsEvent }

@Composable fun AchievementsScreen(state: AchievementsState = AchievementsState(), onEvent: (AchievementsEvent) -> Unit) { AchievementGrid(state.unlockedIds, onEvent) }
@Composable fun AchievementGrid(unlockedIds: Set<String>, onEvent: (AchievementsEvent) -> Unit) = Unit
