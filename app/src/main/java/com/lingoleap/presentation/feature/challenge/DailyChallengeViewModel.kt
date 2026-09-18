package com.lingoleap.presentation.feature.challenge

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetDailyChallengesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DailyChallengeEffect {
    data object NavigateBack : DailyChallengeEffect
    data object Finished : DailyChallengeEffect
}

@HiltViewModel
class DailyChallengeViewModel @Inject constructor(
    private val getDailyChallenges: GetDailyChallengesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(DailyChallengeState())
    val state = _state.asStateFlow()
    private val _effect = Channel<DailyChallengeEffect>(Channel.BUFFERED)
    val effect: Flow<DailyChallengeEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            runCatching { getDailyChallenges() }
                .onSuccess { challenges -> _state.value = DailyChallengeState(isLoading = false, challenges = challenges) }
                .onFailure { error -> _state.value = DailyChallengeState(isLoading = false, error = error.message ?: "Unable to load daily challenges") }
        }
    }

    fun onEvent(event: DailyChallengeEvent) {
        when (event) {
            DailyChallengeEvent.Back -> viewModelScope.launch { _effect.send(DailyChallengeEffect.NavigateBack) }
            is DailyChallengeEvent.SelectAnswer -> selectAnswer(event.answer)
            DailyChallengeEvent.Next -> nextChallenge()
        }
    }

    private fun selectAnswer(answer: String) {
        val challenge = _state.value.currentChallenge ?: return
        if (_state.value.selectedAnswer != null) return
        _state.update {
            it.copy(
                selectedAnswer = answer,
                isAnswerCorrect = answer == challenge.correctAnswer,
                earnedXp = it.earnedXp + if (answer == challenge.correctAnswer) challenge.xpReward else 0,
            )
        }
    }

    private fun nextChallenge() {
        if (_state.value.selectedAnswer == null) return
        if (_state.value.isLastChallenge) {
            viewModelScope.launch { _effect.send(DailyChallengeEffect.Finished) }
        } else {
            _state.update { it.copy(currentIndex = it.currentIndex + 1, selectedAnswer = null, isAnswerCorrect = null) }
        }
    }
}
