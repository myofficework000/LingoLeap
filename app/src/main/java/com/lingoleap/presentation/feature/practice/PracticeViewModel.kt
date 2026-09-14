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

@HiltViewModel
class PracticeViewModel @Inject constructor(
    private val getCoursesUseCase: GetCoursesUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(PracticeState())
    val state: StateFlow<PracticeState> = _state.asStateFlow()

    fun loadPractice(lessonId: String) {
        viewModelScope.launch {

            val courses = getCoursesUseCase()

            val lesson = courses
                .flatMap { it.lessons }
                .firstOrNull { it.id == lessonId }

            _state.update {
                it.copy(
                    words = lesson?.words ?: emptyList()
                )
            }
        }
    }

    fun onEvent(event: PracticeEvent) {

        when (event) {

            is PracticeEvent.SelectMode -> {
                _state.update {
                    it.copy(
                        selectedMode = event.mode
                    )
                }
            }

            is PracticeEvent.SelectLeft -> {
                _state.update {
                    it.copy(
                        selectedLeftWord = event.word,
                        selectedRightWord = null,
                        isWrongMatch = false
                    )
                }
            }

            is PracticeEvent.SelectRight -> {
                _state.update {
                    it.copy(
                        selectedRightWord = event.word,
                        isWrongMatch = false
                    )
                }

                checkMatch()
            }

            PracticeEvent.Next -> {
                _state.update {
                    it.copy(
                        selectedMode = PracticeMode.LISTENING,
                        selectedLeftWord = null,
                        selectedRightWord = null,
                        isWrongMatch = false
                    )
                }
            }
        }
    }

    private fun checkMatch() {

        val left = _state.value.selectedLeftWord
        val right = _state.value.selectedRightWord

        if (left == null || right == null) return

        if (left.id == right.id) {

            _state.update {
                it.copy(
                    matchedWordIds = it.matchedWordIds + left.id,
                    selectedLeftWord = null,
                    selectedRightWord = null,
                    isWrongMatch = false
                )
            }

        } else {

            _state.update {
                it.copy(
                    isWrongMatch = true
                )
            }
        }
    }
}