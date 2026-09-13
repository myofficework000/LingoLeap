package com.lingoleap.presentation.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetLanguagePairsUseCase
import com.lingoleap.domain.usecase.GetSupportedLanguagesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class LanguagePickerViewModel @Inject constructor(
    private val getSupportedLanguagesUseCase: GetSupportedLanguagesUseCase,
    private val getLanguagePairsUseCase: GetLanguagePairsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LanguagePickerState())

    val state: StateFlow<LanguagePickerState> =
        _state.asStateFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {

        viewModelScope.launch {

            val languages = getSupportedLanguagesUseCase()
            val pairs = getLanguagePairsUseCase()

            _state.update { currentState ->
                currentState.copy(
                    languages = languages,
                    pairs = pairs
                )
            }
        }
    }

    fun onEvent(event: LanguagePickerEvent) {

        when (event) {

            is LanguagePickerEvent.SelectSource -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedSourceId = event.languageId,
                        selectedTargetId = null
                    )
                }
            }

            is LanguagePickerEvent.SelectTarget -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedTargetId = event.languageId
                    )
                }
            }

            LanguagePickerEvent.Confirm -> {
                // navigation later
            }
        }
    }
}