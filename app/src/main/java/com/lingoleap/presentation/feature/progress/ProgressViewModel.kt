package com.lingoleap.presentation.feature.progress

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.core.mvi.UiEvent
import com.lingoleap.domain.usecase.GetLearnerProgressUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed interface ProgressEffect {

    data object NavigateToAchievements : ProgressEffect
}

@HiltViewModel
class ProgressViewModel @Inject constructor(private val getLearnerProgressUseCase: GetLearnerProgressUseCase): ViewModel() {

    private val _state = MutableStateFlow(ProgressState())
    val state: StateFlow<ProgressState> = _state.asStateFlow()


    private val _effect = Channel<ProgressEffect>(Channel.BUFFERED)

    val effect: Flow<ProgressEffect> =
        _effect.receiveAsFlow()

    init {
        loadProgress()
    }

    fun onEvent(event: ProgressEvent){
        when(event){

            ProgressEvent.Refresh -> {
                loadProgress()
            }

            ProgressEvent.OpenAchievements -> {
                viewModelScope.launch {
                    _effect.send(
                        ProgressEffect.NavigateToAchievements
                    )
                }
            }

            is ProgressEvent.SelectRange -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedRange = event.range
                    )
                }
            }
        }
    }

    private fun loadProgress() {
        viewModelScope.launch {
            val progress = getLearnerProgressUseCase()

            _state.update {
                it.copy(
                    progress = progress,

                    weeklyXp = listOf(35,55,40,20,45,70,50)
                )
            }
        }
    }
}