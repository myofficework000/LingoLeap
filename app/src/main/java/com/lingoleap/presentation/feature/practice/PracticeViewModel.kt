package com.lingoleap.presentation.feature.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetCoursesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.lingoleap.domain.usecase.ObserveUserPreferencesUseCase
import kotlinx.coroutines.flow.first

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase,
    private val observeUserPreferencesUseCase: ObserveUserPreferencesUseCase
) : ViewModel() {

    private val _state =
        MutableStateFlow(PracticeState())

    val state: StateFlow<PracticeState> =
        _state.asStateFlow()

    fun loadPractice(
        lessonId: String
    ) {
        viewModelScope.launch {

            val courses =
                getCoursesUseCase()

            val lesson =
                courses
                    .flatMap { it.lessons }
                    .firstOrNull {
                        it.id == lessonId
                    }

            val preferences =
                observeUserPreferencesUseCase()
                    .first()

            _state.value = PracticeState(
                words = lesson?.words ?: emptyList(),
                sourceLanguageId = preferences.sourceLanguageId,
            )
        }
    }

    fun onEvent(
        event: PracticeEvent
    ) {

        when (event) {

            is PracticeEvent.SelectMode -> {

                _state.update {
                    it.copy(
                        selectedMode =
                            event.mode
                    )
                }
            }

            is PracticeEvent.SelectLeft -> {

                _state.update {
                    it.copy(
                        selectedLeftWord =
                            event.word,

                        selectedRightWord =
                            null,

                        isWrongMatch =
                            false
                    )
                }
            }

            is PracticeEvent.SelectRight -> {

                _state.update {
                    it.copy(
                        selectedRightWord =
                            event.word,

                        isWrongMatch =
                            false
                    )
                }

                checkMatch()
            }

            is PracticeEvent.SelectListeningAnswer -> {

                if (
                    !_state.value
                        .isListeningAnswerChecked
                ) {

                    _state.update {
                        it.copy(
                            selectedListeningAnswer =
                                event.answer
                        )
                    }
                }
            }

            PracticeEvent.CheckListeningAnswer -> {

                val selectedAnswer =
                    _state.value
                        .selectedListeningAnswer

                if (
                    selectedAnswer != null &&
                    !_state.value
                        .isListeningAnswerChecked
                ) {

                    _state.update {
                        it.copy(
                            isListeningAnswerChecked =
                                true
                        )
                    }
                }
            }

            PracticeEvent.NextListeningWord -> {

                moveToNextListeningWord()
            }

            PracticeEvent.Next -> {

                _state.update {
                    it.copy(
                        selectedMode =
                            PracticeMode.LISTENING,

                        selectedLeftWord =
                            null,

                        selectedRightWord =
                            null,

                        isWrongMatch =
                            false,

                        listeningWordIndex =
                            0,

                        selectedListeningAnswer =
                            null,

                        isListeningAnswerChecked =
                            false
                    )
                }
            }

            is PracticeEvent.FillBlankAnswerChanged -> {
                if (!_state.value.isFillBlankChecked) {
                    _state.update {
                        it.copy(
                            fillBlankAnswer = event.answer
                        )
                    }
                }
            }

            PracticeEvent.CheckFillBlankAnswer -> {
                if (
                    _state.value.fillBlankAnswer.isNotBlank() &&
                    !_state.value.isFillBlankChecked
                ) {
                    _state.update {
                        it.copy(
                            isFillBlankChecked = true
                        )
                    }
                }
            }

            PracticeEvent.NextFillBlankWord -> {
                moveToNextFillBlankWord()
            }
        }
    }

    private fun checkMatch() {

        val left =
            _state.value
                .selectedLeftWord

        val right =
            _state.value
                .selectedRightWord

        if (
            left == null ||
            right == null
        ) {
            return
        }

        if (left.id == right.id) {

            _state.update {
                it.copy(
                    matchedWordIds =
                        it.matchedWordIds +
                                left.id,

                    selectedLeftWord =
                        null,

                    selectedRightWord =
                        null,

                    isWrongMatch =
                        false
                )
            }

        } else {

            _state.update {
                it.copy(
                    isWrongMatch =
                        true
                )
            }
        }
    }

    private fun moveToNextListeningWord() {

        val currentState =
            _state.value

        val nextIndex =
            currentState
                .listeningWordIndex + 1

        if (
            nextIndex <
            currentState.words.size
        ) {

            _state.update {
                it.copy(
                    listeningWordIndex =
                        nextIndex,

                    selectedListeningAnswer =
                        null,

                    isListeningAnswerChecked =
                        false
                )
            }

        } else {

            _state.update {
                it.copy(
                    selectedMode = PracticeMode.FILL_BLANK,

                    selectedListeningAnswer = null,
                    isListeningAnswerChecked = false,

                    fillBlankWordIndex = 0,
                    fillBlankAnswer = "",
                    isFillBlankChecked = false
                )
            }
        }
    }

    private fun moveToNextFillBlankWord() {

        val currentState =
            _state.value

        val nextIndex =
            currentState.fillBlankWordIndex + 1

        if (nextIndex < currentState.words.size) {

            _state.update {
                it.copy(
                    fillBlankWordIndex = nextIndex,
                    fillBlankAnswer = "",
                    isFillBlankChecked = false
                )
            }

        } else {

            _state.update {
                it.copy(
                    fillBlankAnswer = "",
                    isFillBlankChecked = false,
                    isPracticeCompleted = true
                )
            }
        }
    }
}
