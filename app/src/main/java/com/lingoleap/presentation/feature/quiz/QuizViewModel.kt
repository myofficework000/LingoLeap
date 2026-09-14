package com.lingoleap.presentation.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lingoleap.domain.usecase.GetLessonQuizUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val getLessonQuizUseCase: GetLessonQuizUseCase
): ViewModel() {

    private val _quizState = MutableStateFlow(QuizState())
    val quizState : StateFlow<QuizState> = _quizState.asStateFlow()

    fun loadQuiz(lessonId: String){
        viewModelScope.launch {

            val quiz = getLessonQuizUseCase(lessonId)
            _quizState.update { currentState ->
                currentState.copy(
                    quiz = quiz,
                    selectedAnswer = null,
                    isAnswerChecked = false
                )
            }
        }
    }

    fun onEvent(event: QuizEvent){
        when(event){
            is QuizEvent.SelectAnswer -> {
                _quizState.update { currentState ->
                    currentState.copy(
                        selectedAnswer = event.answer
                    )
                }
            }

            is QuizEvent.CheckAnswer -> {
                if (_quizState.value.selectedAnswer != null) {
                    _quizState.update { currentState ->
                        currentState.copy(
                            isAnswerChecked = true
                        )
                    }
                }
            }

            is QuizEvent.Next -> {
                _quizState.update { currentState ->
                    currentState.copy(
                        selectedAnswer = null,
                        isAnswerChecked = false
                    )
                }
            }
        }
    }
}
