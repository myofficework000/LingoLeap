package com.lingoleap.presentation.feature.progress

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.LearnerProgress

data class ProgressState(val progress: LearnerProgress? = null, val weeklyXp: List<Int> = emptyList()) : UiState
sealed interface ProgressEvent : UiEvent { data object Refresh : ProgressEvent; data object OpenAchievements : ProgressEvent }

@Composable fun ProgressScreen(state: ProgressState = ProgressState(), onEvent: (ProgressEvent) -> Unit) { ProgressSummary(progress = state.progress, weeklyXp = state.weeklyXp, onEvent = onEvent) }
@Composable fun ProgressSummary(progress: LearnerProgress?, weeklyXp: List<Int>, onEvent: (ProgressEvent) -> Unit) = Unit
