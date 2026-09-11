package com.lingoleap.presentation.feature.language

import androidx.compose.runtime.Composable
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.core.mvi.UiState
import com.lingoleap.domain.model.Language
import com.lingoleap.domain.model.LanguagePair

data class LanguagePickerState(val languages: List<Language> = emptyList(), val pairs: List<LanguagePair> = emptyList(), val selectedSourceId: String? = null, val selectedTargetId: String? = null) : UiState
sealed interface LanguagePickerEvent : UiEvent { data class SelectSource(val languageId: String) : LanguagePickerEvent; data class SelectTarget(val languageId: String) : LanguagePickerEvent; data object Confirm : LanguagePickerEvent }

@Composable fun LanguagePickerScreen(state: LanguagePickerState = LanguagePickerState(), onEvent: (LanguagePickerEvent) -> Unit) { LanguageGrid(state.languages, onEvent) }
@Composable fun LanguageGrid(languages: List<Language>, onEvent: (LanguagePickerEvent) -> Unit) = Unit
