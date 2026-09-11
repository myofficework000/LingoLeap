package com.lingoleap.presentation.feature.practice

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.VocabularyWord

data class PracticeState(val words: List<VocabularyWord> = emptyList(), val selectedMode: PracticeMode = PracticeMode.WORD_MATCH) : UiState
enum class PracticeMode { WORD_MATCH, LISTENING, FILL_BLANK }
sealed interface PracticeEvent : UiEvent { data class SelectMode(val mode: PracticeMode) : PracticeEvent; data class Submit(val answer: String) : PracticeEvent; data object Next : PracticeEvent }

@Composable fun PracticeScreen(state: PracticeState = PracticeState(), onEvent: (PracticeEvent) -> Unit) { PracticeModeTabs(state.selectedMode, onEvent) }
@Composable fun PracticeModeTabs(selectedMode: PracticeMode, onEvent: (PracticeEvent) -> Unit) = Unit
