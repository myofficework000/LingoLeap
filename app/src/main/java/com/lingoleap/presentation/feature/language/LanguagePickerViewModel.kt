package com.lingoleap.presentation.feature.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetLanguagePairsUseCase
import com.lingoleap.domain.usecase.GetSupportedLanguagesUseCase
import com.lingoleap.domain.usecase.SaveLanguagePairUseCase
import com.lingoleap.domain.usecase.SetActiveLanguagePairUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LanguagePickerViewModel @Inject constructor(
    private val getSupportedLanguagesUseCase: GetSupportedLanguagesUseCase,
    private val getLanguagePairsUseCase: GetLanguagePairsUseCase,
    private val saveLanguagePair: SaveLanguagePairUseCase,
    private val setActiveLanguagePair: SetActiveLanguagePairUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(LanguagePickerState())

    val state: StateFlow<LanguagePickerState> =
        _state.asStateFlow()
    private val _effect = Channel<LanguagePickerEffect>(Channel.BUFFERED)
    val effect: Flow<LanguagePickerEffect> = _effect.receiveAsFlow()

    init {
        loadLanguages()
    }

    private fun loadLanguages() {

        viewModelScope.launch {

            val (languages, pairs) = coroutineScope {
                val languages = async { getSupportedLanguagesUseCase() }
                val pairs = async { getLanguagePairsUseCase() }
                languages.await() to pairs.await()
            }

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
                val sourceId = _state.value.selectedSourceId ?: return
                val targetId = _state.value.selectedTargetId ?: return
                val pair = _state.value.pairs.firstOrNull {
                    it.sourceLanguageId == sourceId && it.targetLanguageId == targetId
                } ?: return
                viewModelScope.launch {
                    saveLanguagePair(sourceId, targetId, pair.id)
                    setActiveLanguagePair(pair.id)
                    _effect.send(LanguagePickerEffect.Confirmed)
                }
            }
        }
    }
}

sealed interface LanguagePickerEffect { data object Confirmed : LanguagePickerEffect }
